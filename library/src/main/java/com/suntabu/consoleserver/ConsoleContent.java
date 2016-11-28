package com.suntabu.consoleserver;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.suntabu.ACS;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

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
        Class activityThreadClass = null;

        activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
        Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
        activitiesField.setAccessible(true);
        Map activities = (Map) activitiesField.get(activityThread);
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

}
