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

    private static String[] keywords = {
            "def", "import", "from", "return"
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
        final List<HiliteWord> keywords = findKeywords(text);

        setCharacterAttributes(0, text.length(), defaultStyle, true);
        for(HiliteWord word : keywords) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), keywordStyle, true);
        }

        final List<HiliteWord> functions = findFunctions(text);
        for(HiliteWord word : functions) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), funcStyle, true);
        }

        final List<HiliteWord> comments = findComments(text);
        for(HiliteWord word : comments) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), commentStyle, true);
        }

        final List<HiliteWord> strings = findStrings(text);
        for(HiliteWord word : strings) {
            int p0 = word._position;
            setCharacterAttributes(p0, word._word.length(), stringStyle, true);
        }
    }

    private static List<HiliteWord> findKeywords(String content) {
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
                    if(isKeyword(word)) {
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

    private static List<HiliteWord> findComments(String content) {
        content += "\n";
        List<HiliteWord> hiliteWords = new ArrayList<HiliteWord>();
        char[] data = content.toCharArray();

        boolean inComment = false;
        int commentStart = -1;
        for (int index = 0; index < data.length; index++) {
            if (inComment) {
                if(data[index] == '\n'){
                    String str = new String(data, commentStart, index - commentStart);
                    hiliteWords.add(new HiliteWord(str,(commentStart)));
                    inComment = false;
                }
            } else {
                inComment = data[index] == '#';
                if(inComment){ commentStart = index; }
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

    private static List<HiliteWord> findStrings(String content)
    {
        content += " ";
        List<HiliteWord> found = new ArrayList<HiliteWord>();
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
                    inComment = true;
                    break;
                case '\n':
                    inComment = false;
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
                            found.add(new HiliteWord(str, lastQuote));
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
            found.add(new HiliteWord(str, lastQuote));
        }

        return found;
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
