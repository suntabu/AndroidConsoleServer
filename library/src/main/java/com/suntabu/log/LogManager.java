package com.suntabu.log;

import com.suntabu.ACS;

import java.io.File;
import java.util.HashMap;

public class LogManager{
    private static LogManager mInstance;

    private LogManager(){
        mModuleDic = new HashMap<>();
    }

    public static LogManager getInstance(){
        if (mInstance == null){
            mInstance = new LogManager();
        }
        return mInstance;
    }




    public String getLogPath(){

        String logPath = ACS.getContext().getCacheDir().getAbsolutePath() + "/applog";
        File log = new File(logPath);
        if (!log.exists()){
            ACS.mkdir(log);
        }

        return logPath;
    }


    public boolean IsAllEnable = true;
    public boolean IsLogFileEnable =true;
    public boolean IsLogConsoleEnable = true;

    private HashMap<String,LogModule> mModuleDic;

    public void Log(String moduleName,String msg){
        if (IsAllEnable){

            if (mModuleDic.containsKey(moduleName)){
                mModuleDic.get(moduleName).Log(msg);
            }else{
                LogModule module = new LogModule(moduleName);
                mModuleDic.put(moduleName,module);
                module.Log(msg);
            }


        }
    }

}
