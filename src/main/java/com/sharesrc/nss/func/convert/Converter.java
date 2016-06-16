/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.func.convert;

import com.sharesrc.nss.common.constant.Constants;
import com.sharesrc.nss.common.constant.Constants.Fonts;
import com.sharesrc.nss.common.constant.Constants.Img;
import com.sharesrc.nss.common.util.GZipUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The Converter UI.
 *
 * @author sou
 * @since 2013
 */
public class Converter extends JDialog {

    private static final long serialVersionUID = 9077685308507661381L;

    private static final String ACTION_COMMAND_ENC = "action_command_enc";
    private static final String ACTION_COMMAND_DEC = "action_command_dec";

    final Converter.EventHandler listener = new Converter.EventHandler();

    JTextArea taEncStr;
    JTextArea taDecStr;

    public Converter(Frame owner) {
        super(owner, Constants.Text.CONVERTER_DIALOG_TITLE, false);
        this.init();
    }

    private Component createMainPnl() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());
        JButton btnEnc = new JButton(Constants.Text.BUTTON_ENCODE);
        btnEnc.setFont(Fonts.BTN_ENC_DEC);
        btnEnc.setIcon(Img.ENC_ICON);
        btnEnc.setActionCommand(ACTION_COMMAND_ENC);
        btnEnc.addActionListener(this.listener);
        JButton btnDec = new JButton(Constants.Text.BUTTON_DECODE);
        btnDec.setFont(Fonts.BTN_ENC_DEC);
        btnDec.setIcon(Img.DEC_ICON);
        btnDec.setActionCommand(ACTION_COMMAND_DEC);
        btnDec.addActionListener(this.listener);
        this.taEncStr = new JTextArea(4, 14);
        this.taEncStr.setLineWrap(true);
        this.taEncStr.setFont(Fonts.TF_ENC_DEC);
        this.taDecStr = new JTextArea(4, 14);
        this.taDecStr.setBackground(Color.LIGHT_GRAY);
        this.taDecStr.setLineWrap(true);
        this.taDecStr.setFont(Fonts.TF_ENC_DEC);
        this.addComponent(panel, new JScrollPane(this.taEncStr, 20, 30), -1, 1, 0, 0, 2, new Insets(5, 5, 2, 0), 21);
        this.addComponent(panel, btnEnc, 0, 1, 1, 0, 0, new Insets(5, 5, 2, 10), 22);
        this.addComponent(panel, new JScrollPane(this.taDecStr, 20, 30), -1, 1, 0, 0, 2, new Insets(5, 5, 2, 0), 21);
        this.addComponent(panel, btnDec, 0, 1, 1, 0, 0, new Insets(5, 5, 2, 10), 22);

        return panel;
    }

    private void init() {
        this.getContentPane().add(this.createMainPnl(), BorderLayout.CENTER);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setIconImage(Img.CONVERTER_ICON);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(this.getOwner());
        this.setVisible(true);
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

    private void enc() {
        String encStr = this.taEncStr.getText().trim();
        if (encStr != null && encStr.length() != 0) {
            this.taDecStr.setText(GZipUtil.compress(encStr));
        }
    }

    private void dec() {
        String decStr = this.taDecStr.getText().trim();
        if (decStr != null && decStr.length() != 0) {
            this.taEncStr.setText(GZipUtil.decompress(decStr));
        }
    }

    class EventHandler implements ActionListener {

        EventHandler() {
        }

        public void actionPerformed(ActionEvent e) {
            if (ACTION_COMMAND_ENC.equals(e.getActionCommand())) {
                Converter.this.enc();
            }

            if (ACTION_COMMAND_DEC.equals(e.getActionCommand())) {
                Converter.this.dec();
            }

        }
    }
}
