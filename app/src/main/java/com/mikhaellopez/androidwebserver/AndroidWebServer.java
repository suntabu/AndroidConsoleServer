package com.mikhaellopez.androidwebserver;

import java.lang.reflect.Field;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Mikhael LOPEZ on 14/12/2015.
 */
public class AndroidWebServer extends NanoHTTPD {

    private String temp;

    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server"+ "" +"</h1>\n";
        Map<String, String> parms = session.getParms();

        try {
            Field tempField = MainActivity.class.getDeclaredField("temp");
            tempField.setAccessible(true);
            temp = (String) tempField.get(MainActivity.activity);


        } catch (NoSuchFieldException e) {


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
//            msg += "<p>Hello, " + parms.get("username") + "!</p>";
            msg += "<p>Hello, " + temp + "!</p>";
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );
    }
}
