package com.suntabu.log;

import android.text.TextUtils;
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


    public String getParentPath() {
        String folder = LogManager.getInstance().getLogPath() + "/" + moduleName;
        new File(folder).mkdirs();
        return folder;
    }


    public LogModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public void Log(String msg) {

        String format = "[%s][%s] : %s";

        String content = String.format(format, new SimpleDateFormat("HH:mm:ss,SSS").format(new Date()) ,moduleName, String.format(" %-40s", callMethodAndLine()) + msg);

        ConsoleContent.append(content);

        if (LogManager.getInstance().IsLogConsoleEnable) {
            Log.i(moduleName, content);
        }

        if (LogManager.getInstance().IsLogFileEnable) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(getFilePath(), true);
                fw.write("\n" + content);
                fw.close();
            } catch (IOException e) {
                Log.i(moduleName, e.getMessage());
            }
        }

    }


    private static String callMethodAndLine() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length - 1; i++) {
            StackTraceElement targetElement = stackTrace[i];
            String className = targetElement.getClassName();
            if (className.contains("SunLog")) {
                if (i + 1 < stackTrace.length) {
                    StackTraceElement stackElement = stackTrace[i + 1];
                    String myClass = stackElement.getClassName();
                    if (!TextUtils.isEmpty(LogManager.getInstance().key) && myClass.contains(LogManager.getInstance().key)) {
                        stackElement = stackTrace[i + 2];
                        myClass = stackElement.getClassName();
                    }

                    String[] classNameInfo = myClass.split("\\.");
                    if (classNameInfo.length > 0) {
                        myClass = classNameInfo[classNameInfo.length - 1] + ".java";
                    }
                    if (myClass.contains("$")) {
                        myClass = myClass.split("\\$")[0] + ".java";
                    }
                    String methodName = stackElement.getMethodName();
                    int lineNumber = stackElement.getLineNumber();
                    if (lineNumber < 0) {
                        lineNumber = 0;
                    }
                    String headString = "(" + myClass + ":" + lineNumber + ")";
                    return headString;
                }
            }
        }
        return "";
    }


}
