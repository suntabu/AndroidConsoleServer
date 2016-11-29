package com.suntabu.log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class SunLog {

    public static void Log(String msg) {
        LogManager.getInstance().Log("-app-", msg);
    }

    public static void Log(String module, String msg) {
        try {
            LogManager.getInstance().Log(module, URLDecoder.decode(msg, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            LogManager.getInstance().Log(module, msg);
            e.printStackTrace();
        }
    }

}


