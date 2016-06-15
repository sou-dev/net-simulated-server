//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss.common.util;

import com.sharesrc.nss.common.constant.Constants.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropUtil {
    static final Properties properties = new Properties();

    public PropUtil() {
    }

    public static void loadCfg() {
        loadCfg((String) null);
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
        return Integer.parseInt(getCfg("DEFAULT_PORT", "21288"));
    }

    public static int getQueueOfConnections() {
        return Integer.parseInt(getCfg("QUEUE_OF_CONNECTIONS", "10"));
    }

    public static int getServerSocketTimeout() {
        return Integer.parseInt(getCfg("SERVER_SOCKET_TIMEOUT", "21288"));
    }

    public static int getMaxBytesBufferLength() {
        return Integer.parseInt(getCfg("MAX_BYTES_BUFFER_LENGTH", "1024"));
    }

    public static boolean getLogAll() {
        return Boolean.parseBoolean(getCfg("LOG_ALL", "false"));
    }

    public static boolean getSeparateLogFolder() {
        return Boolean.parseBoolean(getCfg("SEPARATE_LOG_FOLDER", "false"));
    }

    public static String getDefaultDocument() {
        return getCfg("DEFAULT_DOCUMENT", "index.htm");
    }
}
