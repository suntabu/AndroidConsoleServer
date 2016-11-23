package com.suntabu.log;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class SunLog {

    public static void Log(String msg){
        LogManager.getInstance().Log("-app-",msg);
    }

    public static void Log(String module,String msg){
        LogManager.getInstance().Log(module,msg);
    }

}


