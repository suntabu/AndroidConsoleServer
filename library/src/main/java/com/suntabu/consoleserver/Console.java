package com.suntabu.consoleserver;


/**
 * Created by gouzhun on 2016/11/22.
 */

import android.app.Activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public class Console {
    private static final int MAX_RECORD = 10;
    private ArrayList<String> mCommandRecord = new ArrayList<>();
    private String result;


    public Response console_out(IHTTPSession session) {
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), ConsoleContent.LogContent);
    }


    public Response console_run(IHTTPSession session) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        String command = session.getParms().get("command");
        if (mCommandRecord.size() >= 10) {
            mCommandRecord.remove(0);
        }
        mCommandRecord.add(command);
        String[] strings = command.split(" ");
        Class clazz = Class.forName(strings[0]);
        for (Map.Entry<String, Activity> entry : ConsoleServer.clazzMap.entrySet()) {
            if (entry.getKey().contains(strings[0])) {
                if (command.contains("-m")) {
                    java.lang.reflect.Method method = clazz.getDeclaredMethod(strings[1]);

                } else if (command.contains("-f")) {
                    Field field = clazz.getDeclaredField("string[1]");
                    field.setAccessible(true);
                    result = (String) field.get(entry.getValue());
                }
            }
        }
//        String result = runCommand(command);
        ConsoleContent.LogContent += "\n" + result + "\n";
        return newFixedLengthResponse(Response.Status.OK, mimeTypes().get("md"), ConsoleContent.LogContent);
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


    private String runCommand(String command) {

        return "run :" + command;
    }

}
