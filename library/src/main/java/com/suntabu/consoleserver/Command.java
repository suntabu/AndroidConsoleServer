package com.suntabu.consoleserver;

import android.app.Activity;

import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class Command {


    public NanoHTTPD.Response handle(String command) {
        ConsoleContent.LogContent.append(" > " + command);
        String[] strings = command.split(" ");

        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase("clear")) {

            } else if (strings[0].equalsIgnoreCase("lm")) {
                return listLogModule();
            } else if (strings[0].equalsIgnoreCase("help")) {

            } else if (strings[0].equalsIgnoreCase("pull")) {

            } else if (strings[0].equalsIgnoreCase("push")) {

            } else if (strings[0].equalsIgnoreCase("ck")) {
                return checkVar(command);
            }

        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }

    private String result;

    private NanoHTTPD.Response checkVar(String command) {
        try {
            command = command.trim();
            String[] strings = command.split(" ");
            for (Map.Entry<String, Activity> entry : ConsoleServer.clazzMap.entrySet()) {
                if (entry.getKey().contains(strings[1])) {
                    Class clazz = Class.forName(entry.getKey());
                    String last = strings[strings.length - 1];
                    String lastTemp = last.substring(last.indexOf("(") + 1, last.indexOf(")"));
                    if (command.contains("-m")) {
                        java.lang.reflect.Method method = clazz.getDeclaredMethod(strings[2]);

                    } else {
                        Field field = clazz.getDeclaredField(strings[2]);
                        field.setAccessible(true);
//                        result = processField(field.get(entry.getValue()), lastTemp);
                        Object o = field.get(entry.getValue());
                        if (o instanceof String) {
                            result = (String) o;
                        } else if (o instanceof List) {

                        } else if (o instanceof HashMap) {

                        } else if (o instanceof Array) {

                        } else {
                            result = o.toString();
                        }

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        ConsoleContent.LogContent.append("\n" + result + "\n");
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeTypes().get("md"), ConsoleContent.Log());
    }

    public NanoHTTPD.Response listLogModule() {
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String, LogModule> entry : list) {
            names += "\t\t" + entry.getKey() + "\n";
        }


        ConsoleContent.LogContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }


    public void pullLog(String moduleName) {

    }

}
