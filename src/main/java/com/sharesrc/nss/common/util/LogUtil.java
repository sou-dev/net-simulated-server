//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss.common.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {
    private static LogUtil instance = null;
    private final HashMap<String, Logger> mapLogger = new HashMap();
    String logFolder = null;

    private LogUtil() {
        this.logFolder = PropUtil.getSeparateLogFolder() ? "./log" + File.separator + DateTimeUtil.getTimeNow4LogFolderName() : "./log";
    }

    public static synchronized LogUtil getInstance() {
        if (instance == null) {
            instance = new LogUtil();
        }

        return instance;
    }

    public Logger getLogger(String loggerName) {
        try {
            if (!this.mapLogger.containsKey(loggerName)) {
                Logger e = Logger.getLogger(loggerName);
                File f = new File(this.logFolder);
                if (!f.exists()) {
                    f.mkdirs();
                }

                FileHandler handler = new FileHandler(this.logFolder + File.separator + loggerName.substring(loggerName.lastIndexOf(".") + 1) + ".log", true);
                handler.setFormatter(new SimpleFormatter());
                e.setLevel(PropUtil.getLogAll() ? Level.ALL : Level.WARNING);
                e.addHandler(handler);
                this.mapLogger.put(loggerName, e);
            }
        } catch (SecurityException ex) {
            System.out.println("(SecurityException) Setup logging failed!!!");
        } catch (IOException ex) {
            System.out.println("(IOException) Setup logging failed!!!");
        }

        return (Logger) this.mapLogger.get(loggerName);
    }
}
