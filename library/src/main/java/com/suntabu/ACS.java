package com.suntabu;

import android.content.Context;

import java.io.File;

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

    public static void init(Context ctx){
        context = ctx;
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

}
