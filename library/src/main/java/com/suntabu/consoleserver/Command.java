package com.suntabu.consoleserver;

import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class Command {


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

            }else{
                ConsoleContent.append("no found " + strings[0]);
            }

        }else{
            ConsoleContent.append("nothing to show...");
        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),ConsoleContent.Log());
    }

    public NanoHTTPD.Response listLogModule(){
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
