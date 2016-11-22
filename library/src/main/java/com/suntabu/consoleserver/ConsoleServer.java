package com.suntabu.consoleserver;

import android.content.res.Resources;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleServer extends NanoHTTPD {

    private static final String MIME_JSON = "application/json";
    private static final String MIME_CSS = "text/css";


    public ConsoleServer(String hostname, int port) {
        super(hostname, port);
    }

    public ConsoleServer(int port) {
        super(port);
    }


    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri();
            if (uri.contains("console.css")) {
                return newFixedLengthResponse(Response.Status.OK, MIME_CSS, ConsoleContent.loadAssets("console_html/console.css"));
            } else if (uri.contains("favicon.icon")) {

            }
            Map<String, String> params = session.getParms();
            if (params.size() == 0) {
                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ConsoleContent.loadAssets("console_html/index.html"));
            } else {
                if (uri.contains("command")) {
                    String command = params.get("command");
                    String[] strings = command.split(" ");
                    Class clazz = Class.forName(strings[0]);
                    if (command.contains("-m")) {
                        java.lang.reflect.Method method = clazz.getDeclaredMethod(strings[1]);

                    } else if (command.contains("-f")) {
                        Field field = clazz.getDeclaredField("string[1]");
                        field.setAccessible(true);
//                        String result = field.get();
                    }
                }
            }





            String responseString = "";
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, responseString);

        } catch (Resources.NotFoundException nfe) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        } catch (Exception ex) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }
}
