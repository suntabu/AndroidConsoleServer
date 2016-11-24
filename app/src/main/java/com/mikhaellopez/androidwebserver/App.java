package com.mikhaellopez.androidwebserver;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.suntabu.ACS;
import com.suntabu.consoleserver.ConsoleServer;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/22.
 */

public class App extends Application {
    public static App instance;

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
        instance = this;

        ACS.setApplication(instance);
    }

}
