package com.suntabu.consoleserver;


/**
 * Created by gouzhun on 2016/11/22.
 */

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public class Console {
    private static final int MAX_RECORD = 10;
    private ArrayList<String> mCommandRecord = new ArrayList<>();
    private Command commandHandler;

    public Console() {
        commandHandler = new Command();
    }

    public Response console_out(IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), ConsoleContent.Log());
    }


    public Response console_run(IHTTPSession session) throws UnsupportedEncodingException {
        String command = session.getParms().get("command");
        command = URLDecoder.decode(command, "UTF-8");
        if (mCommandRecord.size() >= 10) {
            mCommandRecord.remove(0);
        }
        mCommandRecord.add(command);
        return commandHandler.handle(command);
    }

    //TODO
    private String processField(Object o, String... params) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String temp1 = "";
        if (params.length == 1) {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, ?> m : ConsoleServer.beanMap.entrySet()) {
                if (m.getKey().contains(params[0])) {
                    temp1 += m.getKey();
                }
            }

            Class clazz = Class.forName(temp1);
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                String name = f.getName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                Method m = clazz.getDeclaredMethod("get" + name);
                String value = (String) m.invoke(clazz);
                sb.append(f.getName() + " = " + value + "\n");
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


}
