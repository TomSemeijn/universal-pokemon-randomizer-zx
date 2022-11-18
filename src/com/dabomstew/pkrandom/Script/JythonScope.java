package com.dabomstew.pkrandom.Script;

import java.util.ArrayList;
import java.util.List;

public class JythonScope {

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

    public class Variable{
        private String name;
        private int declaredAt;

        public Variable(String name, int declaredAt)
        {
            this.name = name;
            this.declaredAt = declaredAt;
        }

        public String getName(){ return this.name; }
        public int getDeclaredAt() { return this.declaredAt; }

        @Override
        public String toString()
        {
            return "<" + name + " declared at " + declaredAt + ">";
        }
    }

    public class Class
    {
        private String name;
        private int declaredAt;
        public List<Variable> members = new ArrayList<>();
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
            return hasMethod(name, Integer.MAX_VALUE);
        }
        public boolean hasMethod(String name, int offset)
        {
            for(Function f : this.methods)
                if(offset >= f.getDeclaredAt() && f.name.equals(name))
                    return true;
            return false;
        }

        @Override
        public String toString()
        {
            String result = "Class \""+this.name+"\" with members [";
            for(Variable mem : this.members)
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
    private List<Function> funcs = new ArrayList<>();
    private List<Class> classes = new ArrayList<>();

    private List<Variable> locals = new ArrayList<>();
    private List<Variable> specialVariables = new ArrayList<>();

    private List<JythonScope> children = new ArrayList<>();

    private ScopeType type;
    private Function thisFunc = null;
    private Class thisClass = null;

    private JythonScope parent;

    public JythonScope(int start, int end, int tabLevel)
    {
        this.start = start;
        this.end = end;
        this.parent = null;
        this.tabLevel = tabLevel;
        this.type = ScopeType.GLOBAL;
    }
    private JythonScope(JythonScope parent, Function thisFunc, int start, int end, int tabLevel){
        this.start = start;
        this.end = end;
        this.parent = parent;
        this.tabLevel = tabLevel;
        this.thisFunc = thisFunc;
        this.type = ScopeType.FUNCTION;
    }

    private JythonScope(JythonScope parent, Class thisClass, int start, int end, int tabLevel){
        this.start = start;
        this.end = end;
        this.parent = parent;
        this.tabLevel = tabLevel;
        this.thisClass = thisClass;
        this.type = ScopeType.CLASS;
    }

    private JythonScope(JythonScope parent, int start, int end, int tabLevel){
        this.start = start;
        this.end = end;
        this.parent = parent;
        this.tabLevel = tabLevel;
        this.type = ScopeType.OTHER;
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
    public void addLocal(Variable toAdd) { this.locals.add(toAdd); }
    public void addSpecialVariable(Variable toAdd) { this.specialVariables.add(toAdd); }

    public JythonScope getLowestScopeOf(int pos)
    {
        if(!this.inScope(pos)){ return null; }

        JythonScope childInScope = null;
        for(JythonScope s : children)
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

    public JythonScope getParent() { return this.parent; }

    public JythonScope addChild(Function func, int start, int end, int tabLevel)
    {
        JythonScope added = new JythonScope(this, func, start, end, tabLevel);
        children.add(added);
        return added;
    }

    public JythonScope addChild(Class cls, int start, int end, int tabLevel)
    {
        JythonScope added = new JythonScope(this, cls, start, end, tabLevel);
        children.add(added);
        return added;
    }

    public JythonScope addChild(int start, int end, int tabLevel)
    {
        JythonScope added = new JythonScope(this, start, end, tabLevel);
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

    public List<Variable> getlocals()
    {
        ArrayList<Variable> toReturn = new ArrayList<>(this.locals);
        if(this.parent != null)
        {
            toReturn.addAll(this.parent.getlocals());
        }
        return toReturn;
    }

    public List<Variable> getSpecialVariables()
    {
        return this.specialVariables;
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

    public int getEnd() { return this.end; }

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
            for(JythonScope child : this.children)
            {
                result += "\n"+child.toString();
            }
        }
        return result;
    }
}