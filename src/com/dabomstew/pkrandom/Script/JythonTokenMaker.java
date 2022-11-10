package com.dabomstew.pkrandom.Script;
import jdk.nashorn.internal.runtime.Scope;
import org.fife.ui.rsyntaxtextarea.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class JythonTokenMaker extends AbstractTokenMaker {

    public JythonSyntaxDocument doc;

    private int currentTokenStart = 0;
    private int currentTokenType = Token.NULL;

    private String cachedText = null;
    private enum ScopeType
    {
        FUNCTION,
        CLASS,
        GLOBAL,
        OTHER //loops and such, have their own variables but inherit everything else
    }

    private class Scope{

        public class Function
        {
            private String name;
            private int declaredAt;
            public List<String> arguments = new ArrayList<>();

            public Function(String name, int declaredAt)
            {
                this.name = name;
                this.declaredAt = declaredAt;
            }

            public String getName(){ return this.name; }
            public int getDeclaredAt(){ return this.declaredAt; }

            @Override
            public String toString()
            {
                String result = "function \""+this.name+"\" with arguments [";
                for(String arg : this.arguments) //add arguments
                {
                    result += "\""+arg+"\", ";
                }
                if(this.arguments.size() > 0) //remove trailing comma and space
                {
                    result = result.substring(0, result.length() - 2);
                }
                return result + "] declared at "+this.declaredAt;
            }
        }
        public class Class
        {
            private String name;
            private int declaredAt;
            public List<String> members = new ArrayList<>();
            public List<Function> methods = new ArrayList<>();

            public Class(String name, int declaredAt)
            {
                this.name = name;
                this.declaredAt = declaredAt;
            }

            public String getName() { return this.name; }
            public int getDeclaredAt(){ return this.declaredAt; }

            public boolean hasMethod(String name)
            {
                for(Function f : this.methods)
                    if(f.name.equals(name))
                        return true;
                return false;
            }

            @Override
            public String toString()
            {
                String result = "Class \""+this.name+"\" with members [";
                for(String mem : this.members)
                {
                    result += "\""+mem+"\", ";
                }
                if(this.members.size() > 0)
                {
                    result = result.substring(0, result.length() - 2);
                }
                result += "] and methods [";
                for(Function meth : this.methods)
                {
                    result += "{ " + meth.toString() + " }, ";
                }
                if(this.methods.size() > 0)
                {
                    result = result.substring(0, result.length() - 2);
                }
                result += "] declared at "+this.declaredAt;
                return result;
            }
        }

        private int start, end, tabLevel;
        private List<Function> funcs;
        private List<Class> classes;

        private List<String> locals;

        private List<Scope> children;

        private ScopeType type;
        private Function thisFunc = null;
        private Class thisClass = null;

        private Scope parent;

        public Scope(int start, int end, int tabLevel)
        {
            this.start = start;
            this.end = end;
            this.parent = null;
            this.tabLevel = tabLevel;
            this.thisFunc = null;
            this.thisClass = null;
            this.type = ScopeType.GLOBAL;
            funcs = new ArrayList<>();
            classes = new ArrayList<>();
            children = new ArrayList<>();
            locals = new ArrayList<>();
        }
        private Scope(Scope parent, Function thisFunc, int start, int end, int tabLevel){
            this.start = start;
            this.end = end;
            this.parent = parent;
            this.tabLevel = tabLevel;
            this.thisClass = null;
            this.thisFunc = thisFunc;
            this.type = ScopeType.FUNCTION;
            funcs = new ArrayList<>();
            classes = new ArrayList<>();
            children = new ArrayList<>();
            locals = new ArrayList<>();
        }

        private Scope(Scope parent, Class thisClass, int start, int end, int tabLevel){
            this.start = start;
            this.end = end;
            this.parent = parent;
            this.tabLevel = tabLevel;
            this.thisClass = thisClass;
            this.thisFunc = null;
            this.type = ScopeType.CLASS;
            funcs = new ArrayList<>();
            classes = new ArrayList<>();
            children = new ArrayList<>();
            locals = new ArrayList<>();
        }

        private Scope(Scope parent, int start, int end, int tabLevel){
            this.start = start;
            this.end = end;
            this.parent = parent;
            this.tabLevel = tabLevel;
            this.thisClass = thisClass;
            this.thisFunc = null;
            this.type = ScopeType.OTHER;
            funcs = new ArrayList<>();
            classes = new ArrayList<>();
            children = new ArrayList<>();
            locals = new ArrayList<>();
        }

        public boolean inScope(int pos)
        {
            return pos >= start && pos < end;
        }

        public List<Function> getFuncs()
        {
            ArrayList<Function> toReturn = new ArrayList<>(this.funcs);
            if(this.parent != null)
            {
                toReturn.addAll(this.parent.getFuncs());
            }
            return toReturn;
        }

        public List<Class> getClasses()
        {
            ArrayList<Class> toReturn = new ArrayList<>(this.classes);
            if(this.parent != null)
            {
                toReturn.addAll(this.parent.getClasses());
            }
            return toReturn;
        }

        public void addFunc(Function toAdd) { this.funcs.add(toAdd); }
        public void addClass(Class toAdd) { this.classes.add(toAdd); }
        public void addLocal(String toAdd) { this.locals.add(toAdd); }

        public Scope getLowestScopeOf(int pos)
        {
            if(!this.inScope(pos)){ return null; }

            Scope childInScope = null;
            for(Scope s : children)
            {
                if(s.inScope(pos))
                {
                    childInScope = s;
                    break;
                }
            }

            return (childInScope == null) ? this : childInScope.getLowestScopeOf(pos);
        }

        public int getTabLevel(){ return this.tabLevel; }

        public Scope getParent() { return this.parent; }

        public Scope addChild(Function func, int start, int end, int tabLevel)
        {
            Scope added = new Scope(this, func, start, end, tabLevel);
            children.add(added);
            return added;
        }

        public Scope addChild(Class cls, int start, int end, int tabLevel)
        {
            Scope added = new Scope(this, cls, start, end, tabLevel);
            children.add(added);
            return added;
        }

        public Scope addChild(int start, int end, int tabLevel)
        {
            Scope added = new Scope(this, start, end, tabLevel);
            children.add(added);
            return added;
        }

        public void setEnd(int end)
        {
            this.end = end;
        }

        public ScopeType getType() { return this.type; }
        public Function getThisFunction() { return this.thisFunc; }
        public Class getThisClass() { return this.thisClass; }

        private boolean inheritArguments() { return this.type == ScopeType.OTHER; }

        public List<String> getArguments()
        {
            ArrayList<String> result = new ArrayList<>();
            switch(this.type)
            {
                case FUNCTION:
                    result.addAll(this.thisFunc.arguments);
                    break;
                case OTHER:
                    result.addAll(this.parent.getArguments());
                    break;
            }
            return result;
        }

        public List<String> getlocals()
        {
            ArrayList<String> toReturn = new ArrayList<>(this.locals);
            if(this.parent != null)
            {
                toReturn.addAll(this.parent.getlocals());
            }
            return toReturn;
        }

        public Function getLastFunction()
        {
            if(this.funcs.size() > 0)
            {
                return this.funcs.get(this.funcs.size() - 1);
            }
            return null;
        }

        public Class getLastClass()
        {
            if(this.classes.size() > 0)
            {
                return this.classes.get(this.classes.size() - 1);
            }
            return null;
        }

        @Override
        public String toString()
        {
            String result = "";
            for(int k = 0; k < this.tabLevel; k++)
            {
                result += "\t";
            }
            switch(this.type)
            {
                case GLOBAL:
                    result += "Global scope";
                    break;
                case FUNCTION:
                    result += "Function scope";
                    result += " { " + this.thisFunc.toString() + " }";
                    break;
                case CLASS:
                    result += "Class scope";
                    result += " { " + this.thisClass.toString() + " }";
                    break;
                case OTHER:
                    result += "Other scope";
                    break;
                default:
                    result += "UNKNOWN SCOPE TYPE";
                    break;
            }
            result += " from "+this.start+" to "+this.end+" tablevel "+this.tabLevel;
            if(this.funcs.size() > 0)
            {
                result += " with functions [";
                for(Function func : this.funcs)
                {
                    result += "{ "+func.toString() + " }, ";
                }
                result = result.substring(0, result.length() - 2);
            }
            if(this.classes.size() > 0)
            {
                result += " with classes [";
                for(Class cls : this.classes)
                {
                    result += "{ "+cls.toString() + " }, ";
                }
                result = result.substring(0, result.length() - 2);
            }
            if(this.children.size() > 0)
            {
                for(Scope child : this.children)
                {
                    result += "\n"+child.toString();
                }
            }
            return result;
        }
    }
    Scope globalScope = null;

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
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
            else if(start < end) {
                String full = cachedText;
                String fullSeg = new String(segment.array);
                String segPart = fullSeg.substring(start, end + 1);
                boolean valid = true;
                if((start >= full.length() || end >= full.length()) || !full.substring(start, end + 1).equals(segPart))
                {
                    valid = false;
                    int segStartLine = start;
                    int segEndLine = end;
                    while(segStartLine > 0 && fullSeg.charAt(segStartLine - 1) != '\n') { segStartLine--; }
                    while(segEndLine < fullSeg.length() && fullSeg.charAt(segEndLine) != '\n') { segEndLine++; }
                    String segLine = fullSeg.substring(segStartLine, segEndLine);

                    int offset = this.doc.getLastEditOffset();
                    int fullStart = offset;
                    int fullEnd = offset;
                    while(fullStart > 0 && full.charAt(fullStart - 1) != '\n') { fullStart--; }
                    while(fullEnd < full.length() && full.charAt(fullEnd) != '\n') { fullEnd++; }
                    String fullLine = full.substring(fullStart, fullEnd);

                    if(fullLine.equals(segLine))
                    {
                        System.out.println(segLine);
                        start += fullStart;
                        end += fullStart;
                        if(startOffset < fullStart)
                            startOffset += fullStart;
                        valid = true;
                    }
                }
                if (valid && (cachedText != null && cachedText.length() > 0 && start < cachedText.length() && end < cachedText.length()))
                {
                    //get the line and the current word from the inputs
                    int startLn = start;
                    int endLn = end;
                    while (startLn > 0 && full.charAt(startLn) != '\n') {
                        startLn--;
                    }
                    while (endLn < full.length() - 1 && full.charAt(endLn) != '\n') {
                        endLn++;
                    }
                    if (full.charAt(startLn) == '\n') {
                        startLn++;
                    }
                    if (endLn > startLn) //skip if empty line
                    {
                        String line = full.substring(startLn, endLn);
                        String part = full.substring(start, end + 1);
                        int lineOffset = startOffset - startLn;

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
                        else
                            tokenType = getScopedTokenType(globalScope.getLowestScopeOf(start), part, line, lineOffset);
                    }
                }
            }

        }
        super.addToken(segment, originalStart, originalEnd, tokenType, originalStartOffset);
    }

    private int getScopedTokenType(Scope myScope, String part, String line, int lineOffset)
    {
        if(myScope != null) {
            //check if it's an argument
            if(myScope.getArguments().contains(part))
                return Token.MARKUP_CDATA;
            //check if it's a function
            for (Scope.Function func : myScope.getFuncs())
                if (func.name.equals(part))
                    return Token.FUNCTION;
            //check if it's a class
            for(Scope.Class cls : myScope.getClasses())
                if(cls.name.equals(part))
                    return Token.DATA_TYPE;
            //check if it's a local variable
            for(String local : myScope.getlocals())
                if(local.equals(part))
                    return Token.VARIABLE;
            //check if there's a '.' before the word
            if(lineOffset > 0 && line.charAt(lineOffset - 1) == '.')
            {
                //get the word before the '.'
                int lastWordStart = lineOffset - 2;
                while(lastWordStart > 0 && (RSyntaxUtilities.isLetterOrDigit(line.charAt(lastWordStart - 1)) || line.charAt(lastWordStart - 1) == '_'))
                    lastWordStart--;
                String lastWord = line.substring(lastWordStart, lineOffset - 1);

                //if the last word was a class, try to find this word in its statics
                for(Scope.Class cls : myScope.getClasses())
                    if(cls.name.equals(lastWord))
                        return cls.hasMethod(part) ? Token.FUNCTION : (cls.members.contains(part) ? Token.MARKUP_ENTITY_REFERENCE : Token.IDENTIFIER);
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

        if(this.doc != null) {
            try {
                String newText = doc.getText(0, doc.getLength());
                if (!newText.equals(this.cachedText)) {
                    this.cachedText = newText;
                    onTextUpdate();
                }
            } catch (BadLocationException e) {
            }
        }

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

                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ',':
                        case '[':
                        case ']':
                        case '.':
                        case ':':
                            currentTokenType = Token.OPERATOR;
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

                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ',':
                        case '[':
                        case ']':
                        case '.':
                        case ':':
                            currentTokenType = Token.OPERATOR;
                            break;

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

                case Token.OPERATOR:
                    switch (c) {

                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ',':
                        case '[':
                        case ']':
                        case '.':
                        case ':':
                            addToken(text, currentTokenStart,i-1, Token.OPERATOR, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            break; //still operators, but do add a token for each

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

                            // Anything not currently handled - mark as identifier
                            currentTokenType = Token.IDENTIFIER;

                    } // End of switch (c).

                default: // Should never happen
                case Token.IDENTIFIER:

                    switch (c) {

                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ',':
                        case '[':
                        case ']':
                        case '.':
                        case ':':
                            addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.OPERATOR;
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

                        case '(':
                        case ')':
                        case '{':
                        case '}':
                        case ',':
                        case '[':
                        case ']':
                        case '.':
                        case ':':
                            addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.OPERATOR;
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

    private void onTextUpdate()
    {
        //TODO: set up a structure of classes (with members) and functions that have
        // been defined and imported so they can be highlighted. Do this by detecting scope
        // and then caching the start and end positions of those scopes so the token methods
        // can easily check what definitions are available at their locations

        //initialize new global scope
        globalScope = new Scope(0, cachedText.length() - 1, 0);
        Scope currentScope = globalScope;

        //find imported classes
        boolean inDoubleStr = false;
        boolean inSingleStr = false;
        boolean inComment = false;
        boolean escaping = false;
        boolean wasInStringOrComment = false;

        String currentWord = "";
        boolean inWhitespace = false;

        boolean inFuncDef = false;
        boolean inImportLine = false;
        boolean inImportFromDef = false;
        String importSource = "";
        int tabLevel = 0;
        boolean startLine = false;

        int lastNewline = 0;

        for(int k = 0; k < cachedText.length(); k++)
        {
            final char c = cachedText.charAt(k);

            //move up in scope when reaching lower tab levels
            if(startLine)
            {
                //increment tab level
                if(c == '\t')
                {
                    tabLevel++;
                    continue;
                }
                //once tab level is known, move up in scope as needed
                else if(!RSyntaxUtilities.isWhitespace(c))
                {
                    startLine = false;
                    while(tabLevel < currentScope.tabLevel)
                    {
                        currentScope.setEnd(lastNewline);
                        currentScope = currentScope.parent;
                    }
                }
            }

            //figure out when we're in strings or comments while parsing through
            switch(c)
            {
                case '"':
                    if(!escaping && !inSingleStr && !inComment)
                    {
                        inDoubleStr = !inDoubleStr;
                    }
                    break;
                case '\'':
                    if(!escaping && !inDoubleStr && !inComment)
                    {
                        inSingleStr = !inSingleStr;
                    }
                    break;
                case '#':
                    if(!inDoubleStr && !inSingleStr)
                    {
                        inComment = true;
                    }
                    break;
                case '\n':
                    inComment = false;
                    startLine = true;
                    tabLevel = 0;
                    break;
            }
            escaping = (c == '\\' && !escaping && !inComment);
            boolean inStr = inSingleStr || inDoubleStr;

            //finish word when entering whitespace or when this is the last character or when entering a string/comment or when the next character is a scope-opening colon
            final boolean enterWhitespace = (RSyntaxUtilities.isWhitespace(c) || c == '\n') && !inWhitespace;
            final boolean wordAtEnd = !(inStr || inComment) && (k == cachedText.length() - 1 && !inWhitespace);
            final boolean enterStrOrComment = ((inStr || inComment) && !wasInStringOrComment) && !inWhitespace;
            final boolean enterScope = !(inStr || inComment) && cachedText.charAt(k) == ':';
            if((enterWhitespace && !inFuncDef) || wordAtEnd || enterStrOrComment || enterScope)
            {
                //this word is a function definition if the last word was a "def" keyword and we're entering a scope
                if(inFuncDef && enterScope)
                {
                    //get definition information
                    int openArgs = currentWord.indexOf('(');
                    int closeArgs = currentWord.indexOf(')');

                    //verify definition is correct
                    if(openArgs > -1 && closeArgs > -1 && openArgs < closeArgs)
                    {
                        //put the definition into the current scope
                        Scope.Function func = currentScope.new Function(currentWord.substring(0, openArgs), lastNewline);
                        String argList = (closeArgs > openArgs + 1) ? currentWord.substring(openArgs + 1, closeArgs) : "";
                        String[] splitArgs = argList.split(",");
                        for(int argIndex = 0; argIndex < splitArgs.length; argIndex++)
                        {
                            splitArgs[argIndex] = splitArgs[argIndex].trim();
                            if(splitArgs[argIndex].length() > 0)
                            {
                                func.arguments.add(splitArgs[argIndex]);
                            }
                        }
                        currentScope.addFunc(func);
                    }

                    inFuncDef = false;
                }
                //this is an imported class if we've passed a from and an import keyword
                else if(inImportFromDef)
                {
                    try
                    {
                        //find the static methods and members of the class
                        Class<?> cls = Class.forName(importSource+"."+currentWord);
                        Scope.Class addedClass = currentScope.new Class(currentWord, lastNewline);
                        for(Method meth : cls.getMethods())
                            if(Modifier.isStatic(meth.getModifiers()))
                                addedClass.methods.add(currentScope.new Function(meth.getName(), lastNewline));
                        for(Field fld : cls.getFields())
                            if(Modifier.isStatic(fld.getModifiers()))
                                addedClass.members.add(fld.getName());
                        //actually add the class
                        currentScope.addClass(addedClass);
                    }
                    catch(ClassNotFoundException e) {} //do nothing, this wasn't a real class
                }

                //look for funciton definitions
                else if(currentWord.equals("def"))
                {
                    inFuncDef = true;
                }

                //look for imports
                else if(currentWord.equals("from"))
                {
                    inImportLine = true;
                }
                else if(inImportLine)
                {
                    if(currentWord.equals("import"))
                    {
                        inImportFromDef = true;
                    }
                    else
                    {
                        importSource = currentWord;
                    }
                }

                //look for locals
                else
                {
                    for(int i = k; i < cachedText.length(); i++)
                    {
                        char curC = cachedText.charAt(i);
                        if(curC == '=')
                        {
                            currentScope.addLocal(currentWord);
                            break;
                        }
                        if(!RSyntaxUtilities.isWhitespace(curC))
                        {
                            break;
                        }
                    }
                }

                //reset the word
                currentWord = "";
            }
            //continue building word
            else
            {
                currentWord += c;
            }

            //try to open scopes (needs to be after finding functions and classes through the word parser)
            if(c == ':')
            {
                if(!inSingleStr && !inDoubleStr)
                {
                    int nextNewline = k;
                    while(nextNewline < cachedText.length() - 1 && cachedText.charAt(nextNewline) != '\n'){ nextNewline++; }
                    Scope.Function lastFunc = currentScope.getLastFunction();
                    Scope.Class lastClass = currentScope.getLastClass();
                    if(lastFunc != null || lastClass != null)
                    {
                        int lastFuncDecl = lastFunc != null ? lastFunc.getDeclaredAt() : -1;
                        int lastClassDecl = lastClass != null ? lastClass.getDeclaredAt() : -1;
                        if(lastFuncDecl == lastNewline) //if this scope is the last declared function
                            currentScope = currentScope.addChild(lastFunc, lastNewline, cachedText.length() - 1, tabLevel + 1);
                        else if(lastClassDecl == lastNewline) //if this scope is the last declared class
                            currentScope = currentScope.addChild(lastClass, lastNewline, cachedText.length() - 1, tabLevel + 1);
                    }
                    else //if there was no function or class, see if this is a scope character and add an "other"-type scope for things like loops
                    {
                        boolean isScope = false;
                        char lastChecked = 0;
                        for(int i = k + 1; i < cachedText.length(); i++)
                        {
                            char nextC = cachedText.charAt(i);
                            lastChecked = nextC;
                            if(nextC == '\n')
                            {
                                isScope = true;
                                break;
                            }
                            else if(!RSyntaxUtilities.isWhitespace(nextC))
                            {
                                isScope = false;
                                break;
                            }
                        }
                        if(isScope)
                        {
                            //create scope
                            currentScope = currentScope.addChild(lastNewline, cachedText.length() - 1, tabLevel + 1);

                            //find any local variables declared in loop definitions
                            String declaration = cachedText.substring(lastNewline, nextNewline);
                            for(String lcl : findLocalsFromScopeDeclaration(declaration))
                                currentScope.addLocal(lcl);
                        }
                    }
                }
            }

            //reset future word parsing when going onto a new line
            if(c == '\n')
            {
                inFuncDef = false;
                inImportLine = false;
                inImportFromDef = false;
                currentWord = "";
                lastNewline = k;
            }

            //keep track of past state
            inWhitespace = RSyntaxUtilities.isWhitespace(c);
            wasInStringOrComment = inStr || inComment;
        }

        //System.out.println(this.globalScope);
    }

    private static List<String> findLocalsFromScopeDeclaration(String scopeDecl)
    {
        ArrayList<String> result = new ArrayList<>();
        String firstWord = "";

        //do some parsing to find the first word and then find the locals based on what that word was
        for(int k = 0; k < scopeDecl.length(); k++)
        {
            char c = scopeDecl.charAt(k);
            if(firstWord.length() == 0 && (RSyntaxUtilities.isWhitespace(c) || c == '\n')) { continue; } //skip leading whitespace
            if(!RSyntaxUtilities.isLetterOrDigit(c) && c != '_')
            {
                if(firstWord.equals("for")) {
                    int inKeyword = scopeDecl.indexOf(" in "); //with spaces so that it must be its own word
                    if(inKeyword > -1)
                    {
                        String list = scopeDecl.substring(k, inKeyword + 1); //add 1 to the end so it includes the character before the space
                        String[] vars = list.trim().split(",");
                        for(String v : vars)
                            result.add(v.trim());
                    }
                }
                //break the parsing loop after the first word, if variables were found, they are in the list now
                break;
            }
            else
            {
                firstWord += c;
            }
        }

        //return the result
        return result;
    }
}
