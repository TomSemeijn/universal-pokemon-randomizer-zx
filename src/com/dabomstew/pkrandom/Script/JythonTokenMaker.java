package com.dabomstew.pkrandom.Script;
import org.fife.ui.rsyntaxtextarea.*;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class JythonTokenMaker extends AbstractTokenMaker {

    private int currentTokenStart = 0;
    private int currentTokenType = Token.NULL;

    private String[] builtinFuncs = {
            "abs", "aiter", "all", "any", "anext", "ascii", "bin", "bool", "breakpoint", "bytearray", "vars", "zip",
            "bytes", "callable", "chr", "classmethod", "compile", "complex", "delattr", "dict", "dir", "divmod", "enumerate",
            "eval", "exec", "filter", "float", "format", "frozenset", "getattr", "globals", "hasattr", "hash", "help", "hex",
            "id", "input", "int", "isinstance", "issubclass", "iter", "len", "list", "locals", "map", "max", "memoryview", "min",
            "next", "object", "oct", "open", "ord", "pow", "print", "property", "range", "repr", "reversed", "round", "set", "setattr", "slice",
            "sorted", "staticmethod", "str", "sum", "super", "tuple", "type"
    };

    private boolean IsBuiltinFunc(String word) {
        return Arrays.asList(builtinFuncs).contains(word);
    }

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

        return tokenMap;
    }

    @Override
    public synchronized void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        if (tokenType == Token.IDENTIFIER) {
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
            else
            {
                //get the line and the current word from the inputs
                String full = new String(segment.array);
                int startLn = startOffset;
                int endLn = startOffset;
                while(startLn > 0 && full.charAt(startLn) != '\n'){ startLn--; }
                while(endLn < full.length() - 1 && full.charAt(endLn) != '\n'){ endLn++; }
                if(full.charAt(startLn) == '\n'){ startLn++; }
                if(endLn > startLn) { //skip if empty line

                    String line = full.substring(startLn, endLn);
                    String part = full.substring(start, end + 1);
                    int lineOffset = startOffset - startLn;

                    //import formatting
                    int importIndex = line.indexOf("import");
                    int fromIndex = line.indexOf("from");
                    if(fromIndex > -1 && lineOffset > fromIndex) //if this is an import line
                    {
                        if(importIndex == -1 || importIndex > lineOffset) //between from and import - it's the module location
                        {
                            tokenType = Token.ANNOTATION;
                        }
                        else if(importIndex > -1 && importIndex < lineOffset) //after import - it's the class name
                        {
                            tokenType = Token.DATA_TYPE;
                        }
                    }
                }
            }

        }
        super.addToken(segment, start, end, tokenType, startOffset);
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

                default: // Should never happen
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
}
