/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.model;

import java.util.HashMap;

/**
 * The model for sent message
 *
 * @author sou
 * @since 2013
 */
public class Send {

    String statusCode = "";
    HashMap<String, String> headers = new HashMap();
    Content content = new Content();

    public Send() {
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
