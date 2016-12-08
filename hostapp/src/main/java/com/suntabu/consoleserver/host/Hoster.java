package com.suntabu.consoleserver.host;

import com.google.gson.Gson;
import com.suntabu.consoleserver.config.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class Hoster extends NanoHTTPD {


    static DatagramSocket socket = null;

    private HashMap<String, PacketObj> devices = new HashMap<>();

    public static void main(String[] args) throws Exception {
        try {
            final Hoster host = new Hoster();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        InetAddress serverAddress = InetAddress.getByName(Config.SERVERIP);
                        System.out.println("Client: Start connecting\n");
                        socket = new DatagramSocket(Config.SERVERPORT);
                        String url = "hello Nano";
                        byte[] buf = url.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                                serverAddress, Config.SERVERPORT);
                        socket.send(packet);
                        System.out.println("Client: sent Succeed!\n");

                        // 接收UDP广播，有的手机不支持
                        while (true) {
                            byte[] recbuf = new byte[255 * 10];
                            DatagramPacket recpacket = new DatagramPacket(recbuf, recbuf.length);
                            try {
                                socket.receive(recpacket);
                                Thread.sleep(Config.RECV_INTERVAL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            String deviceInfo = new String(recpacket.getData(), 0, recpacket.getLength());
                            System.out.println("Server: Message received: ‘" + deviceInfo + "’\n");


                            synchronized (host.devices) {
                                if (deviceInfo.contains("http")) {
                                    PacketObj po = new Gson().fromJson(deviceInfo, PacketObj.class);
                                    if (!host.devices.containsKey(po.url)) {
                                        host.devices.put(po.url, po);
                                    }
                                }
                            }


                            System.out.println("Server: IP " + recpacket.getAddress()
                                    + "’\n");

                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(Config.SEND_INTERVAL * 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        synchronized (host.devices) {
                            host.devices.clear();
                        }
                    }


                }
            }).start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    public class PacketObj {
        String url;
        String name;
        String app;
    }


    public Hoster() throws IOException {
        super(Config.HTTP_PORT);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:"+Config.HTTP_PORT+"/ \n");
    }


    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Working Devices</h1>\n";
        Map<String, String> parms = session.getParms();
        if (devices.size() == 0) {
            msg += "<p>no devices</p>";
        } else {
            for (Map.Entry<String, PacketObj> device : devices.entrySet()) {
                msg += "<a href=\"" + device.getKey() + "\" target=\"_blank\">" + device.getValue().name + " : " + device.getValue().url + "</a></br>";
            }

        }
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}