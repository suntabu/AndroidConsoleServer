package com.suntabu.consoleserver;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.suntabu.ACS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class ConsoleContent {

    private static final int MAX_CACHE = 100;


    private static ArrayList<String> outputs = new ArrayList<>();

    public static String Log() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < outputs.size(); i++) {
            if (i == (outputs.size() - 1)) {
                sb.append(outputs.get(i));
            } else {
                sb.append(outputs.get(i)).append("\n");
            }
        }

        return new String(sb);
    }


    public static void append(String msg) {
        if (outputs.size() >= MAX_CACHE) {
            outputs.remove(0);
        }

        outputs.add(msg);
    }

    public static void clear() {
        outputs.clear();
    }

    public static InputStream loadAssets(String path) throws IOException {
        InputStream fileContent = readAssetsTextFile(ACS.getContext().getResources().getAssets(), path);
        return fileContent;
    }


    private static InputStream readAssetsTextFile(AssetManager am, String fileName) throws IOException {
        InputStream is = am.open(fileName);
        return is;
    }


    public static Activity getActivity(String name) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Map activities = getActivities();
        for (Object activityRecord : activities.values()) {
            if (activityRecord.toString().contains(name)) {
                Class activityRecordClass = activityRecord.getClass();
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                return activity;
            }
        }

        return null;
    }

    public static Map getActivities() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Class activityThreadClass = null;

        activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
        return activities;
    }


    public static String printDirecotry(File fileDir,String tab){
        String names = "";
        File[] files = fileDir.listFiles();
        for(File file:files){
            names += tab + file.getAbsolutePath();
            if (file.isDirectory()){
                names += printDirecotry(file,tab + "\t\t");
            }else{
                names += tab + file.getPath();
            }
        }
        return names;
    }

    @NonNull
    public static NanoHTTPD.Response getDownloadFileResponse(String filePath) {
        File file = new File(filePath);
        try {
            ConsoleContent.append("download... " + filePath);
            FileInputStream fin = new FileInputStream(file);
            NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/octet-stream", fin, file.length());
            response.addHeader("Content-disposition", String.format("attachment; filename=%s", file.getName()));
            return response;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ConsoleContent.append("download error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        }
    }


    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
