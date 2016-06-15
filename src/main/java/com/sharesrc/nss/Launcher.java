//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss;

import com.sharesrc.nss.common.util.LogUtil;
import com.sharesrc.nss.common.util.PropUtil;

import javax.swing.*;
import java.util.logging.Logger;

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
                new NETSimulateServer();
            }
        });
    }
}
