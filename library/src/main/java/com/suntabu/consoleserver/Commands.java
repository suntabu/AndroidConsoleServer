package com.suntabu.consoleserver;

import android.app.Activity;
import android.util.Log;

import com.suntabu.ACS;
import com.suntabu.anno.CommandProcessor;
import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

import com.suntabu.anno.Command;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class Commands {
    private static final String TAG = "Commands";
    private CommandProcessor processor;


    public Commands() {
        processor = new CommandProcessor(this);
    }


    public NanoHTTPD.Response handle(String command) {
        ConsoleContent.append("> " + command);


        return processor.handle(command);
    }


    @Command(value = "clear", description = "clear console")
    private void clearConsole(String[] args) {
        ConsoleContent.clear();
    }

    @Command(value = "help", description = "command info for help")
    private void help(String[] args) {
        processor.help();
    }


    @Command(value = "check", description = "check methods or fields")
    private NanoHTTPD.Response checkVar(String[] args) {
        String result = "";
        try {
            for (int i = 0; i < args.length; i++) {
                args[i].trim();
                Log.e(TAG, "checkVar:" + args[i].trim());
            }
            for (Map.Entry<String, Activity> entry : ACS.clazzMap.entrySet()) {
                if (entry.getKey().contains(args[0])) {
                    Class clazz = Class.forName(entry.getKey());
                    String last = args[args.length - 1];
//                    String lastTemp = last.substring(last.indexOf("(") + 1, last.indexOf(")"));
                    if (Arrays.asList(args).contains("-m")) {
                        java.lang.reflect.Method method = clazz.getDeclaredMethod(args[2]);

                    } else {
                        Field field = clazz.getDeclaredField(args[1]);
                        field.setAccessible(true);
//                        result = processField(field.get(entry.getValue()), lastTemp);
                        Object o = field.get(entry.getValue());
                        if (o instanceof String) {
                            result = args[1] + " = " + o;
                        } else {
                            result = args[1] + " = " + o.toString();
                        }

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        ConsoleContent.append("\n" + result + "\n");
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeTypes().get("md"), ConsoleContent.Log());
    }


    @Command(value = "lm", description = "list module names")
    public NanoHTTPD.Response listLogModule(String[] args) {
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String, LogModule> entry : list) {
            names += "\t\t" + entry.getKey() + "\n";
        }


        ConsoleContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }


    @Command(value = "pull", description = "pull module log")
    public NanoHTTPD.Response pullLogModule(String[] args) {


        try {
            if (args.length >= 1) {
                String moduleName = args[0];
                String filePath = LogManager.getInstance().getModuleDic().get(moduleName).getFilePath();
                File file = new File(filePath);

                FileInputStream fin = new FileInputStream(file);
                NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), "log/pull?file=" + moduleName);
                response.addHeader("Content-disposition", String.format("attachment; filename=%s", file.getName()));
                return response;
            } else {
                ConsoleContent.append("expect <module name>");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), "");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ConsoleContent.append("download error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        } catch (Exception e) {
            ConsoleContent.append("error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        }

    }


}
