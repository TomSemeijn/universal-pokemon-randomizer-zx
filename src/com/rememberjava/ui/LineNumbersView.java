//from http://rememberjava.com/ui/2017/02/19/line_numbers.html
//slightly modified to be more customizable

/* Copyright rememberjava.com. Licensed under GPL 3. See http://rememberjava.com/license */
package com.rememberjava.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

/**
 * Left hand side RowHeaderView for a JEditorPane in a JScrollPane. Highlights
 * the currently selected line. Handles line wrapping, frame resizing.
 */
public class LineNumbersView extends JComponent implements DocumentListener, CaretListener, ComponentListener {

    private static final long serialVersionUID = 1L;

    private JTextComponent editor;

    private Font font;

    private int width;

    private int numberOffset;

    private Color lineColor;

    private Color selectedColor;

    private Color backgroundColor;

    public LineNumbersView(JTextComponent editor, int width, int numberOffset, Color lineColor, Color selectedColor, Color backgroundColor) {
        this.editor = editor;
        this.width = width;
        this.numberOffset = numberOffset;
        this.lineColor = lineColor;
        this.selectedColor = selectedColor;
        this.backgroundColor = backgroundColor;

        editor.getDocument().addDocumentListener(this);
        editor.addComponentListener(this);
        editor.addCaretListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Rectangle clip = g.getClipBounds();
        int startOffset = editor.viewToModel(new Point(0, clip.y));
        int endOffset = editor.viewToModel(new Point(0, clip.y + clip.height));

        g.setColor(backgroundColor);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        while (startOffset <= endOffset) {
            try {
                String lineNumber = getLineNumber(startOffset);
                if (lineNumber != null) {
                    int x = getInsets().left + this.numberOffset;
                    int y = getOffsetY(startOffset);

                    font = font != null ? font : new Font(Font.MONOSPACED, Font.BOLD, editor.getFont().getSize());
                    g.setFont(font);

                    g.setColor(isCurrentLine(startOffset) ? selectedColor : lineColor);

                    g.drawString(lineNumber, x, y);
                }

                startOffset = Utilities.getRowEnd(editor, startOffset) + 1;
            } catch (BadLocationException e) {
                e.printStackTrace();
                // ignore and continue
            }
        }
    }

    /**
     * Returns the line number of the element based on the given (start) offset
     * in the editor model. Returns null if no line number should or could be
     * provided (e.g. for wrapped lines).
     */
    private String getLineNumber(int offset) {
        Element root = editor.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(offset);
        Element line = root.getElement(index);

        return line.getStartOffset() == offset ? String.format("%3d", index + 1) : null;
    }

    /**
     * Returns the y axis position for the line number belonging to the element
     * at the given (start) offset in the model.
     */
    private int getOffsetY(int offset) throws BadLocationException {
        FontMetrics fontMetrics = editor.getFontMetrics(editor.getFont());
        int descent = fontMetrics.getDescent();

        Rectangle r = editor.modelToView(offset);
        int y = r.y + r.height - descent;

        return y;
    }

    /**
     * Returns true if the given start offset in the model is the selected (by
     * cursor position) element.
     */
    private boolean isCurrentLine(int offset) {
        int caretPosition = editor.getCaretPosition();
        Element root = editor.getDocument().getDefaultRootElement();
        return root.getElementIndex(offset) == root.getElementIndex(caretPosition);
    }

    /**
     * Schedules a refresh of the line number margin on a separate thread.
     */
    private void documentChanged() {
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }

    /**
     * Updates the size of the line number margin based on the editor height.
     */
    private void updateSize() {
        Dimension size = new Dimension(width, editor.getHeight());
        setPreferredSize(size);
        setSize(size);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        documentChanged();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        updateSize();
        documentChanged();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        updateSize();
        documentChanged();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}