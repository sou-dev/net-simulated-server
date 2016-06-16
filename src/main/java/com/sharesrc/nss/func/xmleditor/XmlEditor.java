/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.func.xmleditor;

import com.sharesrc.nss.common.constant.Constants;
import com.sharesrc.nss.common.constant.Constants.Colour;
import com.sharesrc.nss.common.constant.Constants.Fonts;
import com.sharesrc.nss.common.constant.Constants.Img;
import com.sharesrc.nss.common.util.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Logger;

/**
 * The XML Editor UI.
 *
 * @author sou
 * @since 2013
 */
public class XmlEditor extends JDialog {

    static final Logger LOGGER = LogUtil.getInstance().getLogger(XmlEditor.class.getName());
    private static final long serialVersionUID = 7165565729973253218L;
    private static final String ACTION_COMMAND_SAVE = "action_command_save";
    final XmlEditor.EventHandler listener = new XmlEditor.EventHandler();
    boolean editMode = false;
    String filePath = null;
    String data = null;
    XmlTextPane xmlTextPane;

    public XmlEditor(Frame owner, String title, String data) {
        super(owner, title, true);
        this.data = data;
        this.init();
    }

    public XmlEditor(Frame owner, String title, String filePath, String data) {
        super(owner, title, true);
        this.filePath = filePath;
        this.data = data;
        this.setEditMode(true);
        this.init();
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    private Component createToolsBarPnl() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(0, 2, 2));
        panel.setBorder(BorderFactory.createRaisedBevelBorder());
        JButton btnSave = new JButton(Img.SAVE_ICON);
        btnSave.setPreferredSize(new Dimension(22, 22));
        btnSave.setActionCommand(ACTION_COMMAND_SAVE);
        btnSave.addActionListener(this.listener);
        JButton btnClose = new JButton(Img.CLOSE_ICON);
        btnClose.setPreferredSize(new Dimension(22, 22));
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XmlEditor.this.dispose();
            }
        });
        JTextField tfFilePath = new JTextField(this.filePath, 66);
        tfFilePath.setEditable(false);
        tfFilePath.setForeground(Colour.XML_EDITOR_TF_FILE_PATH_FG);
        tfFilePath.setBackground(Colour.XML_EDITOR_TF_FILE_PATH_BG);
        panel.add(btnSave);
        panel.add(btnClose);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel(Constants.Text.LABEL_PATH));
        panel.add(tfFilePath);
        return panel;
    }

    private Component createEditorPnl() {
        this.xmlTextPane = new XmlTextPane();
        this.xmlTextPane.getEditor().setFont(Fonts.XMLEDITOR_DEFAULT);
        this.xmlTextPane.getEditor().setEditable(this.isEditMode());
        if (!this.isEditMode()) {
            this.xmlTextPane.append(this.data);
        } else {
            BufferedReader in = null;

            try {
                StringBuffer e = new StringBuffer();
                File f = new File(this.filePath);
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                in = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
                String str = null;

                while ((str = in.readLine()) != null) {
                    e.append(str + "\n");
                }

                if (e.toString().trim().length() == 0) {
                    e.append("<" + Constants.Http.TAG_ALL + ">\n\n");
                    e.append("</" + Constants.Http.TAG_ALL + ">");
                }

                int backOffset = Constants.Http.TAG_ALL.length() + 4;
                e.insert(e.toString().trim().length() - backOffset, "\n<!-- Uncomment for making response \n" + this.data + "\n-->\n");
                this.xmlTextPane.append(e.toString());
            } catch (FileNotFoundException ex) {
                LOGGER.severe("XML file not found!!! (" + this.filePath + ")");
            } catch (IOException ex) {
                LOGGER.severe("Can not read XML file!!! (" + this.filePath + ")");
            } finally {
                try {
                    in.close();
                    in = null;
                } catch (IOException ex) {
                    LOGGER.warning("Closing input stream failed! (" + this.filePath + ")");
                }

            }
        }

        return this.xmlTextPane;
    }

    private void init() {
        if (this.isEditMode()) {
            this.getContentPane().add(this.createToolsBarPnl(), "North");
        }

        this.getContentPane().add(this.createEditorPnl(), "Center");
        this.setDefaultCloseOperation(2);
        this.setIconImage(Img.XML_EDITOR_ICON);
        this.setSize(640, 480);
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        this.setVisible(true);
    }

    public void save() {
        BufferedWriter out = null;

        try {
            File e = new File(this.filePath);
            if (!e.exists()) {
                e.createNewFile();
            }

            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(e), "UTF-8"));
            this.xmlTextPane.getEditor().write(out);
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.severe("OutputStreamWriter unsupported encoding!!! (UTF-8)");
        } catch (FileNotFoundException ex) {
            LOGGER.severe("XML file not found!!! (" + this.filePath + ")");
        } catch (IOException ex) {
            LOGGER.severe("Can not read XML file!!! (" + this.filePath + ")");
        } finally {
            try {
                out.close();
                out = null;
            } catch (IOException ex) {
                LOGGER.warning("Closing output stream failed! (" + this.filePath + ")");
            }

        }

    }

    class EventHandler implements ActionListener {

        EventHandler() {
        }

        public void actionPerformed(ActionEvent e) {
            if (ACTION_COMMAND_SAVE.equals(e.getActionCommand())) {
                XmlEditor.this.save();
            }

        }
    }
}
