package com.suntabu.consoleserver;


/**
 * Created by gouzhun on 2016/11/22.
 */

import com.suntabu.log.LogManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public class Console {
    private static final int MAX_RECORD = 10;
    private ArrayList<String> mCommandRecord = new ArrayList<>();
    private Commands commandsHandler;

    public Console() {
        commandsHandler = new Commands();
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

        return commandsHandler.handle(command);
//        return CommandProcessor.getInstance().process(command);

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


    public Response log_pull(IHTTPSession session) {
        String filePara = session.getParms().get("file");
        String filePath = "";
        if (LogManager.getInstance().getModuleDic().containsKey(filePara)) {
            filePath = LogManager.getInstance().getModuleDic().get(filePara).getFilePath();
        } else {
            filePath = filePara;
        }


        return ConsoleContent.getDownloadFileResponse(filePath);
    }


}
