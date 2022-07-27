package com.dabomstew.pkrandom;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JythonKeyListener implements KeyListener {

    private JTextPane editor;

    public JythonKeyListener(JTextPane editor)
    {
        this.editor = editor;
    }

    @Override
    public void keyPressed(KeyEvent e){
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
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
