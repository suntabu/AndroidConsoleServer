package com.suntabu.log;

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

    public String getFilePath() {
        String filePath = getParentPath() + "/" + moduleName + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
        return filePath;
    }


    public String getParentPath(){
        String folder = LogManager.getInstance().getLogPath() + "/" + moduleName;
        new File(folder).mkdirs();
        return folder;
    }


    public LogModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public void Log(String msg) {

        String format = "[%s] : %s";


        String content = String.format(format, new SimpleDateFormat("HH:mm:ss,SSS").format(new Date()), msg + " ");

        ConsoleContent.append(content);

        if (LogManager.getInstance().IsLogConsoleEnable) {
            Log.i(moduleName, content);
        }


        if (LogManager.getInstance().IsLogFileEnable) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(getFilePath(),true);
                fw.write("\n"+content);
                fw.close();
            } catch (IOException e) {
                Log.i(moduleName, e.getMessage());
            }

        }


    }
}
