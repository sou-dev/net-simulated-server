//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss.common.constant;

import javax.swing.*;
import java.awt.*;

public class Constants {
    public Constants() {
    }

    public static class Colour {
        public static final Color XML_EDITOR_TF_FILE_PATH_FG = new Color(0, 0, 96);
        public static final Color XML_EDITOR_TF_FILE_PATH_BG;
        public static final Color ELEMENT_NAME;
        public static final Color ELEMENT_VALUE;
        public static final Color ATTRIBUTE_NAME;
        public static final Color ATTRIBUTE_VALUE;
        public static final Color COMMENT;
        public static final Color CDATA;
        public static final Color TF_STATUS_READY;
        public static final Color TF_STATUS_STARTED;
        public static final Color TF_STATUS_STOPPED;
        public static final Color TP_CONSOLE_FG;
        public static final Color TP_CONSOLE_BG;

        static {
            XML_EDITOR_TF_FILE_PATH_BG = Color.YELLOW;
            ELEMENT_NAME = new Color(44, 179, 32);
            ELEMENT_VALUE = Color.BLACK;
            ATTRIBUTE_NAME = new Color(179, 32, 115);
            ATTRIBUTE_VALUE = Color.BLUE;
            COMMENT = Color.DARK_GRAY;
            CDATA = Color.ORANGE;
            TF_STATUS_READY = Color.ORANGE;
            TF_STATUS_STARTED = Color.GREEN;
            TF_STATUS_STOPPED = Color.RED;
            TP_CONSOLE_FG = Color.LIGHT_GRAY;
            TP_CONSOLE_BG = Color.BLACK;
        }

        public Colour() {
        }
    }

    public static class Config {
        public static final String[] CONFIG_FILE_PATH = new String[]{"cfg.properties", "/cfg.properties", "./cfg.properties"};
        public static final String CFG_DEFAULT_PORT = "DEFAULT_PORT";
        public static final String DEFAULT_PORT = "21288";
        public static final String CFG_QUEUE_OF_CONNECTIONS = "QUEUE_OF_CONNECTIONS";
        public static final String QUEUE_OF_CONNECTIONS = "10";
        public static final String CFG_SERVER_SOCKET_TIMEOUT = "SERVER_SOCKET_TIMEOUT";
        public static final String SERVER_SOCKET_TIMEOUT = "21288";
        public static final String CFG_MAX_BYTES_BUFFER_LENGTH = "MAX_BYTES_BUFFER_LENGTH";
        public static final String MAX_BYTES_BUFFER_LENGTH = "1024";
        public static final String CFG_LOG_ALL = "LOG_ALL";
        public static final String DEFAULT_LOG_ALL = "false";
        public static final String CFG_SEPARATE_LOG_FOLDER = "SEPARATE_LOG_FOLDER";
        public static final String SEPARATE_LOG_FOLDER = "false";
        public static final String CFG_DEFAULT_DOCUMENT = "DEFAULT_DOCUMENT";
        public static final String DEFAULT_DOCUMENT = "index.htm";

        public Config() {
        }
    }

    public static class Data {
        public static final String[] PAGES_FOLDER_PATH = new String[]{"pages", "/pages", "./pages", "page", "/page", "./page", "data", "/data", "./data", "web", "/web", "./web"};

        public Data() {
        }
    }

    public static class Fonts {
        public static final Font TF_STATUS = new Font("Verdana", 1, 26);
        public static final Font TP_CONSOLE = new Font("Sans", 0, 14);
        public static final Font TF_ENC_DEC = new Font("Arial", 1, 26);
        public static final Font BTN_ENC_DEC = new Font("Sans", 0, 14);
        public static final Font XMLEDITOR_DEFAULT = new Font("Monospaced", 1, 14);
        public static final Font XMLTEXTPANE_DEFAULT = new Font("Sans", 0, 14);

        public Fonts() {
        }
    }

    public static class Http {
        public static final String TAG_ALL = "All";
        public static final String TAG_MESSAGES = "Messages";
        public static final String TAG_RECV = "Recv";
        public static final String TAG_SEND = "Send";
        public static final String TAG_METHOD = "Method";
        public static final String TAG_CONTEXT_PATH = "Context-path";
        public static final String TAG_QUERRY = "Querry";
        public static final String TAG_PROTOCOL = "Protocol";
        public static final String TAG_STATUS_CODE = "Status-code";
        public static final String TAG_HEADERS = "Headers";
        public static final String TAG_HEADER_HOST = "Host";
        public static final String TAG_CONTENT = "Content";
        public static final String TAG_HEX = "Hex";
        public static final String TAG_ASCII = "Ascii";
        public static final String TAG_DECOMPRESS = "Decompress";

        public Http() {
        }
    }

    public static class Img {
        public static final Image APP_ICON = (new ImageIcon(Constants.class.getResource("/images/nss_app_icon.png"))).getImage();
        public static final ImageIcon START_ICON = new ImageIcon(Constants.class.getResource("/images/start.png"));
        public static final ImageIcon STOP_ICON = new ImageIcon(Constants.class.getResource("/images/stop.png"));
        public static final Image CONVERTER_ICON = (new ImageIcon(Constants.class.getResource("/images/converter_app_icon.png"))).getImage();
        public static final ImageIcon ENC_ICON = new ImageIcon(Constants.class.getResource("/images/encode.png"));
        public static final ImageIcon DEC_ICON = new ImageIcon(Constants.class.getResource("/images/decode.png"));
        public static final Image XML_EDITOR_ICON = (new ImageIcon(Constants.class.getResource("/images/editor_app_icon.png"))).getImage();
        public static final ImageIcon SAVE_ICON = new ImageIcon(Constants.class.getResource("/images/save.png"));
        public static final ImageIcon CLOSE_ICON = new ImageIcon(Constants.class.getResource("/images/close.png"));

        public Img() {
        }
    }

    public static class Logging {
        public static final String LOG_DIR = "./log";
        public static final String LOG_FILE_EXT = ".log";

        public Logging() {
        }
    }

    public static class Text {
        public static final String APP_NAME = "NET Simulate Server";
        public static final String APP_VERSION = "v1.1";
        public static final String APP_RELEASE = "r130225";
        public static final String APP_DESCRIPTION = "© 2013 sou";
        public static final String MENU_APPLICATION = "Application";
        public static final String MENU_SERVER_MODE = "Server Mode";
        public static final String MENU_TOOLS = "Tools";
        public static final String MENU_HELP = "Help";
        public static final String ITEM_HTTP_MODE = "HTTP";
        public static final String ITEM_SOCKET_MODE = "SOCKET";
        public static final String ITEM_EXIT = "Exit";
        public static final String ITEM_CONVERTER = "Converter";
        public static final String ITEM_ABOUT = "About";
        public static final String ITEM_MAKE_RESPONSE = "Make response";
        public static final String ITEM_CLEAR = "Clear";
        public static final String TXT_READY = "READY";
        public static final String TXT_STARTED = "STARTED";
        public static final String TXT_STOPPED = "STOPPED";
        public static final String SOCKETMGR_THREAD_NAME = "Socket Manager";
        public static final String PAGESINSPECTOR_THREAD_NAME = "Pages Inspector";
        public static final String CONVERTER_DIALOG_TITLE = "Converter";
        public static final String BUTTON_ENCODE = "Encode";
        public static final String BUTTON_DECODE = "Decode";
        public static final String LABEL_PATH = "Path";

        public Text() {
        }
    }
}
