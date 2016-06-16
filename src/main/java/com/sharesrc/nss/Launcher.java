/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss;

import com.sharesrc.nss.common.util.LogUtil;
import com.sharesrc.nss.common.util.PropUtil;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * The launcher to run desktop application.
 *
 * @author sou
 * @since 2013
 */
public class Launcher {

    public Launcher() {
    }

    public static void main(String[] args) {
        PropUtil.loadCfg();
        Logger LOGGER = LogUtil.getInstance().getLogger(Launcher.class.getName());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.warning("UIManager set \'System Look And Feel\' for application failed!");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NETSimulatedServer();
            }
        });
    }
}
