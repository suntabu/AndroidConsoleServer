package com.suntabu.log;

import android.os.SystemClock;
import android.util.Log;

import com.suntabu.consoleserver.ConsoleContent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class LogModule {

    private String moduleName;

    private String getFilePath(){
        String filePath = LogManager.getInstance().getLogPath() + "/" + moduleName + "/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        new File(filePath).mkdirs();
        return filePath;
    }



    public LogModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public void Log(String msg) {

        String format = "\n [%s] : %s";


        String content = String.format(format,new SimpleDateFormat("HH:mm:ss,SSS").format(new Date()),msg + " " );

        ConsoleContent.LogContent.append(content);

        if (LogManager.getInstance().IsLogConsoleEnable){
            Log.i(moduleName,content);
        }


        if (LogManager.getInstance().IsLogFileEnable){
            FileWriter fw = null;
            try {
                fw = new FileWriter(getFilePath());
                fw.write(content);
                fw.close();
            } catch (IOException e) {
                Log.i(moduleName,e.getMessage());
            }

        }


    }
}
