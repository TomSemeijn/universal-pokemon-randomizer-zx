package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.util.DynamicIntArray;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import java.lang.reflect.*;

public class JythonSyntaxDocument extends RSyntaxDocument {

    private int lastEditOffset = 0;

    public JythonSyntaxDocument(String syntaxStyle) {
        super(syntaxStyle);
    }

    public JythonSyntaxDocument(TokenMakerFactory tmf, String syntaxStyle) {
        super(tmf, syntaxStyle);
    }

    @Override
    public void setSyntaxStyle(String styleKey) {
        super.setSyntaxStyle(styleKey);
        initTokenMaker();
    }

    @Override
    public void setSyntaxStyle(TokenMaker tokenMaker) {
        super.setSyntaxStyle(tokenMaker);
        initTokenMaker();
    }

    private void initTokenMaker()
    {
        try {
            //big bad accessing a private member from a base class because I need all text in the document to be
            //available from the tokenmaker and I can't edit the source code of the RSyntaxTextArea library to make it protected
            Field privateField = RSyntaxDocument.class.getDeclaredField("tokenMaker");
            privateField.setAccessible(true);
            JythonTokenMaker tokenMaker = (JythonTokenMaker)privateField.get((RSyntaxDocument)this);
            if(tokenMaker != null)
                tokenMaker.doc = this;
        }
        catch(NoSuchFieldException e){}
        catch(IllegalAccessException e){}
    }

    @Override
    protected void fireInsertUpdate(DocumentEvent e) {
        lastEditOffset = e.getOffset();
        super.fireInsertUpdate(e);
    }

    public int getLastEditOffset(){ return this.lastEditOffset; }

}
