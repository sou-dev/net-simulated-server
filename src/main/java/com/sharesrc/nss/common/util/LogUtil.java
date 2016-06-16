/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.common.util;

import com.sharesrc.nss.common.constant.Constants.Logging;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The utility for logging.
 *
 * @author sou
 * @since 2013
 */
public class LogUtil {

    private static LogUtil instance = null;
    private final HashMap<String, Logger> mapLogger = new HashMap();
    String logFolder = null;

    private LogUtil() {
        this.logFolder = PropUtil.getSeparateLogFolder() ? Logging.LOG_DIR + File.separator + DateTimeUtil.getTimeNow4LogFolderName() : Logging.LOG_DIR;
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
                Logger logger = Logger.getLogger(loggerName);
                File f = new File(this.logFolder);
                if (!f.exists()) {
                    f.mkdirs();
                }

                FileHandler handler = new FileHandler(this.logFolder + File.separator + loggerName.substring(loggerName.lastIndexOf(".") + 1) + Logging.LOG_FILE_EXT, true);
                handler.setFormatter(new SimpleFormatter());
                logger.setLevel(PropUtil.getLogAll() ? Level.ALL : Level.WARNING);
                logger.addHandler(handler);
                this.mapLogger.put(loggerName, logger);
            }
        } catch (SecurityException ex) {
            System.out.println("(SecurityException) Setup logging failed!!!");
        } catch (IOException ex) {
            System.out.println("(IOException) Setup logging failed!!!");
        }

        return this.mapLogger.get(loggerName);
    }
}
