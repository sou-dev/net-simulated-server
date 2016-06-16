/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss;

import com.sharesrc.nss.common.util.DateTimeUtil;
import com.sharesrc.nss.common.util.LogUtil;
import com.sharesrc.nss.common.util.PropUtil;
import com.sharesrc.nss.model.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * The Socket Request Handler.
 *
 * @author sou
 * @since 2013
 */
public class SocketRequestHandler extends Thread {

    static final Logger LOGGER = LogUtil.getInstance().getLogger(SocketRequestHandler.class.getName());

    NETSimulatedServer nss;
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;
    long time = 0L;

    public SocketRequestHandler(NETSimulatedServer nss, Socket socket) {
        if (nss != null && socket != null) {
            this.nss = nss;
            this.socket = socket;

            try {
                this.in = this.socket.getInputStream();
                this.out = socket.getOutputStream();
            } catch (IOException e) {
                System.out.println("SocketRequestHandler I/O streams initialize failed!");
            }

            this.time = System.currentTimeMillis();
        }
    }

    public void run() {
        int maxBytesBufferLength = PropUtil.getMaxBytesBufferLength();
        if (maxBytesBufferLength <= 0) {
            maxBytesBufferLength = Integer.parseInt("1024");
            System.out.println("Max bytes buffer length is invalid!\nSet max bytes buffer length to default: 1024");
        }

        while (this.nss.isStarted()) {
            byte[] buffer = new byte[maxBytesBufferLength];
            byte[] receivedMsg = null;
            boolean totalNumberOfBytes = true;

            try {
                int totalNumberOfBytes1;
                while ((totalNumberOfBytes1 = this.in.read(buffer)) >= 0) {
                    receivedMsg = Arrays.copyOfRange(buffer, 0, totalNumberOfBytes1);
                    if (receivedMsg.length != 0 && receivedMsg != null) {
                        String e = new String(receivedMsg);
                        Messages messages = new Messages();
                        messages.setDetails(e);
                        String time = DateTimeUtil.getTimeNow();
                        this.nss.addMessages(messages);
                        this.nss.updateTableExchanges(new Object[]{time, "Recv", "", Integer.valueOf(receivedMsg.length), e});
                    }
                }

                if (totalNumberOfBytes1 == -1) {
                    this.time = System.currentTimeMillis() - this.time;
                    System.out.println("The end of the stream has been reached! End connection: " + this.socket.getRemoteSocketAddress().toString() + " (livetime=" + String.format("%.2f", Float.valueOf((float) (this.time / 1000L))) + "s)");
                    return;
                }
            } catch (IOException e) {
                LOGGER.warning("An error has occurred when trying to read bytes from the input stream!");
            }
        }

    }
}
