/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss;

import com.sharesrc.nss.common.constant.Constants.*;
import com.sharesrc.nss.common.util.LogUtil;
import com.sharesrc.nss.common.util.PropUtil;
import com.sharesrc.nss.func.convert.Converter;
import com.sharesrc.nss.func.xmleditor.XmlEditor;
import com.sharesrc.nss.model.Messages;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * The Network Simulated Server.
 *
 * @author sou
 * @since 2013
 */
public class NETSimulatedServer extends JFrame {

    static final Logger LOGGER = LogUtil.getInstance().getLogger(NETSimulatedServer.class.getName());
    static final int COLUMN_TIME = 0;
    static final int COLUMN_TYPE = 1;
    static final int COLUMN_METHOD = 2;
    static final int COLUMN_SIZE = 3;
    static final int COLUMN_DATA = 4;
    private static final long serialVersionUID = -1012391798601692100L;
    private static final int HTTP_MODE = 5;
    private static final int SOCKET_MODE = 6;

    private static final String ACTION_COMMAND_CONVERTER = "action_command_converter";
    private static final String ACTION_COMMAND_ABOUT = "action_command_about";
    private static final String ACTION_COMMAND_MAKE_RESPONSE = "action_command_make_response";
    private static final String ACTION_COMMAND_EXIT = "action_command_exit";
    private static final String ACTION_COMMAND_CLEAR_TABLE = "action_command_clear_table";
    private static final String ACTION_COMMAND_CLEAR_CONSOLE = "action_command_clear_console";
    private static final String ACTION_COMMAND_HTTP = "action_command_http_mode";
    private static final String ACTION_COMMAND_SOCKET = "action_command_socket_mode";

    final HashMap<String, String> hmapAllPages = new HashMap();
    final NETSimulatedServer.EventHandler listener = new NETSimulatedServer.EventHandler();
    final Object[] columnNames = new String[]{"Time", "Type", "Method", "Size", "Data"};
    boolean isStarted = false;
    int serverMode = HTTP_MODE;
    HttpServer httpServer;
    HttpRequestHandler httpRequestHandler;
    File pages_folder = null;
    Vector<String> vAllPages = new Vector();
    Vector<Object> vMessages = new Vector();
    ServerSocket serverSocket;
    Vector<Socket> vClients;

    JButton btnStart;
    JButton btnStop;
    JTextField tfStatus;
    JTable tblExchanges;
    TableColumn tcMethod;
    DefaultTableModel tblExchangesModel;
    JTextPane tpConsole;

    public NETSimulatedServer() {
        this.init();
        this.redirectSystemOutputStreams();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                NETSimulatedServer.this.stopServer();
            }
        });
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public int getServerMode() {
        return this.serverMode;
    }

    public void setServerMode(int serverMode) {
        this.serverMode = serverMode;
    }

    public HttpServer getServer() {
        return this.httpServer;
    }

    public Object getMessages(int idx) {
        return this.vMessages.get(idx);
    }

    public Messages getHttpMessages(int idx) {
        return (Messages) this.vMessages.get(idx);
    }

    public String getHttpMessagesDetails(int idx) {
        return this.getHttpMessages(idx).getDetails();
    }

    public void addMessages(Object m) {
        this.vMessages.add(m);
    }

    public void updateTableExchanges(Object[] rowData) {
        this.tblExchangesModel.addRow(rowData);
        this.tblExchangesModel.fireTableDataChanged();
    }

    public void clearTableExchangesData() {
        this.tblExchangesModel.setRowCount(0);
        this.vMessages.clear();
    }

    public void printConsole(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Document doc = NETSimulatedServer.this.tpConsole.getDocument();

                try {
                    doc.insertString(doc.getLength(), text, null);
                } catch (BadLocationException e) {
                    NETSimulatedServer.LOGGER.warning("(BadLocationException) JTextPane console insert string failed!");
                }

                NETSimulatedServer.this.tpConsole.setCaretPosition(doc.getLength() - 1);
            }
        });
    }

    private Component createTablePopupMenu() {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItemMakeResponse = new JMenuItem(Text.ITEM_MAKE_RESPONSE);
        menuItemMakeResponse.setActionCommand(ACTION_COMMAND_MAKE_RESPONSE);
        menuItemMakeResponse.addActionListener(this.listener);
        JMenuItem menuItemClear = new JMenuItem(Text.ITEM_CLEAR);
        menuItemClear.setActionCommand(ACTION_COMMAND_CLEAR_TABLE);
        menuItemClear.addActionListener(this.listener);
        if (HTTP_MODE == this.getServerMode()) {
            popup.add(menuItemMakeResponse);
            popup.addSeparator();
        }

        popup.add(menuItemClear);

        return popup;
    }

    private Component createTextPaneConsolePopupMenu() {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem(Text.ITEM_CLEAR);
        menuItem.setActionCommand(ACTION_COMMAND_CLEAR_CONSOLE);
        menuItem.addActionListener(this.listener);
        popup.add(menuItem);

        return popup;
    }

    private Component createMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu menuApplication = new JMenu(Text.MENU_APPLICATION);
        JMenu menuServerMode = new JMenu(Text.MENU_SERVER_MODE);
        ButtonGroup serverModeItemGroup = new ButtonGroup();
        JRadioButtonMenuItem itemHttp = new JRadioButtonMenuItem(Text.ITEM_HTTP_MODE);
        itemHttp.setSelected(true);
        itemHttp.setActionCommand(ACTION_COMMAND_HTTP);
        itemHttp.addActionListener(this.listener);
        JRadioButtonMenuItem itemSocket = new JRadioButtonMenuItem(Text.ITEM_SOCKET_MODE);
        itemSocket.setActionCommand(ACTION_COMMAND_SOCKET);
        itemSocket.addActionListener(this.listener);
        serverModeItemGroup.add(itemHttp);
        serverModeItemGroup.add(itemSocket);
        JMenuItem itemExit = new JMenuItem(Text.ITEM_EXIT);
        itemExit.setActionCommand(ACTION_COMMAND_EXIT);
        itemExit.addActionListener(this.listener);
        JMenu menuTools = new JMenu(Text.MENU_TOOLS);
        JMenuItem itemConverter = new JMenuItem(Text.ITEM_CONVERTER);
        itemConverter.setActionCommand(ACTION_COMMAND_CONVERTER);
        itemConverter.addActionListener(this.listener);
        JMenu menuHelp = new JMenu(Text.MENU_HELP);
        JMenuItem itemAbout = new JMenuItem(Text.ITEM_ABOUT);
        itemAbout.setActionCommand(ACTION_COMMAND_ABOUT);
        itemAbout.addActionListener(this.listener);
        menuServerMode.add(itemHttp);
        menuServerMode.add(itemSocket);
        menuApplication.add(menuServerMode);
        menuApplication.addSeparator();
        menuApplication.add(itemExit);
        menuTools.add(itemConverter);
        menuHelp.add(itemAbout);
        mb.add(menuApplication);
        mb.add(menuTools);
        mb.add(menuHelp);

        return mb;
    }

    private Component createMainPnl() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        this.btnStart = new JButton();
        this.btnStart.setIcon(Img.START_ICON);
        this.btnStart.setBorder(BorderFactory.createBevelBorder(0));
        this.btnStart.addActionListener(this.listener);
        this.btnStop = new JButton();
        this.btnStop.setIcon(Img.STOP_ICON);
        this.btnStop.setBorder(BorderFactory.createBevelBorder(0));
        this.btnStop.setEnabled(false);
        this.btnStop.addActionListener(this.listener);
        this.tfStatus = new JTextField(Text.TXT_READY, 48);
        this.tfStatus.setBorder(BorderFactory.createEtchedBorder(1));
        this.tfStatus.setHorizontalAlignment(JTextField.CENTER);
        this.tfStatus.setFont(Fonts.TF_STATUS);
        this.tfStatus.setBackground(Colour.TF_STATUS_READY);
        this.tfStatus.setEditable(false);
        this.tblExchangesModel = new DefaultTableModel(null, this.columnNames) {
            private static final long serialVersionUID = -6492525560999573339L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tblExchanges = new JTable(this.tblExchangesModel);
        this.tblExchanges.getTableHeader().setReorderingAllowed(false);
        TableColumn column = null;

        for (int splitPnl = 0; splitPnl < this.columnNames.length; ++splitPnl) {
            column = this.tblExchanges.getColumnModel().getColumn(splitPnl);
            if (COLUMN_TIME == splitPnl) {
                column.setPreferredWidth(120);
            } else if (COLUMN_TYPE == splitPnl) {
                column.setPreferredWidth(50);
            } else if (COLUMN_METHOD == splitPnl) {
                column.setPreferredWidth(30);
            } else if (COLUMN_SIZE == splitPnl) {
                column.setPreferredWidth(30);
            } else if (COLUMN_DATA == splitPnl) {
                column.setPreferredWidth(500);
            }
        }

        this.tblExchanges.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tblExchanges.setAutoCreateRowSorter(true);
        this.tblExchanges.addMouseListener(this.listener);
        this.tpConsole = new JTextPane();
        this.tpConsole.setFont(Fonts.TP_CONSOLE);
        this.tpConsole.setForeground(Colour.TP_CONSOLE_FG);
        this.tpConsole.setBackground(Colour.TP_CONSOLE_BG);
        this.tpConsole.setEditable(false);
        this.tpConsole.addMouseListener(this.listener);
        JSplitPane spConsole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(this.tblExchanges, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), new JScrollPane(this.tpConsole, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        spConsole.setOneTouchExpandable(true);
        spConsole.setDividerSize(20);
        spConsole.setDividerLocation(200);
        this.addComponent(panel, this.btnStart, 1, 1, 0, 0, 0, new Insets(5, 3, 10, 3), 21);
        this.addComponent(panel, this.btnStop, 1, 1, 0, 0, 0, new Insets(5, 0, 10, 3), 21);
        this.addComponent(panel, this.tfStatus, 0, 1, 1, 0, 2, new Insets(6, 0, 10, 3), 22);
        this.addComponent(panel, spConsole, 0, 1, 1, 1, 1, new Insets(0, 0, 0, 0), 21);

        return panel;
    }

    private void init() {
        this.setJMenuBar((JMenuBar) this.createMenuBar());
        this.getContentPane().add(this.createMainPnl(), BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(Text.APP_NAME + " " + Text.APP_VERSION);
        this.setIconImage(Img.APP_ICON);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3, Toolkit.getDefaultToolkit().getScreenSize().height * 2 / 3);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.toFront();
        this.setAlwaysOnTop(true);
        this.setAlwaysOnTop(false);
        this.addWindowListener(this.listener);
    }

    private void addComponent(Container owner, Component component, int gridwidth, int gridheight, int weightx, int weighty, int fill, Insets insets, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.weightx = (double) weightx;
        c.weighty = (double) weighty;
        c.fill = fill;
        c.insets = insets;
        c.anchor = anchor;
        owner.add(component, c);
    }

    public void visitAllDirsAndFiles(File f, Vector<String> result) {
        if (f != null) {
            if (f.isDirectory()) {
                File[] children = f.listFiles();

                for (int i = 0; i < children.length; ++i) {
                    File child = children[i];
                    result.add(child.isDirectory() ? child.getPath() + File.separator + PropUtil.getDefaultDocument() : child.getPath());
                    this.visitAllDirsAndFiles(child, result);
                }
            }

        }
    }

    public boolean isPagesChanged() {
        Vector vTemp = new Vector();
        this.visitAllDirsAndFiles(this.pages_folder, vTemp);
        return !this.vAllPages.equals(vTemp);
    }

    public void removeAllContext() {
        this.httpServer.removeContext("/");
        this.httpServer.removeContext("/" + PropUtil.getDefaultDocument());

        String context;
        for (Iterator it = this.hmapAllPages.keySet().iterator(); it.hasNext(); this.httpServer.removeContext(context)) {
            context = (String) it.next();
            if (context.substring(1).indexOf("/") != -1) {
                context = context.substring(context.substring(1).indexOf("/") + 1);
            }
        }

        this.hmapAllPages.clear();
    }

    public void createAllContext() {
        this.vAllPages.clear();
        this.visitAllDirsAndFiles(this.pages_folder, this.vAllPages);
        this.httpServer.createContext("/", this.httpRequestHandler);
        this.httpServer.createContext("/" + PropUtil.getDefaultDocument(), this.httpRequestHandler);

        String context_path;
        for (Iterator it = this.vAllPages.iterator(); it.hasNext(); this.httpServer.createContext(context_path, this.httpRequestHandler)) {
            String page = (String) it.next();
            context_path = "";

            try {
                File f = new File(page);
                String uri = f.toURI().toString();
                context_path = uri.substring(6 + this.pages_folder.getAbsolutePath().length(), uri.length());
                this.hmapAllPages.put(context_path, page);
            } catch (NullPointerException e) {
                LOGGER.warning("File is not existed! (" + page + ")");
            }

            if (context_path.substring(1).indexOf("/") != -1) {
                context_path = context_path.substring(context_path.substring(1).indexOf("/") + 1);
            }
        }

    }

    private void startServer() {
        if (HTTP_MODE == this.getServerMode()) {
            this.startHttpServer();
        } else {
            this.startServerSocket();
        }
    }

    private void stopServer() {
        if (HTTP_MODE == this.getServerMode()) {
            this.stopHttpServer();
        } else {
            this.stopServerSocket();
        }
    }

    private void startHttpServer() {
        String errStr = "";

        try {
            int port = PropUtil.getDefaultPort();
            if (port <= 0 || port > 65535) {
                port = Integer.parseInt("21288");
                System.out.println("Port number is out of range [1...65535]!\nSet port number to default: 21288");
            }

            int queueOfConnections = PropUtil.getQueueOfConnections();
            if (queueOfConnections < 0 || queueOfConnections > 10000) {
                queueOfConnections = Integer.parseInt("10");
                System.out.println("Size of connections queue is out of range [0...10000]!\nSet queue of connections to default: 10");
            }

            this.httpServer = HttpServer.create(new InetSocketAddress(port), queueOfConnections);
            String[] pagesFolderPath = Data.PAGES_FOLDER_PATH;

            for (int i = 0; i < pagesFolderPath.length; ++i) {
                String pfPath = pagesFolderPath[i];
                File f = new File(pfPath);
                if (f.exists()) {
                    this.pages_folder = f;
                    break;
                }
            }

            this.httpRequestHandler = new HttpRequestHandler(this);
            this.createAllContext();
            this.httpServer.setExecutor(null);
            this.httpServer.start();
            this.setStarted(true);
            this.setEffect();
            (new NETSimulatedServer.PagesInspector()).start();
            System.out.println("HttpServer started successful (port: " + port + ")");
        } catch (IOException e) {
            this.setStarted(false);
            errStr = e.toString();
            LOGGER.severe("HttpServer start failed!!!");
        } finally {
            if (!this.isStarted()) {
                JOptionPane.showMessageDialog(this, "HttpServer started failed!\nReason:\n" + errStr, "ERROR", JOptionPane.ERROR_MESSAGE);
                if (this.httpServer != null) {
                    this.httpServer.stop(0);
                    this.httpServer = null;
                }
            }

        }

    }

    private void stopHttpServer() {
        if (this.isStarted()) {
            this.removeAllContext();
            if (this.httpServer != null) {
                this.httpServer.stop(0);
                this.httpServer = null;
            }

            this.httpRequestHandler = null;
            this.setStarted(false);
            this.setEffect();
            System.out.println("HttpServer stopped!!!");
        }
    }

    private void startServerSocket() {
        (new NETSimulatedServer.SocketMgr()).start();
    }

    private void stopServerSocket() {
        if (this.isStarted()) {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                    this.serverSocket = null;
                } catch (IOException e) {
                }
            }

            this.setStarted(false);
            this.setEffect();
            System.out.println("ServerSocket stopped!!!");
        }
    }

    private void setEffect() {
        this.btnStart.setEnabled(!this.isStarted());
        this.btnStop.setEnabled(this.isStarted());
        this.tfStatus.setText(this.isStarted() ? Text.TXT_STARTED : Text.TXT_STOPPED);
        this.tfStatus.setBackground(this.isStarted() ? Colour.TF_STATUS_STARTED : Colour.TF_STATUS_STOPPED);
    }

    private void showColumnMethod() {
        TableColumnModel tcm = this.tblExchanges.getColumnModel();
        if (this.tcMethod != null) {
            tcm.addColumn(this.tcMethod);

            try {
                tcm.moveColumn(tcm.getColumnCount() - 1, COLUMN_METHOD);
            } catch (IllegalArgumentException e) {
                System.out.println("Show column \'METHOD\' failed!");
            }
        }

    }

    private void hideColumnMethod() {
        TableColumnModel tcm = this.tblExchanges.getColumnModel();

        try {
            this.tcMethod = tcm.getColumn(COLUMN_METHOD);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Hide column \'METHOD\' failed!");
        }

        tcm.removeColumn(this.tcMethod);
    }

    private void redirectSystemOutputStreams() {
        OutputStream out = new OutputStream() {
            public void write(int b) throws IOException {
                NETSimulatedServer.this.printConsole(String.valueOf((char) b));
            }

            public void write(byte[] b, int off, int len) throws IOException {
                NETSimulatedServer.this.printConsole(new String(b, off, len));
            }

            public void write(byte[] b) throws IOException {
                this.write(b, 0, b.length);
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    class EventHandler extends WindowAdapter implements ActionListener, MouseListener {
        EventHandler() {
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(NETSimulatedServer.this.btnStart)) {
                NETSimulatedServer.this.startServer();
            }

            if (e.getSource().equals(NETSimulatedServer.this.btnStop)) {
                NETSimulatedServer.this.stopServer();
            }

            if (ACTION_COMMAND_EXIT.equals(e.getActionCommand())) {
                System.exit(0);
            }

            if (ACTION_COMMAND_CLEAR_TABLE.equals(e.getActionCommand())) {
                NETSimulatedServer.this.clearTableExchangesData();
            }

            if (ACTION_COMMAND_CLEAR_CONSOLE.equals(e.getActionCommand())) {
                NETSimulatedServer.this.tpConsole.setText("");
            }

            boolean selectedRowIdx;
            if (ACTION_COMMAND_HTTP.equals(e.getActionCommand())) {
                if (HTTP_MODE == NETSimulatedServer.this.getServerMode()) {
                    return;
                }

                selectedRowIdx = NETSimulatedServer.this.isStarted();
                NETSimulatedServer.this.stopServer();
                NETSimulatedServer.this.setServerMode(HTTP_MODE);
                if (selectedRowIdx) {
                    NETSimulatedServer.this.startServer();
                }

                NETSimulatedServer.this.clearTableExchangesData();
                NETSimulatedServer.this.showColumnMethod();
            }

            if (ACTION_COMMAND_SOCKET.equals(e.getActionCommand())) {
                if (SOCKET_MODE == NETSimulatedServer.this.getServerMode()) {
                    return;
                }

                selectedRowIdx = NETSimulatedServer.this.isStarted();
                NETSimulatedServer.this.stopServer();
                NETSimulatedServer.this.setServerMode(SOCKET_MODE);
                if (selectedRowIdx) {
                    NETSimulatedServer.this.startServer();
                }

                NETSimulatedServer.this.clearTableExchangesData();
                NETSimulatedServer.this.hideColumnMethod();
            }

            if (ACTION_COMMAND_CONVERTER.equals(e.getActionCommand())) {
                new Converter(NETSimulatedServer.this);
            }

            if (ACTION_COMMAND_ABOUT.equals(e.getActionCommand())) {
                JOptionPane.showMessageDialog(NETSimulatedServer.this, Text.APP_NAME + "\n" + Text.APP_VERSION + " [" + Text.APP_RELEASE + "]\n" + Text.APP_DESCRIPTION, "About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(Img.APP_ICON));
            }

            if (ACTION_COMMAND_MAKE_RESPONSE.equals(e.getActionCommand())) {
                int selectedRowIdx1 = NETSimulatedServer.this.tblExchanges.convertRowIndexToModel(NETSimulatedServer.this.tblExchanges.getSelectedRow());
                String title = "[Make response] Type: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, COLUMN_TYPE).toString() + " | " + "Method: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, COLUMN_METHOD).toString() + " | " + "Content-length: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, COLUMN_SIZE).toString() + " | ";
                String contextPath = NETSimulatedServer.this.getHttpMessages(selectedRowIdx1).getRecv().getContextPath();
                if (contextPath.endsWith("/")) {
                    contextPath = contextPath.concat(PropUtil.getDefaultDocument());
                }

                String filePath = NETSimulatedServer.this.pages_folder.getAbsolutePath() + "/" + NETSimulatedServer.this.getHttpMessages(selectedRowIdx1).getRecv().getHeaders().get(Http.TAG_HEADER_HOST) + contextPath;
                String data = NETSimulatedServer.this.getHttpMessagesDetails(selectedRowIdx1).trim();
                new XmlEditor(NETSimulatedServer.this, title, filePath, data);
            }

        }

        public void windowClosing(WindowEvent e) {
            NETSimulatedServer.this.stopServer();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof JTable && 2 == e.getClickCount()) {
                int selectedRowIdx = NETSimulatedServer.this.tblExchanges.convertRowIndexToModel(NETSimulatedServer.this.tblExchanges.getSelectedRow());
                String title = "[" + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx, COLUMN_TIME).toString() + "] " + "Type: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx, COLUMN_TYPE).toString() + " | " + "Method: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx, COLUMN_METHOD).toString() + " | " + "Content-length: " + NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx, COLUMN_SIZE).toString() + " | ";
                String data = NETSimulatedServer.this.getHttpMessagesDetails(selectedRowIdx).trim();
                new XmlEditor(NETSimulatedServer.this, title, data);
            }

        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (e.getSource() instanceof JTable) {
                    int rowAtPoint = NETSimulatedServer.this.tblExchanges.rowAtPoint(e.getPoint());
                    NETSimulatedServer.this.tblExchanges.setRowSelectionInterval(0, rowAtPoint);
                    int selectedRowIdx = NETSimulatedServer.this.tblExchanges.convertRowIndexToModel(rowAtPoint);
                    String type = NETSimulatedServer.this.tblExchangesModel.getValueAt(selectedRowIdx, COLUMN_TYPE).toString();
                    if (Http.TAG_RECV.equalsIgnoreCase(type)) {
                        ((JPopupMenu) NETSimulatedServer.this.createTablePopupMenu()).show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                if (e.getSource() instanceof JTextPane) {
                    ((JPopupMenu) NETSimulatedServer.this.createTextPaneConsolePopupMenu()).show(e.getComponent(), e.getX(), e.getY());
                }
            }

        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    class PagesInspector extends Thread {
        public PagesInspector() {
            super(Text.APP_NAME + " - " + Text.PAGESINSPECTOR_THREAD_NAME);
        }

        public void run() {
            while (NETSimulatedServer.this.isStarted()) {
                try {
                    if (NETSimulatedServer.this.isPagesChanged()) {
                        System.out.println("Content of pages folder was changed!");
                        NETSimulatedServer.this.removeAllContext();
                        NETSimulatedServer.this.createAllContext();
                    }

                    System.gc();
                    Thread.sleep(1488L);
                } catch (InterruptedException e) {
                    NETSimulatedServer.LOGGER.warning("PagesInspector thread was interrupted!");
                }
            }

            this.interrupt();
        }
    }

    class SocketMgr extends Thread {
        public SocketMgr() {
            super(Text.APP_NAME + " - " + Text.SOCKETMGR_THREAD_NAME);
        }

        public void run() {
            int portNumber = PropUtil.getDefaultPort();
            if (portNumber <= 0 || portNumber > 65535) {
                portNumber = Integer.parseInt("21288");
                System.out.println("Port number is out of range [1...65535]!\nSet port number to default: 21288");
            }

            int queueOfConnections = PropUtil.getQueueOfConnections();
            if (queueOfConnections < 0 || queueOfConnections > 10000) {
                queueOfConnections = Integer.parseInt("10");
                System.out.println("Size of connections queue is out of range [0...10000]!\nSet queue of connections to default: 10");
            }

            try {
                NETSimulatedServer.this.serverSocket = new ServerSocket(portNumber, queueOfConnections);
            } catch (IOException e) {
                NETSimulatedServer.this.setStarted(false);
                JOptionPane.showMessageDialog(NETSimulatedServer.this, "ServerSocket started failed!\nReason:\n" + e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
                if (NETSimulatedServer.this.serverSocket != null) {
                    try {
                        NETSimulatedServer.this.serverSocket.close();
                        NETSimulatedServer.this.serverSocket = null;
                    } catch (IOException ex) {
                    }
                }

                NETSimulatedServer.LOGGER.severe("ServerSocket start failed!!!");
                return;
            }

            try {
                int serverSocketTimeout = PropUtil.getServerSocketTimeout();
                if (serverSocketTimeout < 0) {
                    serverSocketTimeout = 0;
                }

                NETSimulatedServer.this.serverSocket.setSoTimeout(serverSocketTimeout);
                NETSimulatedServer.this.setStarted(true);
                NETSimulatedServer.this.setEffect();
                System.out.println("ServerSocket started successful (port: " + portNumber + (serverSocketTimeout > 0 ? "; timeout: " + serverSocketTimeout + "ms" : "") + ")");
                NETSimulatedServer.this.vClients = new Vector();

                while (NETSimulatedServer.this.isStarted()) {
                    Socket clientSocket = null;

                    try {
                        clientSocket = NETSimulatedServer.this.serverSocket.accept();
                    } catch (SocketTimeoutException ex) {
                        continue;
                    } catch (SocketException ex) {
                        break;
                    } catch (IOException ex) {
                        break;
                    }

                    if (clientSocket != null) {
                        NETSimulatedServer.this.vClients.add(clientSocket);
                        SocketRequestHandler socketRequestHandler = new SocketRequestHandler(NETSimulatedServer.this, clientSocket);
                        socketRequestHandler.start();
                    }

                    System.gc();

                    try {
                        Thread.sleep(1488L);
                    } catch (InterruptedException ex) {
                        NETSimulatedServer.LOGGER.warning("SocketMgr thread was interrupted!");
                    }
                }
            } catch (Exception e) {
                System.out.println("ServerSocket: an exception has occurred at setSoTimeout() method");
            }

        }
    }
}