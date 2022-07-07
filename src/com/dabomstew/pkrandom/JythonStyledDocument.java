package com.dabomstew.pkrandom;

import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//from https://stackoverflow.com/questions/14400946/how-to-change-the-color-of-specific-words-in-a-jtextpane

public class JythonStyledDocument extends DefaultStyledDocument {
    private StyleContext styleContext;
    private Style defaultStyle;
    private Style commentStyle;
    private Style keywordStyle;

    private Style funcStyle;

    private Style stringStyle;

    private Style boolStyle;

    private static String[] keywords = {
            "def", "import", "from", "return", "for", "in", "if", "else", "elif", "match", "case", "not"
    };

    public JythonStyledDocument() {
        styleContext = new StyleContext();
        defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        commentStyle = styleContext.addStyle("comment", null);
        StyleConstants.setForeground(commentStyle, new Color(85, 150, 6));
        StyleConstants.setItalic(commentStyle, true);
        keywordStyle = styleContext.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, Color.blue);
        StyleConstants.setBold(keywordStyle, true);
        funcStyle = styleContext.addStyle("function", null);
        StyleConstants.setForeground(funcStyle, new Color(178, 60, 178));
        stringStyle = styleContext.addStyle("string", null);
        StyleConstants.setForeground(stringStyle, new Color(211, 144, 116));
        boolStyle = styleContext.addStyle("string", null);
        StyleConstants.setForeground(boolStyle, new Color(204, 120, 50));
        StyleConstants.setBold(boolStyle, true);
    }

    public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offset, str, a);
        refreshDocument();
    }

    public void remove (int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        refreshDocument();
    }

    private static class HiliteWord {

        int _position;
        String _word;

        public HiliteWord(String word, int position) {
            _position = position;
            _word = word;
        }
    }

    private synchronized void refreshDocument() throws BadLocationException {
        String text = getText(0, getLength());
        setCharacterAttributes(0, text.length(), defaultStyle, true);

        setStyleOf(funcStyle, findFunctions(text));
        setStyleOf(keywordStyle, findKeywords(text));
        setStyleOf(boolStyle, findBools(text));

        List<HiliteWord>[] commentsAndStrings = findStringsAndComments(text);
        setStyleOf(commentStyle, commentsAndStrings[1]);
        setStyleOf(stringStyle, commentsAndStrings[0]);
    }

    private synchronized void setStyleOf(Style style, List<HiliteWord> words)
    {
        for(HiliteWord word : words) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), style, true);
        }
    }

    private static List<HiliteWord> findKeywords(String content) {
        return findWords(content, (str -> isKeyword(str)));
    }

    private static List<HiliteWord> findBools(String content)
    {
        return findWords(content, (str -> str.trim().equals("True") || str.trim().equals("False")));
    }

    private interface StrCheck{
        public boolean op(String str);
    }

    private static List<HiliteWord> findWords(String content, StrCheck check)
    {
        content += " ";
        List<HiliteWord> hiliteWords = new ArrayList<HiliteWord>();
        int lastWhitespacePosition = 0;
        String word = "";
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
                lastWhitespacePosition = index;
                if(word.length() > 0) {
                    if(check.op(word)) {
                        hiliteWords.add(new HiliteWord(word,(lastWhitespacePosition - word.length())));
                    }
                    word="";
                }
            }
            else {
                word += ch;
            }
        }
        return hiliteWords;
    }

    private static List<HiliteWord> findFunctions(String content)
    {
        content += " ";
        List<HiliteWord> hiliteWords = new ArrayList<HiliteWord>();
        int lastWhitespacePosition = 0;
        String word = "";
        HiliteWord lastWord = null;
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            if(!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) {
                lastWhitespacePosition = index;
                if(word.length() > 0) {
                    lastWord = new HiliteWord(word, lastWhitespacePosition - word.length());
                    word="";
                }
            }
            if(ch == '(')
            {
                if(lastWord != null)
                {
                    hiliteWords.add(lastWord);
                    lastWord = null;
                }
            }
            else {
                word += ch;
                lastWord = null;
            }
        }
        return hiliteWords;
    }

    private static List<HiliteWord>[] findStringsAndComments(String content)
    {
        content += "\n ";
        List<HiliteWord> strings = new ArrayList<HiliteWord>();
        List<HiliteWord> comments = new ArrayList<HiliteWord>();
        int commentStart = -1;
        int lastQuote = -1;
        char quoteType = '\0';
        boolean escaped = false;
        boolean inComment = false;
        char[] data = content.toCharArray();

        for(int index=0; index < data.length; index++) {
            char ch = data[index];
            switch(ch)
            {
                case '\\':
                    escaped = true;
                    break;
                case '#':
                    if(lastQuote == -1 && !inComment)
                    {
                        inComment = true;
                        commentStart = index;
                    }
                    break;
                case '\n':
                    if(inComment)
                    {
                        String str = new String(data, commentStart, (index + 1) - commentStart);
                        comments.add(new HiliteWord(str, commentStart));
                        commentStart = -1;
                        inComment = false;
                    }
                    break;
                case '"':
                case '\'':
                    if(!escaped) {
                        if (lastQuote == -1) {
                            if(!inComment)
                            {
                                lastQuote = index;
                                quoteType = ch;
                            }
                        }
                        else if(quoteType == ch)
                        {
                            String str = new String(data, lastQuote, (index + 1) - lastQuote);
                            strings.add(new HiliteWord(str, lastQuote));
                            lastQuote = -1;
                            quoteType = '\0';
                        }
                    }
                    break;
            }
            if(escaped && ch != '\\'){ escaped = false; }
        }
        if(lastQuote != -1)
        {
            String str = new String(data, lastQuote, data.length - 1 - lastQuote);
            strings.add(new HiliteWord(str, lastQuote));
        }

        return new List[]{ strings, comments };
    }

    private static final boolean isKeyword(String word) {
        String realWord = word.trim();
        for(String keyword : keywords)
        {
            if(realWord.equals(keyword)){ return true;}
        }
        return false;
    }

}
