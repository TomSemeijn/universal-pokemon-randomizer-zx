package com.dabomstew.pkrandom;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class JythonKeyListener implements KeyListener {

    private JTextArea editor;

    public JythonKeyListener(JTextArea editor)
    {
        this.editor = editor;
    }

    @Override
    public void keyPressed(KeyEvent e){

        //home key accounting for leading tabs
        if(e.getKeyCode() == KeyEvent.VK_HOME){

            String text = editor.getText();
            int caretPos = editor.getCaretPosition();
            if(caretPos == 0){ return; }

            //find the start of the line
            int lineStart = caretPos;
            for(int k = caretPos - 1; k >= 0; k--)
            {
                if(text.charAt(k) == '\n')
                {
                    break;
                }
                lineStart = k;
            }

            //find last leading tab position
            int newCaretPos = lineStart;
            while(text.charAt(newCaretPos) == '\t')
            {
                newCaretPos++;
            }

            //go to start of line if already at last leading tab
            if(caretPos == newCaretPos)
            {
                newCaretPos = lineStart;
            }

            //set new caret pos
            editor.setCaretPosition(newCaretPos);

            //consume event
            e.consume();
        }
        //copying, pasting, and cutting full lines when there is no selection
        else if(editor.getSelectedText() == null && (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_V || e.getKeyCode() == KeyEvent.VK_C) && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)
        {
            final int caretPos = editor.getCaretPosition();
            final String text = editor.getText();
            if(text.length() == 0){ return; }

            int lineStart = caretPos;
            while(lineStart > 0 && text.charAt(lineStart - 1) != '\n')
            {
                lineStart--;
            }
            int lineEnd = lineStart + 1;
            while(lineEnd < text.length() && text.charAt(lineEnd - 1) != '\n')
            {
                lineEnd++;
            }
            String line = text.substring(lineStart, lineEnd);
            if(!line.endsWith("\n")){ line += "\n"; }

            //if ctr->x
            if(e.getKeyCode() == KeyEvent.VK_X)
            {
                //remove line from editor
                try{
                    editor.getDocument().remove(lineStart, lineEnd - lineStart);
                }
                catch(BadLocationException exception)
                {}

                //copy line to user clipboard (from https://stackoverflow.com/questions/3591945/copying-to-the-clipboard-in-java)
                StringSelection selection = new StringSelection(line);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                e.consume();
            }
            //if ctr->c
            else if(e.getKeyCode() == KeyEvent.VK_C)
            {
                //copy line to user clipboard (from https://stackoverflow.com/questions/3591945/copying-to-the-clipboard-in-java)
                StringSelection selection = new StringSelection(line);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                e.consume();
            }
            //if ctr->v
            else if(e.getKeyCode() == KeyEvent.VK_V)
            {
                //if text to paste is a single full line
                try{
                    String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    int newlineCount = 0;
                    for(int k = 0; k < data.length(); k++) { if(data.charAt(k) == '\n'){ newlineCount++; } }
                    if(newlineCount == 1 && data.endsWith("\n"))
                    {
                        try{
                            editor.getDocument().insertString(lineStart, data, null);
                        }
                        catch(BadLocationException exception){}
                        e.consume();
                    }
                }
                catch(UnsupportedFlavorException exception){}
                catch(IOException exception){}
            }
            //if ctrl->d
            else if(e.getKeyCode() == KeyEvent.VK_D)
            {
                try{
                    editor.getDocument().insertString(lineStart, line, null);
                }
                catch(BadLocationException exception){}
                e.consume();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
