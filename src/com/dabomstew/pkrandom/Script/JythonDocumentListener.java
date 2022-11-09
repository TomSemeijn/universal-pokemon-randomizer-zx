package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JythonDocumentListener implements DocumentListener {

    RSyntaxTextArea editor;
    private boolean updating = false;

    public  JythonDocumentListener(RSyntaxTextArea editor)
    {
        this.editor = editor;
    }

    public void insertUpdate(DocumentEvent e)
    {
        resetHighlighting();
    }

    public void removeUpdate(DocumentEvent e)
    {
        resetHighlighting();
    }

    public void changedUpdate(DocumentEvent e)
    {
        resetHighlighting();
    }

    private void resetHighlighting()
    {
        //just reset the text completely to force RSyntaxTextArea to reset all of the highlighting
        if(!updating)
        {
            updating = true;
            SwingUtilities.invokeLater(() -> {
                int caret = this.editor.getCaretPosition();
                this.editor.setText(this.editor.getText());
                this.editor.setCaretPosition(caret);
                updating = false;
            });
        }
    }

}
