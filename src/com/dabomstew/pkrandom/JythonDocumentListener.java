package com.dabomstew.pkrandom;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class JythonDocumentListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
        Document doc = e.getDocument();

        int offset = e.getOffset();
        int length = e.getLength();

        if(length == 1) //check if single character (only try to add tabs when a single newline is added)
        {
            try {
                String change = doc.getText(offset, 1);
                if(change.equals("\n")) //check if newline (only try to add tabs when a single newline is added)
                {
                    //count necessary tabs
                    String allText = doc.getText(0, doc.getLength());
                    int tabLevel = 0;
                    boolean inComment = false;
                    boolean startLine = true;
                    boolean tabLevelReset = false;
                    boolean hadColon = false;
                    for(int k = 0; k < offset; k++)
                    {
                        char c = allText.charAt(k);
                        switch(c)
                        {
                            case '\n':
                                if(startLine)
                                {
                                    tabLevel = 0;
                                }
                                startLine = true;
                                tabLevelReset = false;
                                inComment = false;
                                hadColon = false;
                                break;
                            case '\t':
                                if(startLine) {
                                    if(!tabLevelReset)
                                    {
                                        tabLevel = 0;
                                        tabLevelReset = true;
                                    }
                                    tabLevel++;
                                }
                                break;
                            case ':':
                                startLine = false;
                                if(!inComment)
                                {
                                    hadColon = true;
                                }
                                break;
                            case '#':
                                startLine = false;
                                hadColon = false;
                                inComment = true;
                                break;
                            default:
                                startLine = false;
                                if(c != ' ')
                                {
                                    hadColon = false;
                                }
                                break;
                        }
                    }
                    if(hadColon)
                    {
                        tabLevel++;
                    }

                    //add tabs
                    if(tabLevel > 0)
                    {
                        int finalTabLevel = tabLevel;
                        Runnable doAddTabs = new Runnable() {
                            @Override
                            public void run() {
                                String tabs = "";
                                for(int k = 0; k < finalTabLevel; k++){ tabs += "\t"; }
                                try
                                {
                                    doc.insertString(offset + 1, tabs, null);
                                }
                                catch (BadLocationException ex) {
                                    return;
                                }
                            }
                        };
                        SwingUtilities.invokeLater(doAddTabs);

                    }
                }
            } catch (BadLocationException ex) {
                return; //should never happen since the event is assumed to give the correct info
            }
        }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
