package com.suntabu.consoleserver;

import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class Command {


    public NanoHTTPD.Response handle(String command){
        ConsoleContent.LogContent.append("\n > " + command + "\n");
        String[] strings = command.split(" ");

        if (strings.length >0){
            if (strings[0].equalsIgnoreCase("clear")){
                ConsoleContent.LogContent.delete(0,ConsoleContent.LogContent.length());
            }else if(strings[0].equalsIgnoreCase("lm")){
                return listLogModule();
            }else if(strings[0].equalsIgnoreCase("help")){

            }else if(strings[0].equalsIgnoreCase("pull")){

            }else if(strings[0].equalsIgnoreCase("push")){

            }else{
                ConsoleContent.LogContent.append("no found " + strings[0]);
            }

        }else{
            ConsoleContent.LogContent.append("nothing to show...\n");
        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),ConsoleContent.Log());
    }

    public NanoHTTPD.Response listLogModule(){
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String,LogModule> entry: list){
            names +="\t\t"+ entry.getKey()+"\n";
        }


        ConsoleContent.LogContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),ConsoleContent.Log());
    }


    public void pullLog(String moduleName){

    }

}
