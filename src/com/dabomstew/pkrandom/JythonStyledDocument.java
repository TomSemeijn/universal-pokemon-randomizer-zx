package com.dabomstew.pkrandom;

import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//from https://stackoverflow.com/questions/14400946/how-to-change-the-color-of-specific-words-in-a-jtextpane

public class JythonStyledDocument extends DefaultStyledDocument {
    private StyleContext styleContext;
    private Style defaultStyle;
    private Style commentStyle;
    private Style keywordStyle;

    private Style funcStyle;

    private Style stringStyle;

    private Style boolStyle;

    private Style argStyle;

    private Style memberStyle;

    private Style numericLiteralStyle;

    private Style importStyle;

    private Style classStyle;

    private static String[] keywords = {
            "def", "import", "from", "return", "for", "in", "if", "else", "elif", "match", "case", "not", "class", "self", "pass", "del"
    };

    public JythonStyledDocument() {
        styleContext = new StyleContext();
        defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(defaultStyle, Color.white);
        commentStyle = styleContext.addStyle("comment", null);
        StyleConstants.setForeground(commentStyle, new Color(87, 166, 74));
        StyleConstants.setItalic(commentStyle, true);
        keywordStyle = styleContext.addStyle("keyword", null);
        StyleConstants.setForeground(keywordStyle, new Color(86, 156, 214));
        StyleConstants.setBold(keywordStyle, true);
        funcStyle = styleContext.addStyle("function", null);
        StyleConstants.setForeground(funcStyle, new Color(220, 220, 170));
        stringStyle = styleContext.addStyle("string", null);
        StyleConstants.setForeground(stringStyle, new Color(211, 144, 116));
        boolStyle = styleContext.addStyle("boolean", null);
        StyleConstants.setForeground(boolStyle, new Color(204, 120, 50));
        StyleConstants.setBold(boolStyle, true);
        argStyle = styleContext.addStyle("arg", null);
        StyleConstants.setForeground(argStyle, new Color(154, 154, 154));
        memberStyle = styleContext.addStyle("member", null);
        StyleConstants.setForeground(memberStyle, new Color(190, 183, 255));
        numericLiteralStyle = styleContext.addStyle("numericLiteral", null);
        StyleConstants.setForeground(numericLiteralStyle, new Color(181, 206, 168));
        importStyle = styleContext.addStyle("import", null);
        StyleConstants.setForeground(importStyle, new Color(154, 154, 154));
        StyleConstants.setItalic(importStyle, true);
        StyleConstants.setBold(importStyle, true);
        classStyle = styleContext.addStyle("class", null);
        StyleConstants.setForeground(classStyle, new Color(78, 201, 176));
        StyleConstants.setBold(classStyle, true);
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

        List<HiliteWord>[] commentsAndStrings = findStringsAndComments(text);
        setStyleOf(argStyle, findArguments(text, commentsAndStrings[0], commentsAndStrings[1]));
        setStyleOf(memberStyle, findMembers(text));

        setStyleOf(funcStyle, findFunctions(text));
        setStyleOf(importStyle, findImportLines(text, commentsAndStrings[0], commentsAndStrings[1]));
        setStyleOf(classStyle, findClasses(text, commentsAndStrings[0], commentsAndStrings[1]));
        setStyleOf(classStyle, findImportedClasses(text, commentsAndStrings[0], commentsAndStrings[1]));
        setStyleOf(keywordStyle, findKeywords(text));
        setStyleOf(boolStyle, findBools(text));
        setStyleOf(numericLiteralStyle, findNumericLiterals(text));

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
        return findWords(content, ((str, i) -> isKeyword(str)));
    }

    private static List<HiliteWord> findBools(String content)
    {
        return findWords(content, ((str, i) -> str.trim().equals("True") || str.trim().equals("False")));
    }

    private interface StrCheck{
        public boolean op(String str, int pos);
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
                    if(check.op(word, index - word.length())) {
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

    private static List<HiliteWord> findMembers(String content)
    {
        char[] data = content.toCharArray();
        List<HiliteWord> members = findWords(content, (str, i) -> i > 0 ? data[i - 1] == '.' : false);
        members.removeIf(word -> {

            //find start of line
            int lineStart = 0;
            for(int k = word._position; k >= 0; k--)
            {
                char c = data[k];
                if(c == '\n'){ lineStart = k + 1; break; }
            }

            //find from keyword within the line
            String[] splitLine = content.substring(lineStart, word._position).split(" ");
            for(String str : splitLine)
            {
                if(str.equals("from")){ return true; }
            }
            return false;
        });
        return members;
    }

    private static List<HiliteWord> findArguments(String content, List<HiliteWord> comments, List<HiliteWord> strings)
    {
        List<HiliteWord> toReturn = new ArrayList<>();

        List<HiliteWord> defs = findWords(content, ((str, i) -> str.trim().equals("def")));
        for(HiliteWord definition : defs)
        {
            if(!intersectsCommentOrString(definition, comments, strings))
            {
                //find argument list
                int argListStart = 0;
                int argListEnd = 0;
                int funcStart = 0;
                for(int k = definition._position + definition._word.length(); k < content.length(); k++)
                {
                    char c = content.charAt(k);
                    if(c == '\n'){ funcStart = k + 1; break; }
                    if(c == '(' && argListStart == 0)
                    {
                        argListStart = k;
                    }
                    else if(c == ')')
                    {
                        argListEnd = k;
                    }
                }
                String argListStr = content.substring(argListStart + 1, argListEnd);
                String[] argList = argListStr.split(",");
                for(int k = 0; k < argList.length; k++)
                {
                    argList[k] = argList[k].trim();
                }

                //find tab level of definition
                int tabLevel = 0;
                for(int k = definition._position; k >= 0; k--)
                {
                    char c = content.charAt(k);
                    if(c == '\n'){ break; }
                    if(c == '\t'){ tabLevel++; }
                    else { tabLevel = 0; }
                }

                //find end of function (or end of code if there is no end yet)
                boolean lineStart = true;
                boolean resetTabs = false;
                char lastChar = '.';
                int currentTabLevel = 0;
                int funcEnd = -1;
                for(int k = funcStart; k < content.length(); k++)
                {
                    char c = content.charAt(k);
                    boolean foundEnd = false;
                    switch(c)
                    {
                        case '\n':
                            if(lastChar == '\n')
                            {
                                currentTabLevel = 0;
                            }
                            lineStart = true;
                            resetTabs = false;
                            break;
                        case '\t':
                            if(lineStart)
                            {
                                if(!resetTabs)
                                {
                                    currentTabLevel = 0;
                                    resetTabs = true;
                                }
                                currentTabLevel++;
                            }
                            break;
                        default:
                            if(lineStart)
                            {
                                lineStart = false;
                                if(!resetTabs)
                                {
                                    currentTabLevel = 0;
                                    resetTabs = true;
                                }
                                if(currentTabLevel <= tabLevel)
                                {
                                    foundEnd = true;
                                    funcEnd = k - 1;
                                }
                            }
                            break;
                    }
                    lastChar = c;
                    if(foundEnd) { break; }
                }
                if(funcEnd == -1) { funcEnd = content.length() - 1; }

                //find all mentions of arguments within the function (including the definition)
                for(String arg : argList)
                {
                    int finalFuncEnd = funcEnd;
                    List<HiliteWord> argMentions = findWords(content, ((str, i) -> str.trim().equals(arg) && i >= definition._position && i + str.length() - 1 <= finalFuncEnd));
                    toReturn.addAll(argMentions);
                }
            }
        }
        return toReturn;
    }

    private static List<HiliteWord> findClasses(String content, List<HiliteWord> comments, List<HiliteWord> strings)
    {
        List<HiliteWord> nameDefs = new ArrayList<>();

        List<HiliteWord> classKeywords = findWords(content, (str, i) -> str.equals("class"));
        for(HiliteWord keyword : classKeywords)
        {
            if(intersectsCommentOrString(keyword, comments, strings)){ continue; } //skip if class is commented out

            int nameStart = -1;
            for(int k = keyword._position + keyword._word.length(); k < content.length(); k++)
            {
                char c = content.charAt(k);
                if(c != ' ' && c != '\t')
                {
                    nameStart = k;
                    break;
                }
            }
            if(nameStart == -1) { break; }
            int nameEnd = content.length() - 1;
            for(int k = nameStart; k < content.length(); k++)
            {
                char c = content.charAt(k);
                if(c == ' ' || c == '\t' || c == ':')
                {
                    nameEnd = k;
                    break;
                }
            }
            nameDefs.add(new HiliteWord(content.substring(nameStart, nameEnd), nameStart));
        }

        //create the result and add all class definitions to it
        List<HiliteWord> toReturn = new ArrayList<>();
        toReturn.addAll(nameDefs);

        //find all usages of defined classes after their definitions and add them to the result
        for(HiliteWord def : nameDefs)
        {
            toReturn.addAll(findWords(content, (str, i) -> str.trim().equals(def._word) && i > def._position));
        }

        return toReturn;
    }

    private static List<HiliteWord> findImportedClasses(String content, List<HiliteWord> comments, List<HiliteWord> strings)
    {
        List<HiliteWord> toReturn = new ArrayList<>();

        List<HiliteWord> importKeywords = findWords(content, (str, i) -> str.equals("import"));
        for(HiliteWord def : importKeywords)
        {
            if(intersectsCommentOrString(def, comments, strings)){ continue; } //skip if import is commented out

            final int classNameStart = def._position + def._word.length() + 1;
            int classNameEnd = classNameStart;
            while(content.length() > classNameEnd && content.charAt(classNameEnd) != '\n'){ classNameEnd++; }

            String importedClass = content.substring(classNameStart, classNameEnd).trim();
            toReturn.addAll(findWords(content, (str, i) -> str.trim().equals(importedClass) && i >= classNameStart - 1));
        }

        return toReturn;
    }

    private static List<HiliteWord> findNumericLiterals(String content)
    {
        return findWords(content, (str, i) -> Pattern.compile("-?\\d+(\\.\\d+)?").matcher(str.trim()).matches()); //from https://www.baeldung.com/java-check-string-number
    }

    private static List<HiliteWord> findImportLines(String content, List<HiliteWord> comments, List<HiliteWord> strings)
    {
        List<HiliteWord> toReturn = new ArrayList<>();

        List<HiliteWord> importKeywords = findWords(content, (str, i) -> str.equals("import"));
        for(HiliteWord keyword : importKeywords)
        {
            if(!intersectsCommentOrString(keyword, comments, strings))
            {
                int lineStart = 0;
                for(int k = keyword._position; k >= 0; k--)
                {
                    if(content.charAt(k) == '\n')
                    {
                        lineStart = k + 1;
                        break;
                    }
                }

                int lineEnd = content.length() - 1;
                for(int k = keyword._position + keyword._word.length(); k < content.length(); k++)
                {
                    if(content.charAt(k) == '\n'){
                        lineEnd = k;
                        break;
                    }
                }

                String line = content.substring(lineStart, lineEnd);
                if(line.contains("from ") && line.contains(" import "))
                {
                    toReturn.add(new HiliteWord(line, lineStart));
                }
            }
        }

        return toReturn;
    }

    private static final boolean isKeyword(String word) {
        String realWord = word.trim();
        for(String keyword : keywords)
        {
            if(realWord.equals(keyword)){ return true;}
        }
        return false;
    }

    private static boolean intersectsCommentOrString(HiliteWord test, List<HiliteWord> comments, List<HiliteWord> strings)
    {
        for(HiliteWord str : comments)
        {
            if(intersect(test, str)) { return true; }
        }
        for(HiliteWord str : strings)
        {
            if(intersect(test, str)) { return true; }
        }
        return false;
    }

    private static boolean intersect(HiliteWord a , HiliteWord b)
    {
        final int aStart = a._position;
        final int aEnd = aStart + a._word.length() - 1;

        final int bStart = b._position;
        final int bEnd = bStart + b._word.length() - 1;

        return (aStart >= bStart && aStart <= bEnd) ||
                (aEnd >= bStart && aEnd <= bEnd) ||
                (bStart >= aStart && bStart <= aEnd) ||
                (bEnd >= aStart && bEnd <= aEnd);
    }
}
