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




    public NanoHTTPD.Response listLogModule(){
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String,LogModule> entry: list){
            names +="\t\t"+ entry.getKey()+"\n";
        }


        ConsoleContent.LogContent += names;
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK,NanoHTTPD.mimeTypes().get("md"),ConsoleContent.LogContent);
    }


    public void pullLog(String moduleName){

    }

}
