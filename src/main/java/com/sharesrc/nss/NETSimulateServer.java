//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss;

import com.sharesrc.nss.common.constant.Constants.Colour;
import com.sharesrc.nss.common.constant.Constants.Data;
import com.sharesrc.nss.common.constant.Constants.Fonts;
import com.sharesrc.nss.common.constant.Constants.Img;
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
import javax.swing.text.AttributeSet;
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
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class NETSimulateServer extends JFrame {
    static final Logger LOGGER = LogUtil.getInstance().getLogger(NETSimulateServer.class.getName());
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
    final NETSimulateServer.EventHandler listener = new NETSimulateServer.EventHandler();
    final Object[] columnNames = new String[]{"Time", "Type", "Method", "Size", "Data"};
    boolean isStarted = false;
    int serverMode = 5;
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

    public NETSimulateServer() {
        this.init();
        this.redirectSystemOutputStreams();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                NETSimulateServer.this.stopServer();
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

    public Object getMessageṣ̣̣̣̣̣̣̣̣(int idx) {
        return this.vMessages.get(idx);
    }

    public Messages getHttpMessageṣ̣̣̣̣̣̣̣̣(int idx) {
        return (Messages) this.vMessages.get(idx);
    }

    public String getHttpMessageṣ̣̣̣̣̣̣̣̣Details(int idx) {
        return this.getHttpMessageṣ̣̣̣̣̣̣̣̣(idx).getDetails();
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
                Document doc = NETSimulateServer.this.tpConsole.getDocument();

                try {
                    doc.insertString(doc.getLength(), text, (AttributeSet) null);
                } catch (BadLocationException e) {
                    NETSimulateServer.LOGGER.warning("(BadLocationException) JTextPane console insert string failed!");
                }

                NETSimulateServer.this.tpConsole.setCaretPosition(doc.getLength() - 1);
            }
        });
    }

    private Component createTablePopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItemMakeResponse = new JMenuItem("Make response");
        menuItemMakeResponse.setActionCommand("action_command_make_response");
        menuItemMakeResponse.addActionListener(this.listener);
        JMenuItem menuItemClear = new JMenuItem("Clear");
        menuItemClear.setActionCommand("action_command_clear_table");
        menuItemClear.addActionListener(this.listener);
        if (this.getServerMode() == 5) {
            popup.add(menuItemMakeResponse);
            popup.addSeparator();
        }

        popup.add(menuItemClear);
        return popup;
    }

    private Component createTextPaneConsolePopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Clear");
        menuItem.setActionCommand("action_command_clear_console");
        menuItem.addActionListener(this.listener);
        popup.add(menuItem);
        return popup;
    }

    private Component createMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu menuApplication = new JMenu("Application");
        JMenu menuServerMode = new JMenu("Server Mode");
        ButtonGroup serverModeItemGroup = new ButtonGroup();
        JRadioButtonMenuItem itemHttp = new JRadioButtonMenuItem("HTTP");
        itemHttp.setSelected(true);
        itemHttp.setActionCommand("action_command_http_mode");
        itemHttp.addActionListener(this.listener);
        JRadioButtonMenuItem itemSocket = new JRadioButtonMenuItem("SOCKET");
        itemSocket.setActionCommand("action_command_socket_mode");
        itemSocket.addActionListener(this.listener);
        serverModeItemGroup.add(itemHttp);
        serverModeItemGroup.add(itemSocket);
        JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.setActionCommand("action_command_exit");
        itemExit.addActionListener(this.listener);
        JMenu menuTools = new JMenu("Tools");
        JMenuItem itemConverter = new JMenuItem("Converter");
        itemConverter.setActionCommand("action_command_converter");
        itemConverter.addActionListener(this.listener);
        JMenu menuHelp = new JMenu("Help");
        JMenuItem itemAbout = new JMenuItem("About");
        itemAbout.setActionCommand("action_command_about");
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
        this.tfStatus = new JTextField("READY", 48);
        this.tfStatus.setBorder(BorderFactory.createEtchedBorder(1));
        this.tfStatus.setHorizontalAlignment(0);
        this.tfStatus.setFont(Fonts.TF_STATUS);
        this.tfStatus.setBackground(Colour.TF_STATUS_READY);
        this.tfStatus.setEditable(false);
        this.tblExchangesModel = new DefaultTableModel((Object[][]) null, this.columnNames) {
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
            if (splitPnl == 0) {
                column.setPreferredWidth(120);
            } else if (splitPnl == 1) {
                column.setPreferredWidth(50);
            } else if (splitPnl == 2) {
                column.setPreferredWidth(30);
            } else if (splitPnl == 3) {
                column.setPreferredWidth(30);
            } else if (splitPnl == 4) {
                column.setPreferredWidth(500);
            }
        }

        this.tblExchanges.setSelectionMode(0);
        this.tblExchanges.setAutoCreateRowSorter(true);
        this.tblExchanges.addMouseListener(this.listener);
        this.tpConsole = new JTextPane();
        this.tpConsole.setFont(Fonts.TP_CONSOLE);
        this.tpConsole.setForeground(Colour.TP_CONSOLE_FG);
        this.tpConsole.setBackground(Colour.TP_CONSOLE_BG);
        this.tpConsole.setEditable(false);
        this.tpConsole.addMouseListener(this.listener);
        JSplitPane spConsole = new JSplitPane(0, new JScrollPane(this.tblExchanges, 20, 31), new JScrollPane(this.tpConsole, 20, 30));
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
        this.getContentPane().add(this.createMainPnl(), "Center");
        this.setDefaultCloseOperation(3);
        this.setTitle("NET Simulate Server v1.1");
        this.setIconImage(Img.APP_ICON);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width * 2 / 3, Toolkit.getDefaultToolkit().getScreenSize().height * 2 / 3);
        this.setLocationRelativeTo((Component) null);
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
                File e = new File(page);
                String uri = e.toURI().toString();
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
        if (this.getServerMode() == 5) {
            this.startHttpServer();
        } else {
            this.startServerSocket();
        }

    }

    private void stopServer() {
        if (this.getServerMode() == 5) {
            this.stopHttpServer();
        } else {
            this.stopServerSocket();
        }

    }

    private void startHttpServer() {
        String errStr = "";

        try {
            int e = PropUtil.getDefaultPort();
            if (e <= 0 || e > '\uffff') {
                e = Integer.parseInt("21288");
                System.out.println("Port number is out of range [1...65535]!\nSet port number to default: 21288");
            }

            int queueOfConnections = PropUtil.getQueueOfConnections();
            if (queueOfConnections < 0 || queueOfConnections > 10000) {
                queueOfConnections = Integer.parseInt("10");
                System.out.println("Size of connections queue is out of range [0...10000]!\nSet queue of connections to default: 10");
            }

            this.httpServer = HttpServer.create(new InetSocketAddress(e), queueOfConnections);
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
            this.httpServer.setExecutor((Executor) null);
            this.httpServer.start();
            this.setStarted(true);
            this.setEffect();
            (new NETSimulateServer.PagesInspector()).start();
            System.out.println("HttpServer started successful (port: " + e + ")");
        } catch (IOException e) {
            this.setStarted(false);
            errStr = e.toString();
            LOGGER.severe("HttpServer start failed!!!");
        } finally {
            if (!this.isStarted()) {
                JOptionPane.showMessageDialog(this, "HttpServer started failed!\nReason:\n" + errStr, "ERROR", 0);
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
        (new NETSimulateServer.SocketMgr()).start();
    }

    private void stopServerSocket() {
        if (this.isStarted()) {
            if (this.serverSocket != null) {
                try {
                    this.serverSocket.close();
                    this.serverSocket = null;
                } catch (IOException e) {
                    ;
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
        this.tfStatus.setText(this.isStarted() ? "STARTED" : "STOPPED");
        this.tfStatus.setBackground(this.isStarted() ? Colour.TF_STATUS_STARTED : Colour.TF_STATUS_STOPPED);
    }

    private void showColumnMethod() {
        TableColumnModel tcm = this.tblExchanges.getColumnModel();
        if (this.tcMethod != null) {
            tcm.addColumn(this.tcMethod);

            try {
                tcm.moveColumn(tcm.getColumnCount() - 1, 2);
            } catch (IllegalArgumentException e) {
                System.out.println("Show column \'METHOD\' failed!");
            }
        }

    }

    private void hideColumnMethod() {
        TableColumnModel tcm = this.tblExchanges.getColumnModel();

        try {
            this.tcMethod = tcm.getColumn(2);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Hide column \'METHOD\' failed!");
        }

        tcm.removeColumn(this.tcMethod);
    }

    private void redirectSystemOutputStreams() {
        OutputStream out = new OutputStream() {
            public void write(int b) throws IOException {
                NETSimulateServer.this.printConsole(String.valueOf((char) b));
            }

            public void write(byte[] b, int off, int len) throws IOException {
                NETSimulateServer.this.printConsole(new String(b, off, len));
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
            if (e.getSource().equals(NETSimulateServer.this.btnStart)) {
                NETSimulateServer.this.startServer();
            }

            if (e.getSource().equals(NETSimulateServer.this.btnStop)) {
                NETSimulateServer.this.stopServer();
            }

            if (e.getActionCommand().equals("action_command_exit")) {
                System.exit(0);
            }

            if (e.getActionCommand().equals("action_command_clear_table")) {
                NETSimulateServer.this.clearTableExchangesData();
            }

            if (e.getActionCommand().equals("action_command_clear_console")) {
                NETSimulateServer.this.tpConsole.setText("");
            }

            boolean selectedRowIdx;
            if (e.getActionCommand().equals("action_command_http_mode")) {
                if (NETSimulateServer.this.getServerMode() == 5) {
                    return;
                }

                selectedRowIdx = NETSimulateServer.this.isStarted();
                NETSimulateServer.this.stopServer();
                NETSimulateServer.this.setServerMode(5);
                if (selectedRowIdx) {
                    NETSimulateServer.this.startServer();
                }

                NETSimulateServer.this.clearTableExchangesData();
                NETSimulateServer.this.showColumnMethod();
            }

            if (e.getActionCommand().equals("action_command_socket_mode")) {
                if (NETSimulateServer.this.getServerMode() == 6) {
                    return;
                }

                selectedRowIdx = NETSimulateServer.this.isStarted();
                NETSimulateServer.this.stopServer();
                NETSimulateServer.this.setServerMode(6);
                if (selectedRowIdx) {
                    NETSimulateServer.this.startServer();
                }

                NETSimulateServer.this.clearTableExchangesData();
                NETSimulateServer.this.hideColumnMethod();
            }

            if (e.getActionCommand().equals("action_command_converter")) {
                new Converter(NETSimulateServer.this);
            }

            if (e.getActionCommand().equals("action_command_about")) {
                JOptionPane.showMessageDialog(NETSimulateServer.this, "NET Simulate Server\nv1.1 [r130225]\n© 2013 sou", "About", 1, new ImageIcon(Img.APP_ICON));
            }

            if (e.getActionCommand().equals("action_command_make_response")) {
                int selectedRowIdx1 = NETSimulateServer.this.tblExchanges.convertRowIndexToModel(NETSimulateServer.this.tblExchanges.getSelectedRow());
                String title = "[Make response] Type: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, 1).toString() + " | " + "Method: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, 2).toString() + " | " + "Content-length: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx1, 3).toString() + " | ";
                String contextPath = NETSimulateServer.this.getHttpMessageṣ̣̣̣̣̣̣̣̣(selectedRowIdx1).getRecv().getContextPath();
                if (contextPath.endsWith("/")) {
                    contextPath = contextPath.concat(PropUtil.getDefaultDocument());
                }

                String filePath = NETSimulateServer.this.pages_folder.getAbsolutePath() + "/" + (String) NETSimulateServer.this.getHttpMessageṣ̣̣̣̣̣̣̣̣(selectedRowIdx1).getRecv().getHeaders().get("Host") + contextPath;
                String data = NETSimulateServer.this.getHttpMessageṣ̣̣̣̣̣̣̣̣Details(selectedRowIdx1).trim();
                new XmlEditor(NETSimulateServer.this, title, filePath, data);
            }

        }

        public void windowClosing(WindowEvent e) {
            NETSimulateServer.this.stopServer();
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            if (e.getSource() instanceof JTable && e.getClickCount() == 2) {
                int selectedRowIdx = NETSimulateServer.this.tblExchanges.convertRowIndexToModel(NETSimulateServer.this.tblExchanges.getSelectedRow());
                String title = "[" + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx, 0).toString() + "] " + "Type: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx, 1).toString() + " | " + "Method: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx, 2).toString() + " | " + "Content-length: " + NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx, 3).toString() + " | ";
                String data = NETSimulateServer.this.getHttpMessageṣ̣̣̣̣̣̣̣̣Details(selectedRowIdx).trim();
                new XmlEditor(NETSimulateServer.this, title, data);
            }

        }

        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (e.getSource() instanceof JTable) {
                    int rowAtPoint = NETSimulateServer.this.tblExchanges.rowAtPoint(e.getPoint());
                    NETSimulateServer.this.tblExchanges.setRowSelectionInterval(0, rowAtPoint);
                    int selectedRowIdx = NETSimulateServer.this.tblExchanges.convertRowIndexToModel(rowAtPoint);
                    String type = NETSimulateServer.this.tblExchangesModel.getValueAt(selectedRowIdx, 1).toString();
                    if (type.equalsIgnoreCase("Recv")) {
                        ((JPopupMenu) NETSimulateServer.this.createTablePopupMenu()).show(e.getComponent(), e.getX(), e.getY());
                    }
                }

                if (e.getSource() instanceof JTextPane) {
                    ((JPopupMenu) NETSimulateServer.this.createTextPaneConsolePopupMenu()).show(e.getComponent(), e.getX(), e.getY());
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
            super("NET Simulate Server - Pages Inspector");
        }

        public void run() {
            while (NETSimulateServer.this.isStarted()) {
                try {
                    if (NETSimulateServer.this.isPagesChanged()) {
                        System.out.println("Content of pages folder was changed!");
                        NETSimulateServer.this.removeAllContext();
                        NETSimulateServer.this.createAllContext();
                    }

                    System.gc();
                    Thread.sleep(1488L);
                } catch (InterruptedException e) {
                    NETSimulateServer.LOGGER.warning("PagesInspector thread was interrupted!");
                }
            }

            this.interrupt();
        }
    }

    class SocketMgr extends Thread {
        public SocketMgr() {
            super("NET Simulate Server - Socket Manager");
        }

        public void run() {
            int portNumber = PropUtil.getDefaultPort();
            if (portNumber <= 0 || portNumber > '\uffff') {
                portNumber = Integer.parseInt("21288");
                System.out.println("Port number is out of range [1...65535]!\nSet port number to default: 21288");
            }

            int queueOfConnections = PropUtil.getQueueOfConnections();
            if (queueOfConnections < 0 || queueOfConnections > 10000) {
                queueOfConnections = Integer.parseInt("10");
                System.out.println("Size of connections queue is out of range [0...10000]!\nSet queue of connections to default: 10");
            }

            try {
                NETSimulateServer.this.serverSocket = new ServerSocket(portNumber, queueOfConnections);
            } catch (IOException e) {
                NETSimulateServer.this.setStarted(false);
                JOptionPane.showMessageDialog(NETSimulateServer.this, "ServerSocket started failed!\nReason:\n" + e.toString(), "ERROR", 0);
                if (NETSimulateServer.this.serverSocket != null) {
                    try {
                        NETSimulateServer.this.serverSocket.close();
                        NETSimulateServer.this.serverSocket = null;
                    } catch (IOException ex) {
                        ;
                    }
                }

                NETSimulateServer.LOGGER.severe("ServerSocket start failed!!!");
                return;
            }

            try {
                int e = PropUtil.getServerSocketTimeout();
                if (e < 0) {
                    e = 0;
                }

                NETSimulateServer.this.serverSocket.setSoTimeout(e);
                NETSimulateServer.this.setStarted(true);
                NETSimulateServer.this.setEffect();
                System.out.println("ServerSocket started successful (port: " + portNumber + (e > 0 ? "; timeout: " + e + "ms" : "") + ")");
                NETSimulateServer.this.vClients = new Vector();

                while (NETSimulateServer.this.isStarted()) {
                    Socket clientSocket = null;

                    try {
                        clientSocket = NETSimulateServer.this.serverSocket.accept();
                    } catch (SocketTimeoutException ex) {
                        continue;
                    } catch (SocketException ex) {
                        break;
                    } catch (IOException ex) {
                        break;
                    }

                    if (clientSocket != null) {
                        NETSimulateServer.this.vClients.add(clientSocket);
                        SocketRequestHandler e1 = new SocketRequestHandler(NETSimulateServer.this, clientSocket);
                        e1.start();
                    }

                    System.gc();

                    try {
                        Thread.sleep(1488L);
                    } catch (InterruptedException ex) {
                        NETSimulateServer.LOGGER.warning("SocketMgr thread was interrupted!");
                    }
                }
            } catch (Exception e) {
                System.out.println("ServerSocket: an exception has occurred at setSoTimeout() method");
            }

        }
    }
}