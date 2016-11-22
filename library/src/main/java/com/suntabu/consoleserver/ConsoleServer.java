package com.suntabu.consoleserver;

import android.content.res.Resources;

import com.suntabu.log.SunLog;

import java.util.Map;
import java.util.logging.Logger;

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
            Method method = session.getMethod();
            String uri = session.getUri().toLowerCase();


            if (uri.contains("console.css")) {
                return newFixedLengthResponse(Response.Status.OK, MIME_CSS, ConsoleContent.loadAssets("console_html/console.css"));
            } else if (uri.contains("favicon.icon")) {

            }

            if (uri.contains("console/out")) {
                SunLog.Log("refresh",uri);
                return console.console_out(session);

            } else if (uri.contains("console/run")) {
                SunLog.Log(uri);
                return console.console_run(session);

            } else if (uri.contains("console/commandhistory")) {

                return console.console_history(session);

            } else if (uri.contains("console/complete")) {

                return console.console_complete(session);

            }


            Map<String, String> parms = session.getParms();
            if (parms.size() == 0) {
                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ConsoleContent.loadAssets("console_html/index.html"));
            } else {

            }


            String responseString = "";
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, responseString);

        } catch (Resources.NotFoundException nfe) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        } catch (Exception ex) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }
}
