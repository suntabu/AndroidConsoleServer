package com.suntabu.consoleserver;

import android.app.Activity;
import android.util.Log;

import com.suntabu.ACS;
import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String TAG = "Command";

    public NanoHTTPD.Response handle(String command){
        ConsoleContent.append("> " + command);
        String[] strings = command.split(" ");

        if (strings.length >0){
            if (strings[0].equalsIgnoreCase("clear")){
                ConsoleContent.clear();
            }else if(strings[0].equalsIgnoreCase("lm")){
                return listLogModule();
            }else if(strings[0].equalsIgnoreCase("help")){

            }else if(strings[0].equalsIgnoreCase("pull")){
                if (strings.length >= 2){
                    return pullLogModule(strings[1]);
                }else{
                    ConsoleContent.append("expect <module name>");
                }
            }else if(strings[0].equalsIgnoreCase("push")){

            } else if (strings[0].equalsIgnoreCase("check")) {
                return checkVar(command);
            } else {
                ConsoleContent.append("not found " + strings[0]);
            }

        } else {
            ConsoleContent.append("nothing to show...");
        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }

    private String result;


    private NanoHTTPD.Response checkVar(String command) {
        try {
            command = command.trim();
            String[] strings = command.split(" ");
            for (int i = 0; i < strings.length; i++) {
                strings[i].trim();
                Log.e(TAG, "checkVar:" + strings[i].trim());
            }
            for (Map.Entry<String, Activity> entry : ACS.clazzMap.entrySet()) {
                if (entry.getKey().contains(strings[1])) {
                    Class clazz = Class.forName(entry.getKey());
                    String last = strings[strings.length - 1];
//                    String lastTemp = last.substring(last.indexOf("(") + 1, last.indexOf(")"));
                    if (command.contains("-m")) {
                        java.lang.reflect.Method method = clazz.getDeclaredMethod(strings[2]);

                    } else {
                        Field field = clazz.getDeclaredField(strings[2]);
                        field.setAccessible(true);
//                        result = processField(field.get(entry.getValue()), lastTemp);
                        Object o = field.get(entry.getValue());
                        if (o instanceof String) {
                            result = strings[2] + " = " + o;
                        } else {
                            result = strings[2] + " = " + o.toString();
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

    public NanoHTTPD.Response listLogModule() {
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String,LogModule> entry: list){
            names +="\t\t"+ entry.getKey()+"\n";
        }


        ConsoleContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),ConsoleContent.Log());
    }


    public NanoHTTPD.Response pullLogModule(String moduleName){


        try {
            String filePath = LogManager.getInstance().getModuleDic().get(moduleName).getFilePath();
            File file = new File(filePath);

            FileInputStream fin = new FileInputStream(file);
            NanoHTTPD.Response  response =  NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),"log/pull?file="+moduleName);
            response.addHeader("Content-disposition", String.format("attachment; filename=%s", file.getName()));
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ConsoleContent.append("download error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),e.getMessage());
        }catch (Exception e){
            ConsoleContent.append("error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),e.getMessage());
        }

    }



}
