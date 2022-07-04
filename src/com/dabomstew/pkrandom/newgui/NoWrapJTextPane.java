package com.dabomstew.pkrandom.newgui;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

//from https://stackoverflow.com/questions/7156038/how-to-turn-off-jtextpane-line-wrapping
public class NoWrapJTextPane extends JTextPane {

    public NoWrapJTextPane(StyledDocument doc) {
        super(doc);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Only track viewport width when the viewport is wider than the preferred width
        return getUI().getPreferredSize(this).width
                <= getParent().getSize().width;
    };

    @Override
    public Dimension getPreferredSize() {
        // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
        return getUI().getPreferredSize(this);
    };
}