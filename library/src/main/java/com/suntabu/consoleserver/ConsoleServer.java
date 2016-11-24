package com.suntabu.consoleserver;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleServer extends NanoHTTPD {
    private static final String ASSET_BASE = "console_html";

    private static final String MIME_JSON = "application/json";
    private static final String MIME_CSS = "text/css";
    private HashMap<String, String> fileTypes = new HashMap<>();
    private Pattern pattern = null;
    private Console console = new Console();

    public ConsoleServer(String hostname, int port) {
        super(hostname, port);
        initPattern();
    }

    public ConsoleServer(int port) {
        super(port);

        initPattern();
    }

    private void initPattern() {
        fileTypes.put("js", "application/javascript");
        fileTypes.put("json", "application/json");
        fileTypes.put("jpg", "image/jpeg");
        fileTypes.put("jpeg", "image/jpeg");
        fileTypes.put("gif", "image/gif");
        fileTypes.put("png", "image/png");
        fileTypes.put("css", "text/css");
        fileTypes.put("htm", "text/html");
        fileTypes.put("html", "text/html");
        fileTypes.put("ico", "image/x-icon");


        String patternstr = "^/(.*\\.(";
        for (Map.Entry<String, String> key : fileTypes.entrySet()) {
            patternstr += key.getKey() + "|";
        }
        patternstr = patternstr.substring(0, patternstr.length() - 1) + "))$";

        pattern = Pattern.compile(patternstr);
    }


    @Override
    public Response serve(IHTTPSession session) {
        try {
            Method method = session.getMethod();
            String uri = session.getUri();


            if (uri.equalsIgnoreCase("/")) {
                uri += "index.html";
            }

            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                String key = matcher.group();
                String[] strs = key.split("\\.");
                if (strs.length >= 1) {
                    String mimeType = strs[strs.length - 1];
                    if (fileTypes.containsKey(mimeType)) {
                        InputStream inputStream = ConsoleContent.loadAssets(ASSET_BASE + key);
                        return newFixedLengthResponse(Response.Status.OK, fileTypes.get(mimeType), inputStream, inputStream.available());
                    } else
                        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, mimeTypes().get("md"), "unsupported " + key);
                } else {
                    return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, mimeTypes().get("md"), "error for " + key);
                }

            }

            uri = uri.toLowerCase();
            if (uri.contains("console/out")) {

                return console.console_out(session);

            } else if (uri.contains("console/run")) {
                return console.console_run(session);

            } else if (uri.contains("console/commandhistory")) {

                return console.console_history(session);

            } else if (uri.contains("console/complete")) {

                return console.console_complete(session);

            } else if (uri.contains("log/pull")) {
                return console.log_pull(session);
            }


            String responseString = "";
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, responseString);

        } catch (Resources.NotFoundException nfe) {
            ConsoleContent.append(nfe.getMessage());
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found " + nfe.getMessage());
        } catch (Exception ex) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }

}
