package com.mikhaellopez.androidwebserver;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Created by Administrator on 2016/11/22.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       /* try {
            ArrayList<String> classes = new ArrayList<>();
            DexFile file = new DexFile(getPackageResourcePath());
            Enumeration<String> strs = file.entries();
            while (strs.hasMoreElements()) {
                classes.add(strs.nextElement());
            }
            for (String c : classes) {
                Log.e(" ClassName", c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/



    }
}
