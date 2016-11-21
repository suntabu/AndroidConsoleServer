package com.mikhaellopez.androidwebserver;

/**
 * Created by gouzhun on 2016/11/21.
 */

import android.content.res.Resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class ClockServer extends NanoHTTPD {

    private static final String MIME_JSON = "application/json";
    private Clock clock;

    public ClockServer(Clock clock, int port) {
        super(port);
        this.clock = clock;
    }

    public ClockServer(int port) {
        super(port);
        this.clock = new Clock();
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Method method = session.getMethod();
            String uri = session.getUri();
            Map<String, String> parms = session.getParms();
            String responseString = serveClock(session, uri, method, parms);
            return newFixedLengthResponse(Response.Status.OK, MIME_JSON, responseString);

        } catch (IOException ioe) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
        } catch (ResponseException re) {
            return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
        } catch (NotFoundException nfe) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        } catch (Exception ex) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_HTML, "<html><body><h1>Error</h1>" + ex.toString() + "</body></html>");
        }
    }

    private String serveClock(IHTTPSession session, String uri, Method method, Map<String, String> parms) throws IOException, ResponseException {
        String responseString = "";
        do {
            if (Method.GET.equals(method)) {
                responseString = handleGet(session, parms);
                break;
            }

            if (Method.POST.equals(method)) {
                responseString = handlePost(session);
                break;
            }

            throw new Resources.NotFoundException();

        } while (false);

        return responseString;
    }

    private String handleGet(IHTTPSession session, Map<String, String> parms) {
        return clock.handleRequest("{'name':'status', 'value':''}");
    }

    private String handlePost(IHTTPSession session) throws IOException, ResponseException {
        Map<String, String> files = new HashMap<String, String>();
        session.parseBody(files);

        return clock.handleRequest(files.get("postData"));
    }


    private class NotFoundException extends RuntimeException {
    }
}