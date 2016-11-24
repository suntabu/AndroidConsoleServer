package com.suntabu.consoleserver;

import android.content.Context;
import android.content.res.AssetManager;

import com.suntabu.ACS;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleContent {

    private static final int MAX_CACHE = 100;


    private static ArrayList<String> outputs = new ArrayList<>();

    public static String Log(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<outputs.size();i++){
            if(i==(outputs.size()-1)){
                sb.append(outputs.get(i));
            }else{
                sb.append(outputs.get(i)).append("\n");
            }
        }

        return new String(sb);
    }


    public static void append(String msg){
        if (outputs.size() >=MAX_CACHE){
            outputs.remove(0);
        }

        outputs.add(msg);
    }

    public static void clear(){
        outputs.clear();
    }

    public static String loadAssets(String path) throws IOException {
        String fileContent = readAssetsTextFile(ACS.getContext().getResources().getAssets(),path);
        return fileContent;
    }



    private static String readAssetsTextFile(AssetManager am, String fileName) throws IOException {

            InputStream is = am.open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, "utf-8");
            is.close();
            return json;

    }



}
