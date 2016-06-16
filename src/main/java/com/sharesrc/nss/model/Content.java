/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.model;

/**
 * The model for HTTP Content
 *
 * @author sou
 * @since 2013
 */
public class Content {

    String hex = "";
    String ascii = "";
    String decompress = "";

    public Content() {
    }

    public String getHex() {
        return this.hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getAscii() {
        return this.ascii;
    }

    public void setAscii(String ascii) {
        this.ascii = ascii;
    }

    public String getDecompress() {
        return this.decompress;
    }

    public void setDecompress(String decompress) {
        this.decompress = decompress;
    }
}
