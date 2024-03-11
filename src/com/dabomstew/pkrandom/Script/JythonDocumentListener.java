package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaBase;
import org.fife.ui.rtextarea.RUndoManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEdit;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class JythonDocumentListener implements DocumentListener {

    RSyntaxTextArea editor;

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
        // Defer a call to onTextUpdate to update the scope datastructure for syntax highlighting
        SwingUtilities.invokeLater(() -> {
            JythonSyntaxDocument jdoc = (JythonSyntaxDocument)doc;
            if(jdoc != null)
            {
                jdoc.onTextUpdate();
            }
        });
    }

}
