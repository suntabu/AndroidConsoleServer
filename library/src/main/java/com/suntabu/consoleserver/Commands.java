package com.suntabu.consoleserver;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.suntabu.ACS;
import com.suntabu.anno.Command;
import com.suntabu.anno.CommandProcessor;
import com.suntabu.log.LogManager;
import com.suntabu.log.LogModule;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.mimeTypes;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 * Created by gouzhun on 2016/11/22.
 */

public class Commands {
    private static final String TAG = "Commands";
    private CommandProcessor processor;
    private static final int JSON_INDENT = 2;

    public Commands() {
        processor = new CommandProcessor(this);
    }


    public NanoHTTPD.Response handle(String command) {
        ConsoleContent.append("> " + command);


        return processor.handle(command);
    }


    @Command(value = "clear", description = "clear console")
    private void clearConsole(String[] args) {
        ConsoleContent.clear();
    }

    @Command(value = "help", description = "command info for help")
    private void help(String[] args) {
        processor.help();
    }


    @Command(value = "check", description = "check methods or fields")
    private NanoHTTPD.Response checkVar(String[] args) {
        String result = "";
        try {
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();
            }
            for (Map.Entry<String, Activity> entry : ACS.clazzMap.entrySet()) {
                if (entry.getKey().contains(args[0])) {
                    Class clazz = Class.forName(entry.getKey());
                    String last = args[args.length - 1];
//                    String lastTemp = last.substring(last.indexOf("(") + 1, last.indexOf(")"));
                    if (Arrays.asList(args).contains("-m")) {
                        java.lang.reflect.Method method = clazz.getDeclaredMethod(args[2]);

                    } else {
                        Field field = clazz.getDeclaredField(args[1]);
                        field.setAccessible(true);
//                        result = processField(field.get(entry.getValue()), lastTemp);
                        Object o = field.get(entry.getValue());
                        if (o instanceof String) {
                            result = args[1] + " = " + o;
                        } else {
                            result = args[1] + " = " + o.toString();
                        }

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        ConsoleContent.append("\n" + result + "\n");
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeTypes().get("md"), "");
    }


    @Command(value = "lm", description = "list module names")
    public NanoHTTPD.Response listLogModule(String[] args) {
        Set<Map.Entry<String, LogModule>> list = LogManager.getInstance().getModuleDic().entrySet();
        String names = "Modules: \n";
        for (Map.Entry<String, LogModule> entry : list) {
            names += "\t\t" + entry.getKey() + "\n";
        }


        ConsoleContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }


    @Command(value = "ls", description = "list: \n\t\t\t --  -i cache directory.(context.getCacheDir() and context.getExternalCacheDir() )\n\t\t\t --  -e custom use external directory")
    public NanoHTTPD.Response listDirecotry(String[] args) {
        String names = "files: \n";
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        if (Arrays.asList(args).contains("-e")) {

        }

        if (Arrays.asList(args).contains("-i")) {
            names += "\ninternal cached files: \n";
            File fileDir = ACS.getContext().getCacheDir();
            names += ConsoleContent.printDirecotry(fileDir, "\n\t\t");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                names += "\nexternal cached files: \n";
                names += ConsoleContent.printDirecotry(ACS.getContext().getExternalCacheDir(), "\n\t\t");
            }
        }

        ConsoleContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }

    @Command(value = "la", description = "list all activities")
    public NanoHTTPD.Response listActivities(String[] args) {

        String names = "Activities: \n";
        Map activities = null;
        try {
            activities = ConsoleContent.getActivities();
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field activityField = activityRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Activity activity = (Activity) activityField.get(activityRecord);
                names += "\t\t" + "  " + activity.getPackageCodePath() + "  " + activity.getClass().getName() + "\n";
            }
        } catch (ClassNotFoundException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        }

        ConsoleContent.append(names);
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }


    @Command(value = "pull", description = "pull module log or file")
    public NanoHTTPD.Response pullLogModule(String[] args) {
        try {
            if (args.length >= 1) {
                String arg = args[0];
                NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), "log/pull?file=" + arg);

                if (LogManager.getInstance().getModuleDic().containsKey(arg)) {
                    String filePath = LogManager.getInstance().getModuleDic().get(arg).getFilePath();
                    File file = new File(filePath);
                    response.addHeader("Content-disposition", String.format("attachment; filename=%s", file.getName()));
                } else {
                    response.addHeader("Content-disposition", String.format("attachment; filename=%s", arg));
                }

                return response;
            } else {
                ConsoleContent.append("expect <module name>");
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), "");
            }

        } catch (Exception e) {
            ConsoleContent.append("error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        }

    }

    @Command(value = "cc", description = "clear cache")
    public NanoHTTPD.Response clearCache(String[] args) {
        try {
            ConsoleContent.clearAllCache(ACS.getContext());
            ConsoleContent.append("\t\tdone!");
        } catch (Exception e) {
            ConsoleContent.append("error: " + e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), e.getMessage());
        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), "");
    }


    @Command(value = "print", description = "print activity's info by executing toString method  (with -f to format json str)")
    public NanoHTTPD.Response printInfo(String[] args) {
        String result = "";
        boolean isFormat = false;
        try {
            ArrayList<String> ars = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();

                if (args[i].contains("-f")) {
                    isFormat = true;
                } else {
                    ars.add(args[i]);
                }

            }


            if (ars.size() >= 1) {
                Activity activity = ConsoleContent.getActivity(ars.get(0));
                if (activity != null) {
                    result = activity.toString();

                    if (isFormat) {
                        result = result.replace(" ", "\n");
                    }
                } else {
                    ConsoleContent.append("can not find activity named " + args[0]);
                }
            } else {
                ConsoleContent.append("expect <Activity Name>");
            }


        } catch (ClassNotFoundException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            ConsoleContent.append(e.getMessage());
            e.printStackTrace();
        }
        ConsoleContent.append("\n" + result + "\n");
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeTypes().get("md"), "");
    }

}
