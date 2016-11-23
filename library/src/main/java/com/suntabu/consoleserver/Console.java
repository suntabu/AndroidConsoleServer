package com.suntabu.consoleserver;


/**
 * Created by gouzhun on 2016/11/22.
 */

import android.util.Log;

import com.suntabu.log.LogManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public class Console {
    private static final int MAX_RECORD = 10;
    private ArrayList<String> mCommandRecord = new ArrayList<>();
    private String result;
    private Command commandHandler;

    public Console() {
        commandHandler = new Command();
    }

    public Response console_out(IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), ConsoleContent.Log());
    }


    public Response console_run(IHTTPSession session) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, UnsupportedEncodingException {
        String command = session.getParms().get("command");
        command = URLDecoder.decode(command, "UTF-8");
        if (mCommandRecord.size() >= 10) {
            mCommandRecord.remove(0);
        }
        mCommandRecord.add(command);

        return commandHandler.handle(command);

        /*String[] strings = command.split(" ");
        for (Map.Entry<String, Activity> entry : ConsoleServer.clazzMap.entrySet()) {
            if (entry.getKey().contains(strings[0])) {
                Class clazz = Class.forName(entry.getKey());
                String last = strings[strings.length - 1];
                String lastTemp = last.substring(last.indexOf("(") + 1, last.indexOf(")"));
                if (command.contains("-m")) {
                    java.lang.reflect.Method method = clazz.getDeclaredMethod(strings[1]);

                } else {
                    Field field = clazz.getDeclaredField(strings[1]);
                    field.setAccessible(true);
                    result = processField(field.get(entry.getValue()), lastTemp);
                }
            }
        }
//        String result = runCommand(command);
        ConsoleContent.LogContent.append("\n" + result + "\n");
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), ConsoleContent.Log());*/
    }

    private String processField(Object o, String... params) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String temp1 = "";
        Log.e("....", "processField: " + params[0]);
        if (params.length == 1) {
            StringBuffer sb = new StringBuffer();
            for (String s : ConsoleServer.beanList) {
                if (s.contains(params[0])) {
                    temp1 += s;
                }
            }

            Class clazz = Class.forName(temp1);
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                Field tempField = clazz.getDeclaredField(f.getName());
                tempField.setAccessible(true);
                sb.append(tempField.getName() + " = " + tempField.get(tempField.getName()) + "\n");
            }
            return new String(sb);
        }

        if (o instanceof String) {
            return (String) o;
        } else if (o instanceof List) {

        } else if (o instanceof HashMap) {

        } else if (o instanceof Array) {

        } else {
            return o.toString();
        }
        return "";
    }

    public Response console_history(IHTTPSession session) {
        int index = Integer.parseInt(session.getParms().get("index"));

        int commandInx = mCommandRecord.size() - index;
        if (commandInx > mCommandRecord.size() - 1) {
            commandInx = mCommandRecord.size() - 1;
        }

        if (commandInx < 0) {
            commandInx = 0;
        }

        String command = mCommandRecord.size() > 0 ? mCommandRecord.get(commandInx) : "";


        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), command);
    }


    public Response console_complete(IHTTPSession session) {

        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), "");
    }


    public Response log_pull(IHTTPSession session) {
        String moduleName = session.getParms().get("file");


        String filePath = LogManager.getInstance().getModuleDic().get(moduleName).getFilePath();
        File file = new File(filePath);
        try {
            ConsoleContent.append("download... " + filePath);
            FileInputStream fin = new FileInputStream(file);
            NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/octet-stream", fin, file.length());
            response.addHeader("Content-disposition", String.format("attachment; filename=%s", file.getName()));
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ConsoleContent.append("download error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        }
    }
}
