/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.func.xmleditor;

import com.sharesrc.nss.common.constant.Constants.Colour;
import com.sharesrc.nss.common.constant.Constants.Fonts;
import org.bounce.text.LineNumberMargin;
import org.bounce.text.ScrollableEditorPanel;
import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLFoldingMargin;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The XML TextPane.
 *
 * @author sou
 * @since 2013
 */
public class XmlTextPane extends JScrollPane {

    private static final long serialVersionUID = 765914301979449315L;

    JEditorPane editor = new JEditorPane();
    XMLEditorKit kit = new XMLEditorKit();

    public XmlTextPane() {
        this.editor.setEditorKit(this.kit);
        this.editor.setFont(Fonts.XMLTEXTPANE_DEFAULT);
        this.editor.getDocument().putProperty("tabSize", new Integer(4));
        this.kit.setAutoIndentation(true);
        this.kit.setTagCompletion(true);
        this.editor.getDocument().putProperty("errorHighlighting", new Boolean(true));
        this.kit.setStyle("element-name", Colour.ELEMENT_NAME, 0);
        this.kit.setStyle("element-value", Colour.ELEMENT_VALUE, 1);
        this.kit.setStyle("attribute-name", Colour.ATTRIBUTE_NAME, 0);
        this.kit.setStyle("attribute-value", Colour.ATTRIBUTE_VALUE, 1);
        this.kit.setStyle("Comment", Colour.COMMENT, 2);
        this.kit.setStyle("CDATA", Colour.CDATA, 1);
        ScrollableEditorPanel editorPanel = new ScrollableEditorPanel(this.editor);
        this.setViewportView(editorPanel);
        JPanel rowHeader = new JPanel(new BorderLayout());

        try {
            rowHeader.add(new XMLFoldingMargin(this.editor), "East");
        } catch (IOException ex) {
            System.out.println("(IOException) XMLTextPane rowHeader add XMLFoldingMargin failed!");
        }

        rowHeader.add(new LineNumberMargin(this.editor), "West");
        this.setRowHeaderView(rowHeader);
    }

    public JEditorPane getEditor() {
        return this.editor;
    }

    public XMLEditorKit getKit() {
        return this.kit;
    }

    public void appendLine(String msg) {
        this.append("\n" + msg);
    }

    public void append(String msg) {
        this.editor.setText(this.editor.getText() + msg);
        this.getViewport().revalidate();
    }
}
