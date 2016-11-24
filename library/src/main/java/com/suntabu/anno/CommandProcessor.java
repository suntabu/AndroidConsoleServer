package com.suntabu.anno;

import com.suntabu.ACS;
import com.suntabu.consoleserver.ConsoleContent;

import java.lang.reflect.Method;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Administrator on 2016/11/24.
 */

public class CommandProcessor {
    private static CommandProcessor processor;

    public static CommandProcessor getInstance() {
        if (processor == null) {
            processor = new CommandProcessor();
        }
        return processor;
    }


    public NanoHTTPD.Response procesgit s(String comm) {
        Method[] ms = AnnotationMethod.class.getDeclaredMethods();
        for (Method method : ms) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                if (comm.contains(command.value())) {
                    return AnnotationMethod.getInstance().excute(comm);
                }
            }
        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }


}
