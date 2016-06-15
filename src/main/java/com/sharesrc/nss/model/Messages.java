//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss.model;

public class Messages {
    Recv recv = new Recv();
    Send send = new Send();
    String details = "";

    public Messages() {
    }

    public Recv getRecv() {
        return this.recv;
    }

    public void setRecv(Recv recv) {
        this.recv = recv;
    }

    public Send getSend() {
        return this.send;
    }

    public void setSend(Send send) {
        this.send = send;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
