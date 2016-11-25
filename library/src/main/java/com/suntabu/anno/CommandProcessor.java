package com.suntabu.anno;

import com.suntabu.consoleserver.Commands;
import com.suntabu.consoleserver.ConsoleContent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Administrator on 2016/11/24.
 */

public class CommandProcessor {

    public CommandProcessor(Commands commands) {
        registeredCommand = new ArrayList<>();
        registerCommands();
        commandsInstance = commands;
    }

    private Commands commandsInstance;
    private ArrayList<CommandWrapper> registeredCommand;


    private void registerCommands() {
        Method[] ms = Commands.class.getDeclaredMethods();
        for (Method method : ms) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                if (command != null) {
                    CommandWrapper cw = new CommandWrapper(command,method);
                    registeredCommand.add(cw);
                }
            }
        }
    }

    public class CommandWrapper{
        public Command command;
        public Method method;

        public CommandWrapper(Command command, Method method) {
            this.command = command;
            this.method = method;
        }
    }


    public NanoHTTPD.Response handle(String command){
        CommandWrapper commandWrapper = null;
        String[] strings = command.split(" ");
        if (strings.length > 0){
            String commandStr = strings[0].trim();

            for (int i =0;i<registeredCommand.size();i++){
                CommandWrapper cw = registeredCommand.get(i);
                if (cw.command.value().equalsIgnoreCase(commandStr)){
                    commandWrapper = cw;

                }
            }

            if (commandWrapper != null){
                try {
//                   Object obj = commandWrapper.method.invoke(commandsInstance,command.replace(commandStr,"").split(" "));
                   Object obj = commandWrapper.method.invoke(commandsInstance,command.replace(commandStr,"").split(" "));
                    NanoHTTPD.Response response = (NanoHTTPD.Response) obj;
                    if (response!=null){
                        return response;
                    }
                } catch (IllegalAccessException e) {
                    ConsoleContent.append(e.getMessage());
                } catch (InvocationTargetException e) {
                    ConsoleContent.append(e.getMessage());
                }
            }else{
                ConsoleContent.append("not found " + strings[0]);
            }
        }
        else {
            ConsoleContent.append("nothing to show...");
        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.mimeTypes().get("md"), ConsoleContent.Log());
    }
}
