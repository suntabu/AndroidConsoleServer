package com.suntabu.consoleserver;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleContent {


    private static Context context;

    public static String LogContent = "hahahhaha";



    public static void init(Context ctx){
        context = ctx;
    }

    public static String loadAssets(String path){
        String fileContent = readAssetsTextFile(context.getResources().getAssets(),path);
        return fileContent;
    }



    private static String readAssetsTextFile(AssetManager am, String fileName) {
        try {
            InputStream is = am.open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, "utf-8");
            is.close();
            return json;
        } catch (IOException e) {
            return "";
        }
    }



}
