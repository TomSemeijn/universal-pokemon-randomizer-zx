package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

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

    private RSyntaxTextArea editor;

    public JythonKeyListener(RSyntaxTextArea editor)
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
            while(newCaretPos < text.length() && text.charAt(newCaretPos) == '\t')
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
            if(caretPos >= text.length())
            {
                lineStart = caretPos;
                lineEnd = caretPos;
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
                        catch(BadLocationException exception){ System.out.println("bad location haha"); }
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
        //text resize - CTR->SHIFT->LESS/GREATER
        else if((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0 && (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0 && (e.getKeyCode() == KeyEvent.VK_COMMA || e.getKeyCode() == KeyEvent.VK_LESS || e.getKeyCode() == KeyEvent.VK_PERIOD || e.getKeyCode() == KeyEvent.VK_GREATER))
        {
            Font fnt = editor.getFont();
            int diff = 4;
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_COMMA:
                case KeyEvent.VK_LESS:
                    editor.setFont(fnt.deriveFont(fnt.getStyle(), fnt.getSize() - diff));
                    break;
                case KeyEvent.VK_PERIOD:
                case KeyEvent.VK_GREATER:
                    editor.setFont(fnt.deriveFont(fnt.getStyle(), fnt.getSize() + diff));
                    break;
            }
            e.consume();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
