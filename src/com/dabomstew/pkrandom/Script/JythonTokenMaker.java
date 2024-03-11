package com.dabomstew.pkrandom.Script;
import org.fife.ui.rsyntaxtextarea.*;
import javax.swing.text.*;

public class JythonTokenMaker extends AbstractTokenMaker {

    public JythonSyntaxDocument doc;
    private int currentTokenStart = 0;
    private int currentTokenType = Token.NULL;

    public static void register() {
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping("text/jython", "com.dabomstew.pkrandom.Script.JythonTokenMaker");
    }

    @Override
    public TokenMap getWordsToHighlight() {

        TokenMap tokenMap = new TokenMap();

        //keyword highlighting
        String[] keywords = {"await", "else", "import", "pass",
                "break", "except", "in", "raise",
                "class", "finally", "is", "return",
                "and", "continue", "for", "lambda", "try",
                "as", "def", "from", "nonlocal", "while",
                "assert", "del", "global", "not", "with",
                "async", "elif", "if", "or", "yield"
        };
        for (String word : keywords) {
            tokenMap.put(word, Token.RESERVED_WORD);
        }

        //boolean literal-style highlighting
        String[] boolWords = {
                "False", "True", "None"
        };
        for (String word : boolWords) {
            tokenMap.put(word, Token.LITERAL_BOOLEAN);
        }
        String[] builtinFuncs = {
                "abs", "aiter", "all", "any", "anext", "ascii", "bin", "bool", "breakpoint", "bytearray", "vars", "zip",
                "bytes", "callable", "chr", "classmethod", "compile", "complex", "delattr", "dict", "dir", "divmod", "enumerate",
                "eval", "exec", "filter", "float", "format", "frozenset", "getattr", "globals", "hasattr", "hash", "help", "hex",
                "id", "input", "int", "isinstance", "issubclass", "iter", "len", "list", "locals", "map", "max", "memoryview", "min",
                "next", "object", "oct", "open", "ord", "pow", "print", "property", "range", "repr", "reversed", "round", "set", "setattr", "slice",
                "sorted", "staticmethod", "str", "sum", "super", "tuple", "type"
        };
        for (String word : builtinFuncs) {
            tokenMap.put(word, Token.FUNCTION);
        }

        return tokenMap;
    }

    @Override
    public synchronized void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {

        int originalStart = start;
        int originalEnd = end;
        int originalStartOffset = startOffset;
        if (tokenType == Token.IDENTIFIER) {

            String full = this.doc.getAllText();
            String segPart = new String(segment.array, start, (end + 1) - start);
            boolean valid = true;
            if ((start >= full.length() || end >= full.length()) || !full.substring(start, end + 1).equals(segPart)) {
                valid = false;
                int segStartLine = start;
                int segEndLine = end;
                while (segStartLine > 0 && segment.array[segStartLine - 1] != '\n' && segment.array[segStartLine - 1] != 0) {
                    segStartLine--;
                }
                while (segEndLine < segment.array.length && segment.array[segEndLine] != '\n' && segment.array[segEndLine] != 0) {
                    segEndLine++;
                }
                String segLine = new String(segment.array, segStartLine, segEndLine - segStartLine);

                int offset = this.doc.getLastEditOffset();
                int fullStart = offset;
                int fullEnd = offset;
                while (fullStart > 0 && full.charAt(fullStart - 1) != '\n') {
                    fullStart--;
                }
                while (fullEnd < full.length() && full.charAt(fullEnd) != '\n') {
                    fullEnd++;
                }
                String fullLine = full.substring(fullStart, fullEnd);

                // When adding a newline, the previous line detection will wrongfully point to the line before the desired one
                while(fullEnd < full.length() && !fullLine.equals(segLine))
                {
                    fullStart = fullEnd + 1;
                    fullEnd = fullStart;
                    while (fullEnd < full.length() && full.charAt(fullEnd) != '\n') {
                        fullEnd++;
                    }
                    fullLine = full.substring(fullStart, fullEnd);
                }

                if (fullLine.equals(segLine)) {
                    start = fullStart + (start - segStartLine);
                    end = start + (originalEnd - originalStart);
                    if (startOffset < fullStart)
                        startOffset += fullStart;
                    valid = true;
                }
            }
            if (valid && start < full.length() && end < full.length()) {

                //get the line and the current word from the inputs
                int startLn = start;
                int endLn = end;
                while (startLn > 0 && full.charAt(startLn) != '\n') {
                    startLn--;
                }
                while (endLn < full.length() && full.charAt(endLn) != '\n') {
                    endLn++;
                }
                if (full.charAt(startLn) == '\n') {
                    startLn++;
                }

                String line = "";
                if (endLn > startLn)
                    line = full.substring(startLn, endLn);
                String part = full.substring(start, end + 1);
                int lineOffset = startOffset - startLn;

                int searchStart = (start > segment.array.length) ? line.indexOf(part) : start;
                int searchEnd = searchStart + part.length() - 1;

                int value = wordsToHighlight.get(segment, originalStart, originalEnd);
                boolean preChangeCheck = lineOffset >= line.length();
                if(!preChangeCheck) {
                    if (value != -1 && line.length() > 0 && !(lineOffset > 0 && line.charAt(lineOffset - 1) == '.')) {
                        tokenType = value;
                    } else if (start <= end) {
                        if (full != null && full.length() > 0 && start < full.length() && end < full.length()) {
                            if (endLn > startLn) //skip if empty line
                            {
                                //import formatting
                                int importIndex = line.indexOf("import");
                                int fromIndex = line.indexOf("from");
                                if (fromIndex > -1 && lineOffset > fromIndex) //if this is an import line
                                {
                                    if (importIndex == -1 || importIndex > lineOffset) //between from and import - it's the module location
                                        tokenType = Token.ANNOTATION;
                                    else if (importIndex > -1 && importIndex < lineOffset) //after import - it's the class name
                                        tokenType = Token.DATA_TYPE;
                                }
                                //scoped formatting
                                else {
                                    JythonScope globalScope = this.doc.getGlobalScope();
                                    if (globalScope != null)
                                        tokenType = getScopedTokenType(globalScope.getLowestScopeOf(start), part, line, lineOffset, start);
                                }
                            }
                        }
                    }
                }

            }
        }

        super.addToken(segment, originalStart, originalEnd, tokenType, originalStartOffset);
    }

    private int getScopedTokenType(JythonScope myScope, String part, String line, int lineOffset, int identifierPosition)
    {
        if(myScope != null) {
            //check if it's an argument
            if(myScope.getArguments().contains(part))
                return Token.MARKUP_CDATA;
            //check if it's a function
            for (JythonScope.Function func : myScope.getFuncs())
                if (identifierPosition >= func.getDeclaredAt() && func.getName().equals(part))
                    return Token.FUNCTION;
            //check if it's a class
            for(JythonScope.Class cls : myScope.getClasses())
                if(identifierPosition >= cls.getDeclaredAt() && cls.getName().equals(part))
                    return Token.DATA_TYPE;
            //check if it's a member within a class definition
            if(myScope.getType() == ScopeType.CLASS)
                for(JythonScope.Variable member : myScope.getThisClass().members)
                    if(identifierPosition >= member.getDeclaredAt() && member.getName().equals(part))
                        return Token.MARKUP_ENTITY_REFERENCE;
            //check if it's a local variable
            for(JythonScope.Variable local : myScope.getlocals())
                if(identifierPosition >= local.getDeclaredAt() && local.getName().equals(part))
                    return Token.VARIABLE;
            //check if it's a @staticmethod modifier within a class scope
            if(myScope.getType() == ScopeType.CLASS && part.equals("@staticmethod"))
                return Token.PREPROCESSOR;
            //check if there's a '.' before the word
            if(lineOffset > 0 && line.charAt(lineOffset - 1) == '.')
            {
                //get the word before the '.'
                int lastWordStart = lineOffset - 2;
                while(lastWordStart > 0 && (RSyntaxUtilities.isLetterOrDigit(line.charAt(lastWordStart - 1)) || line.charAt(lastWordStart - 1) == '_'))
                    lastWordStart--;
                String lastWord = line.substring(lastWordStart, lineOffset - 1);

                //if the last word was a class, try to find this word in its statics
                for(JythonScope.Class cls : myScope.getClasses())
                    if(identifierPosition > cls.getDeclaredAt() && cls.getName().equals(lastWord)) {
                        if (cls.hasMethod(part, identifierPosition)) {
                            return Token.FUNCTION;
                        }
                        for(JythonScope.Variable mem : cls.members)
                            if(identifierPosition >= mem.getDeclaredAt() && mem.getName().equals(part))
                                return Token.MARKUP_ENTITY_REFERENCE;
                        return Token.IDENTIFIER;
                    }
            }
        }
        return Token.IDENTIFIER;
    }

    /**
     * Returns a list of tokens representing the given text.
     *
     * @param text           The text to break into tokens.
     * @param startTokenType The token with which to start tokenizing.
     * @param startOffset    The offset at which the line of tokens begins.
     * @return A linked list of tokens representing <code>text</code>.
     */
    @Override
    public synchronized Token getTokenList(Segment text, int startTokenType, int startOffset) {

        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;
        int end = offset + count;

        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        int newStartOffset = startOffset - offset;

        currentTokenStart = offset;
        currentTokenType  = startTokenType;

        boolean doubleQuote = false;
        boolean singleQuote = false;

        boolean escaped = false;

        for (int i=offset; i<end; i++) {

            char c = array[i];

            switch (currentTokenType) {

                case Token.NULL:

                    currentTokenStart = i;   // Starting a new token here.

                    switch (c) {

                        case ' ':
                        case '\t':
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                        case '\'':
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            doubleQuote = c == '"';
                            singleQuote = c != '"';
                            break;

                        case '#':
                            currentTokenType = Token.COMMENT_EOL;
                            break;

                        default:
                            if (RSyntaxUtilities.isDigit(c)) {
                                currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                                break;
                            }
                            else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }
                            else if(isOperator(c))
                            {
                                currentTokenType = Token.OPERATOR;
                                break;
                            }

                            // Anything not currently handled - mark as an identifier
                            currentTokenType = Token.IDENTIFIER;
                            break;

                    } // End of switch (c).

                    break;

                case Token.WHITESPACE:

                    switch (c) {

                        case ' ':
                        case '\t':
                            break;   // Still whitespace.

                        case '"':
                        case '\'':
                            addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            doubleQuote = c == '"';
                            singleQuote = c != '"';
                            break;

                        case '#':
                            addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.COMMENT_EOL;
                            break;

                        default:   // Add the whitespace token and start anew.

                            if(isOperator(c))
                            {
                                currentTokenType = Token.OPERATOR;
                                break;
                            }

                            addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
                            currentTokenStart = i;

                            if (RSyntaxUtilities.isDigit(c)) {
                                currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                                break;
                            }
                            else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }

                            // Anything not currently handled - mark as identifier
                            currentTokenType = Token.IDENTIFIER;

                    } // End of switch (c).

                    break;

                case Token.OPERATOR:
                    switch (c) {

                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;   // whitespace.

                        case '"':
                        case '\'':
                            addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            doubleQuote = c == '"';
                            singleQuote = c != '"';
                            break;

                        case '#':
                            addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.COMMENT_EOL;
                            break;

                        default:   // Add the whitespace token and start anew.

                            addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                            currentTokenStart = i;

                            if (RSyntaxUtilities.isDigit(c)) {
                                currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
                                break;
                            }
                            else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }
                            else if(isOperator(c))
                            {
                                addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                                currentTokenStart = i;
                                break; //still operators, but do add a token for each
                            }

                            // Anything not currently handled - mark as identifier
                            currentTokenType = Token.IDENTIFIER;

                    } // End of switch (c).

                //default: // Should never happen
                case Token.IDENTIFIER:

                    switch (c) {

                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                        case '\'':
                            addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            doubleQuote = c == '"';
                            singleQuote = c != '"';
                            break;

                        default:
                            if (RSyntaxUtilities.isLetterOrDigit(c) || c=='/' || c=='_') {
                                break;   // Still an identifier of some type.
                            }
                            else if(isOperator(c))
                            {
                                addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                                currentTokenStart = i;
                                currentTokenType = Token.OPERATOR;
                                break;
                            }
                            // Otherwise, we're still an identifier (?).

                    } // End of switch (c).

                    break;

                case Token.LITERAL_NUMBER_DECIMAL_INT:

                    switch (c) {

                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                        case '\'':
                            addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                            doubleQuote = c == '"';
                            singleQuote = c != '"';
                            break;

                        default:

                            if (RSyntaxUtilities.isDigit(c)) {
                                break;   // Still a literal number.
                            }
                            else if(isOperator(c))
                            {
                                addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                                currentTokenStart = i;
                                currentTokenType = Token.OPERATOR;
                                break;
                            }

                            // Otherwise, remember this was a number and start over.
                            addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                            i--;
                            currentTokenType = Token.NULL;

                    } // End of switch (c).

                    break;

                case Token.COMMENT_EOL:
                    i = end - 1;
                    addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
                    // We need to set token type to null so at the bottom we don't add one more token.
                    currentTokenType = Token.NULL;
                    break;

                case Token.LITERAL_STRING_DOUBLE_QUOTE:
                    if (!escaped && ((c=='"' && doubleQuote) || (c=='\'' && singleQuote))) {
                        addToken(text, currentTokenStart,i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset+currentTokenStart);
                        currentTokenType = Token.NULL;
                        doubleQuote = false;
                        singleQuote = false;
                    }
                    break;

            } // End of switch (currentTokenType).

            escaped = (c == '\\') && !escaped;

        } // End of for (int i=offset; i<end; i++).

        switch (currentTokenType) {

            // Remember what token type to begin the next line with.
            case Token.LITERAL_STRING_DOUBLE_QUOTE:
                addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
                break;

            // Do nothing if everything was okay.
            case Token.NULL:
                addNullToken();
                break;

            // All other token types don't continue to the next line...
            default:
                addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
                addNullToken();

        }

        // Return the first token in our linked list.
        return firstToken;
    }

    @Override
    public boolean getShouldIndentNextLineAfter(Token token) {
        return token != null && token.getType() == Token.OPERATOR && token.isSingleChar(':');
    }

    @Override
    public boolean getMarkOccurrencesOfTokenType(int type) {
        return
                type != Token.COMMENT_EOL &&
                        type != Token.PREPROCESSOR &&
                        type != Token.OPERATOR &&
                        type != Token.LITERAL_STRING_DOUBLE_QUOTE &&
                        type != Token.RESERVED_WORD &&
                        type != Token.LITERAL_BOOLEAN &&
                        type != Token.LITERAL_NUMBER_DECIMAL_INT &&
                        type != Token.LITERAL_NUMBER_FLOAT &&
                        type != Token.LITERAL_NUMBER_HEXADECIMAL &&
                        type != Token.LITERAL_CHAR &&
                        type != Token.WHITESPACE
                ;
    }

    private static boolean isOperator(char c)
    {
        return  c == '(' ||
                c == ')' ||
                c == '{' ||
                c == '}' ||
                c == ',' ||
                c == '[' ||
                c == ']' ||
                c == '.' ||
                c == ':' ||
                c == '+' ||
                c == '-' ||
                c == '/' ||
                c == '*' ||
                c == '%';
    }
}
