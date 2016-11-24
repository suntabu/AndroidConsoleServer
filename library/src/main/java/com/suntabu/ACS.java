package com.suntabu;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.suntabu.consoleserver.ConsoleServer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gouzhun on 2016/11/22.
 *
 *
 * ACS(Android console server) api class
 *
 *
 */

public class ACS {

    private static Context context;
    private static boolean isStarted;
    private static int port = 8443;
    private static ConsoleServer consoleServer;

//TODO: wifi_state_changed and network_changed broadcast callback for server
    public static void init(Context ctx){
        context = ctx;
    }


    /**
     *
     * @param ctx
     * @param pt listen port on
     */
    public static void init(Context ctx,int pt){
        init(context);
        port = pt;
    }


    public static Context getContext(){
        return context;
    }




    public static boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }


    //region Start And Stop AndroidWebServer
    public static boolean startAndroidWebServer() {
        if (!isStarted) {
            try {
                if (port == 0) {
                    throw new Exception();
                }
                consoleServer = new ConsoleServer(port);
                consoleServer.start();
                isStarted = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Server","The PORT " + port + " doesn't work, please change it between 1000 and 9999.");
            }
        }
        return false;
    }

    private static boolean stopAndroidWebServer() {
        if (isStarted && consoleServer != null) {
            consoleServer.stop();
            isStarted = false;
            return true;
        }
        return false;
    }
    //endregion

    private static Application application;
    public static HashMap<String, Activity> clazzMap = new HashMap<>();
    public static HashMap<String, ?> beanMap = new HashMap<>();
    public static ArrayList<String> beanList = new ArrayList<>();

    public static void setApplication(Application app) {
        application = app;
    }

    public Application getApplication() {
        return application;
    }
    public static HashMap<String, Method> methodMap = new HashMap<>();
}
