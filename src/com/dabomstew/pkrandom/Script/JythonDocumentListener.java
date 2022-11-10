package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class JythonDocumentListener implements DocumentListener {

    RSyntaxTextArea editor;
    private boolean updating = false;

    public  JythonDocumentListener(RSyntaxTextArea editor)
    {
        this.editor = editor;
    }

    public void insertUpdate(DocumentEvent e)
    {
        resetHighlighting(e.getDocument());
    }

    public void removeUpdate(DocumentEvent e)
    {
        resetHighlighting(e.getDocument());
    }

    public void changedUpdate(DocumentEvent e)
    {
        resetHighlighting(e.getDocument());
    }

    private void resetHighlighting(Document doc)
    {
        //just reset the text completely to force RSyntaxTextArea to reset all of the highlighting
        if(!updating)
        {
            updating = true;
            SwingUtilities.invokeLater(() -> {
                JythonSyntaxDocument jdoc = (JythonSyntaxDocument)doc;
                if(jdoc != null)
                    jdoc.onTextUpdate();
                int caret = this.editor.getCaretPosition();
                this.editor.setText(this.editor.getText());
                this.editor.setCaretPosition(caret);
                updating = false;
            });
        }
    }

}
