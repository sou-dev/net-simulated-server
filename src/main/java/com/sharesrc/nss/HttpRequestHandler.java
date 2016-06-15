//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sharesrc.nss;

import com.sharesrc.nss.common.util.*;
import com.sharesrc.nss.model.Messages;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class HttpRequestHandler implements HttpHandler {
    static final Logger LOGGER = LogUtil.getInstance().getLogger(HttpRequestHandler.class.getName());
    NETSimulateServer server = null;
    HttpExchange he = null;
    Messages messages = null;

    public HttpRequestHandler(NETSimulateServer server) {
        this.server = server;
    }

    public void handle(HttpExchange he) throws IOException {
        this.he = he;
        this.processRequest();
        this.makeResponse();
    }

    private String genElement(String tagName) {
        return this.genElement(tagName, false);
    }

    private String genElement(String tagName, boolean isTagClosed) {
        return "<" + (isTagClosed ? "/" : "") + tagName + ">";
    }

    private String genElement(String tagName, String value) {
        return "<" + tagName + ">" + (!value.contains("\"") && !value.contains("&") && !value.contains("\'") && !value.contains("<") && !value.contains(">") ? value : "<![CDATA[" + value + "]]>") + "</" + tagName + ">";
    }

    private String genElement(String tagName, List<String> values) {
        return this.genElement(tagName, this.convertListToString(values));
    }

    private String convertListToString(List<String> values) {
        String result = "";

        String value;
        for (Iterator it = values.iterator(); it.hasNext(); result = result + value + ", ") {
            value = (String) it.next();
        }

        result = result.substring(0, result.lastIndexOf(",")).trim();
        return result;
    }

    private boolean isEqual(Messages m1, Messages m2) {
        return m1.getRecv().getMethod().equals(m2.getRecv().getMethod()) && m1.getRecv().getContextPath().equals(m2.getRecv().getContextPath()) && m1.getRecv().getQuerry().equals(m2.getRecv().getQuerry()) && m1.getRecv().getProtocol().equals(m2.getRecv().getProtocol()) && m1.getRecv().getHeaders().equals(m2.getRecv().getHeaders());
    }

    private Messages getCorrespondingMessages(Messages messages, List<Messages> listMessages) {
        Iterator it = listMessages.iterator();

        while (it.hasNext()) {
            Messages m = (Messages) it.next();
            if (this.isEqual(messages, m)) {
                return m;
            }
        }

        return null;
    }

    private void processRequest() {
        this.messages = new Messages();
        StringBuffer sb = new StringBuffer();
        sb.append("\n" + this.genElement("Messages"));
        sb.append("\n\t" + this.genElement("Recv"));
        this.messages.getRecv().setMethod(this.he.getRequestMethod());
        sb.append("\n\t\t" + this.genElement("Method", this.he.getRequestMethod()));
        this.messages.getRecv().setContextPath(this.he.getHttpContext().getPath());
        sb.append("\n\t\t" + this.genElement("Context-path", this.he.getHttpContext().getPath()));
        if (this.he.getRequestURI().getQuery() != null) {
            this.messages.getRecv().setQuerry(this.he.getRequestURI().getQuery());
            sb.append("\n\t\t" + this.genElement("Querry", this.he.getRequestURI().getQuery()));
        }

        this.messages.getRecv().setProtocol(this.he.getProtocol());
        sb.append("\n\t\t" + this.genElement("Protocol", this.he.getProtocol()));
        sb.append("\n\t\t" + this.genElement("Headers"));
        Headers headers = this.he.getRequestHeaders();
        Iterator a = headers.keySet().iterator();

        String is;
        while (a.hasNext()) {
            is = (String) a.next();
            this.messages.getRecv().getHeaders().put(is, this.convertListToString(headers.get(is)));
            sb.append("\n\t\t\t" + this.genElement(is, headers.get(is)));
        }

        sb.append("\n\t\t" + this.genElement("Headers", true));
        InputStream is1 = this.he.getRequestBody();
        boolean a1 = false;
        int maxBytesBufferLength = PropUtil.getMaxBytesBufferLength();
        if (maxBytesBufferLength <= 0) {
            maxBytesBufferLength = Integer.parseInt("1024");
            System.out.println("Max bytes buffer length is invalid!\nSet max bytes buffer length to default: 1024");
        }

        byte[] buffer = new byte[maxBytesBufferLength];
        byte[] receivedMsg = (byte[]) null;

        try {
            int a2;
            try {
                while ((a2 = is1.read(buffer)) >= 0) {
                    receivedMsg = Arrays.copyOfRange(buffer, 0, a2);
                }
            } catch (IOException ex) {
                LOGGER.warning("Read request body failed!");
            }
        } finally {
            try {
                if (is1 != null) {
                    is1.close();
                }

                is = null;
            } catch (IOException ex) {
                ;
            }

        }

        String time;
        if (receivedMsg != null) {
            time = BHSUtil.bytesToHex(receivedMsg);
            String ascii = BHSUtil.convertHexToString(BHSUtil.bytesToHex(receivedMsg));
            String decompress = GZipUtil.decompress(BHSUtil.bytesToHex(receivedMsg));
            sb.append("\n\t\t" + this.genElement("Content"));
            this.messages.getRecv().getContent().setHex(time);
            sb.append("\n\t\t\t" + this.genElement("Hex", time));
            this.messages.getRecv().getContent().setAscii(ascii);
            sb.append("\n\t\t\t" + this.genElement("Ascii", ascii));
            if (decompress.length() > 0) {
                this.messages.getRecv().getContent().setDecompress(decompress);
                sb.append("\n\t\t\t" + this.genElement("Decompress", decompress));
            }

            sb.append("\n\t\t" + this.genElement("Content", true));
        }

        sb.append("\n\t" + this.genElement("Recv", true));
        sb.append("\n\t" + this.genElement("Send"));
        sb.append("\n\t" + this.genElement("Send", true));
        sb.append("\n" + this.genElement("Messages", true));
        time = DateTimeUtil.getTimeNow();
        this.messages.setDetails(sb.toString());
        this.server.addMessages(this.messages);
        this.server.updateTableExchanges(new Object[]{time, "Recv", this.messages.getRecv().getMethod(), Integer.valueOf(this.messages.getRecv().getContent().getHex().length() / 2), this.messages.getRecv().getContent().getDecompress().length() == 0 ? this.messages.getRecv().getContent().getAscii() : this.messages.getRecv().getContent().getDecompress()});
        LOGGER.info(sb.toString());
    }

    private void makeResponse() {
        try {
            SAXParserFactory e = SAXParserFactory.newInstance();
            SAXParser saxParser = e.newSAXParser();
            String fPath = "/" + (this.he.getRequestURI().getHost() == null ? "" : this.he.getRequestURI().getHost()) + this.he.getHttpContext().getPath();
            if (fPath.endsWith("/")) {
                fPath = fPath.concat(PropUtil.getDefaultDocument());
            }

            if (!this.server.hmapAllPages.keySet().contains(fPath)) {
                LOGGER.warning("Can not mapping \'" + fPath + "\' to corresponding file!!!");
                return;
            }

            File correspondingFile = new File((String) this.server.hmapAllPages.get(fPath));
            FileInputStream inputStream = new FileInputStream(correspondingFile);
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            HttpRequestHandler.XMLParser xmlParser = new HttpRequestHandler.XMLParser();
            saxParser.parse(is, xmlParser);
            Messages m = this.getCorrespondingMessages(this.messages, xmlParser.all);
            String responseContentHex = m.getSend().getContent().getHex();
            Headers headers = this.he.getResponseHeaders();
            Iterator responseContentBytes = m.getSend().getHeaders().keySet().iterator();

            while (responseContentBytes.hasNext()) {
                String statusCode = (String) responseContentBytes.next();
                headers.set(statusCode, (String) m.getSend().getHeaders().get(statusCode));
            }

            int statusCode1 = 200;

            try {
                statusCode1 = Integer.parseInt(m.getSend().getStatusCode());
            } catch (NumberFormatException ex) {
                LOGGER.warning("(NumberFormatException) HTTP response status code parsing failed!");
            }

            this.he.sendResponseHeaders(statusCode1, (long) responseContentHex.length());
            byte[] responseContentBytes1 = BHSUtil.hexStringToByteArray(responseContentHex);
            OutputStream os = this.he.getResponseBody();
            os.write(responseContentBytes1);
            os.flush();
            os = null;
            this.he.close();
            this.he = null;
        } catch (FileNotFoundException ex) {
            LOGGER.severe("Corresponding XML file not found!!!");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.severe("Encoding (UTF-8) failed!!!");
        } catch (ParserConfigurationException ex) {
            LOGGER.severe("(ParserConfigurationException) ");
        } catch (SAXException ex) {
            LOGGER.severe("SAXParser parse input source error!!!\n");
        } catch (IOException ex) {
            LOGGER.severe("(IOException)");
        }

    }

    class XMLParser extends DefaultHandler {
        static final int PARENT_ELEMENT = 2;
        static final int GRAND_PARENT_ELEMENT = 3;
        List<Messages> all = new ArrayList();
        Stack<String> elementStack = new Stack();
        Stack<Object> objectStack = new Stack();

        public XMLParser() {
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            this.elementStack.push(qName);
            if (qName.equalsIgnoreCase("Messages")) {
                Messages messages = new Messages();
                this.all.add(messages);
                this.objectStack.push(messages);
            }

        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            this.elementStack.pop();
            if (qName.equalsIgnoreCase("Messages")) {
                this.objectStack.pop();
            }

        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            if (value.length() != 0) {
                if (this.objectStack.size() != 0 && this.objectStack.peek() instanceof Messages) {
                    Messages messages = (Messages) this.objectStack.peek();
                    if (this.currentElementParent().equalsIgnoreCase("Recv") || this.currentElementGrandParent().equalsIgnoreCase("Recv")) {
                        if (this.currentElement().equalsIgnoreCase("Method")) {
                            messages.getRecv().setMethod(value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Context-path")) {
                            messages.getRecv().setContextPath(value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Querry")) {
                            messages.getRecv().setQuerry(value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Protocol")) {
                            messages.getRecv().setProtocol(value);
                        }

                        if (this.currentElementParent().equalsIgnoreCase("Headers")) {
                            messages.getRecv().getHeaders().put(this.currentElement(), value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Hex")) {
                            messages.getRecv().getContent().setHex(value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Ascii")) {
                            messages.getRecv().getContent().setAscii(value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Decompress")) {
                            messages.getRecv().getContent().setDecompress(value);
                        }
                    }

                    if (this.currentElementParent().equalsIgnoreCase("Send") || this.currentElementGrandParent().equalsIgnoreCase("Send")) {
                        if (this.currentElement().equalsIgnoreCase("Status-code")) {
                            messages.getSend().setStatusCode(value);
                        }

                        if (this.currentElementParent().equalsIgnoreCase("Headers")) {
                            messages.getSend().getHeaders().put(this.currentElement(), value);
                        }

                        if (this.currentElement().equalsIgnoreCase("Hex")) {
                            messages.getSend().getContent().setHex(value);
                        }
                    }

                }
            }
        }

        private String currentElement() {
            return (String) this.elementStack.peek();
        }

        private String currentElementParent() {
            return this.levelUpCurrentElement(2);
        }

        private String currentElementGrandParent() {
            return this.levelUpCurrentElement(3);
        }

        private String levelUpCurrentElement(int level) {
            return this.elementStack.size() < level ? "" : (String) this.elementStack.get(this.elementStack.size() - level);
        }
    }
}
