package com.dabomstew.pkrandom.Script;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.util.DynamicIntArray;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class JythonSyntaxDocument extends RSyntaxDocument {

    private int lastEditOffset = 0;
    private JythonScope globalScope = new JythonScope(0, 0, 0);
    private List<JythonScope.Function> extraGlobalFuncs = new ArrayList<>();
    private List<JythonScope.Class> extraGlobalClasses = new ArrayList<>();


    public JythonSyntaxDocument(String syntaxStyle) {
        super(syntaxStyle);
    }

    public JythonSyntaxDocument(TokenMakerFactory tmf, String syntaxStyle) {
        super(tmf, syntaxStyle);
    }

    @Override
    public void setSyntaxStyle(String styleKey) {
        super.setSyntaxStyle(styleKey);
        initTokenMaker();
    }

    @Override
    public void setSyntaxStyle(TokenMaker tokenMaker) {
        super.setSyntaxStyle(tokenMaker);
        initTokenMaker();
    }

    private void initTokenMaker()
    {
        try {
            //big bad accessing a private member from a base class because I need all text in the document to be
            //available from the tokenmaker and I can't edit the source code of the RSyntaxTextArea library to make it protected
            Field privateField = RSyntaxDocument.class.getDeclaredField("tokenMaker");
            privateField.setAccessible(true);
            JythonTokenMaker tokenMaker = (JythonTokenMaker)privateField.get((RSyntaxDocument)this);
            if(tokenMaker != null)
                tokenMaker.doc = this;
        }
        catch(NoSuchFieldException e){}
        catch(IllegalAccessException e){}
    }

    @Override
    protected void fireInsertUpdate(DocumentEvent e) {
        lastEditOffset = e.getOffset();
        super.fireInsertUpdate(e);
    }

    public int getLastEditOffset(){ return this.lastEditOffset; }

    public String getAllText()
    {
        String full = "";
        try{
            full = this.getText(0, this.getLength());
        }
        catch(BadLocationException e){ }
        return full;
    }

    public void onTextUpdate()
    {
        //TODO: set up a structure of classes (with members) and functions that have
        // been defined and imported so they can be highlighted. Do this by detecting scope
        // and then caching the start and end positions of those scopes so the token methods
        // can easily check what definitions are available at their locations

        //initialize new global scope
        String cachedText = getAllText();
        globalScope = new JythonScope(0, cachedText.length() - 1, 0);
        for(JythonScope.Function f : extraGlobalFuncs)
            globalScope.addFunc(f);
        for(JythonScope.Class c : extraGlobalClasses)
            globalScope.addClass(c);
        JythonScope currentScope = globalScope;

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
                    while(tabLevel < currentScope.getTabLevel())
                    {
                        currentScope.setEnd(lastNewline);
                        currentScope = currentScope.getParent();
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
                        JythonScope.Function func = currentScope.new Function(currentWord.substring(0, openArgs), lastNewline);
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
                        JythonScope.Class addedClass = currentScope.new Class(currentWord, lastNewline);
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
                    JythonScope.Function lastFunc = currentScope.getLastFunction();
                    JythonScope.Class lastClass = currentScope.getLastClass();
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

    public void addExtraGlobalFunc(JythonScope.Function func)
    {
        extraGlobalFuncs.add(func);
    }

    public void addExtraGlobalClass(JythonScope.Class cls)
    {
        extraGlobalClasses.add(cls);
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

    public JythonScope getGlobalScope() { return globalScope; }
}
