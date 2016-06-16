/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.common.util;

import com.sharesrc.nss.common.constant.Constants.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * The utility for properties files.
 *
 * @author sou
 * @since 2013
 */
public class PropUtil {

    static final Properties properties = new Properties();

    public PropUtil() {
    }

    public static void loadCfg() {
        loadCfg(null);
    }

    public static void loadCfg(String filePath) {
        try {
            if (filePath != null) {
                properties.load(new FileInputStream(filePath));
            } else {
                for (String configFilePath : Config.CONFIG_FILE_PATH) {
                    if (!(new File(configFilePath)).exists()) {
                        continue;
                    } else {
                        properties.load(new FileInputStream(configFilePath));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file not found!!!");
        } catch (IOException e) {
            System.out.println("Can not read configuration file!!!");
        }

    }

    public static String getCfg(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public static String getCfg(String propertyName, String defaultValue) {
        return properties.getProperty(propertyName, defaultValue);
    }

    public static int getDefaultPort() {
        return Integer.parseInt(getCfg(Config.CFG_DEFAULT_PORT, Config.DEFAULT_PORT));
    }

    public static int getQueueOfConnections() {
        return Integer.parseInt(getCfg(Config.CFG_QUEUE_OF_CONNECTIONS, Config.DEFAULT_QUEUE_OF_CONNECTIONS));
    }

    public static int getServerSocketTimeout() {
        return Integer.parseInt(getCfg(Config.CFG_SERVER_SOCKET_TIMEOUT, Config.DEFAULT_SERVER_SOCKET_TIMEOUT));
    }

    public static int getMaxBytesBufferLength() {
        return Integer.parseInt(getCfg(Config.CFG_MAX_BYTES_BUFFER_LENGTH, Config.DEFAULT_MAX_BYTES_BUFFER_LENGTH));
    }

    public static boolean getLogAll() {
        return Boolean.parseBoolean(getCfg(Config.CFG_LOG_ALL, Config.DEFAULT_LOG_ALL));
    }

    public static boolean getSeparateLogFolder() {
        return Boolean.parseBoolean(getCfg(Config.CFG_SEPARATE_LOG_FOLDER, Config.DEFAULT_SEPARATE_LOG_FOLDER));
    }

    public static String getDefaultDocument() {
        return getCfg(Config.CFG_DEFAULT_DOCUMENT, Config.DEFAULT_DOCUMENT);
    }
}
