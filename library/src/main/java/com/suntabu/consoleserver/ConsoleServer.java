package com.suntabu.consoleserver;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleServer extends NanoHTTPD {

    private static final String MIME_JSON = "application/json";
    private static final String MIME_CSS = "text/css";
    private Console console = new Console();

    public ConsoleServer(String hostname, int port) {
        super(hostname, port);
    }

    public ConsoleServer(int port) {
        super(port);
    }


    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri();
            if (uri.contains("console.css")) {
                return newFixedLengthResponse(Response.Status.OK, MIME_CSS, ConsoleContent.loadAssets("console_html/console.css"));
            } else if (uri.contains("favicon.icon")) {

            }
            Map<String, String> params = session.getParms();
                if (params.size() == 0) {
                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ConsoleContent.loadAssets("console_html/index.html"));
            } else {
                Console con = new Console();
                con.console_run(session);
            }


            String responseString = "";
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, responseString);

        } catch (Resources.NotFoundException nfe) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        } catch (Exception ex) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }


    private Application application;
    public static HashMap<String, Activity> clazzMap = new HashMap<>();

    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }


}
