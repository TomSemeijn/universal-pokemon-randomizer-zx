package com.dabomstew.pkrandom.Script.Doc;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DocGenerator {

    private static class Argument
    {
        public String name = "";
        public String type= "";
        public String descr= "";
        public String defaultValue= "";

        @Override
        public String toString()
        {
            return "<argument \""+name+"\" of type \""+type+"\" with descr \""+descr+"\""+(defaultValue.length() > 0 ? " an default \""+defaultValue+"\"" : "")+">";
        }
    }

    private static class Return
    {
        public String type= "";
        public String descr= "";

        @Override
        public String toString()
        {
            return "<return type \""+type+"\" with descr \""+descr+"\">";
        }
    }

    private static class Function
    {
        public String name= "";
        public String descr= "";
        public List<Argument> args = new ArrayList<>();
        public Return returnValue;

        @Override
        public String toString()
        {
            String result = "<function \""+name+"\" with descr \""+descr+"\" and args [";
            for(Argument arg : args)
                result += arg + ", ";
            result = result.substring(0, result.length() - 2);
            result += "]" + (returnValue != null ? " returns "+returnValue: "")+">";
            return result;
        }
    }

    private static class Member
    {
        public String name= "";
        public String descr= "";
        public String type= "";
        public boolean isStatic = false;

        @Override
        public String toString()
        {
            return "<member \""+name+"\" of type \""+type+"\" with descr \""+descr+"\""+(isStatic ? " is static" : "")+">";
        }
    }

    private static class Method
    {
        public Function func;
        public boolean isStatic = false;

        @Override
        public String toString()
        {
            return "<method "+func+(isStatic ? " is static" : "") + ">";
        }
    }

    private static class Class
    {
        public String name= "";
        public String descr= "";
        public String category= "";
        public List<Method> methods = new ArrayList<>();
        public List<Member> members = new ArrayList<>();
        public Class baseClass = null;

        @Override
        public String toString()
        {
            String result = "<class \""+name+"\" with descr \""+descr+"\" and methods [";
            for(Method meth : methods)
                result += meth + ", ";
            result = result.substring(0, result.length() - 2);
            result += "] and members [";
            for(Member mem : members)
                result += mem + ", ";
            result = result.substring(0, result.length() - 2);
            result += "]>";
            return result;
        }
    }

    private static class ScriptDocument
    {
        public List<Function> funcs = new ArrayList<>();
        public List<Class> classes = new ArrayList<>();

        @Override
        public String toString()
        {
            String result = "<document with functions [";
            for(Function func : funcs)
                result += func + ", ";
            result = result.substring(0, result.length() - 2);
            result += "] and classes [";
            for(Class cls : classes)
                result += cls + ", ";
            result = result.substring(0, result.length() - 2);
            result += "]>";
            return result;
        }
    }

    public static String generateDocumentationFiles(String sourceFile, String styleFile, String dstFolder)
    {
        //load the data
        ScriptDocument doc = generateDocument(sourceFile);

        //create function and class pages
        for(Function func : doc.funcs)
            createFunctionPage(dstFolder, func, doc);
        for(Class cls : doc.classes)
            createClassPage(dstFolder, cls, doc);

        //create main page
        createMainPage(dstFolder, doc);

        //copy over the style file
        copyStyleFile(styleFile, dstFolder);

        //return main page filename
        return getMainPageFileName(dstFolder);
    }

    private static String getFunctionHTML(Function func, ScriptDocument doc, int subFolderLevel) { return getFunctionHTML(func, doc, subFolderLevel, "Function"); }

    private static String getFunctionHTML(Function func, ScriptDocument doc, int subFolderLevel, String identifier)
    {
        String result = "<div class=\"funcContent\" id=\"Function_"+func.name+"\"><div id=\"Function_"+func.name+func.args.size()+"\">";
        String fullName = getFullName(func);
        result += "<h3>"+identifier+": "+fullName+"</h3>";
        result += "<p>"+getLinked(func.descr, doc, subFolderLevel)+"</p>";
        if(func.args.size() > 0) {
            result += "<div class=\"argumentList\"><h4>Arguments</h4>";
            for (Argument arg : func.args)
                result += "<div class=\"argument\"><p>\t<b>" + arg.name + (arg.defaultValue.length() > 0 ? " = " + arg.defaultValue : "") + "</b> : " + getLinked(arg.type, doc, subFolderLevel) + "</p><div class=\"argumentDescr\"><p>" + getLinked(arg.descr, doc, subFolderLevel) + "</p></div></div>";
            result += "</div>";
        }
        result += "<div class=\"returnContent\"><h4>Returns</h4>";
        if(func.returnValue != null)
            result += "<div class=\"return\"><p>"+getLinked(func.returnValue.type, doc, subFolderLevel) + "</p><div class=\"returnDescr\"><p>" + getLinked(func.returnValue.descr, doc, subFolderLevel)+"</p></div></div>";
        else
            result += "<p>nothing</p>";
        result += "</div>";
        return result + "</div></div>";
    }

    private static String getClassHTML(Class cls, ScriptDocument doc, int subFolderLevel)
    {
        String result = "<div class=\"classContent\" id=\"Class_"+cls.name+"\">";
        result += "<h1>Class: "+cls.name+"</h1>";
        List<Member> members = new ArrayList<>(cls.members);
        List<Method> methods = new ArrayList<>(cls.methods);
        Class parent = cls.baseClass;
        while (parent != null)
        {
            members.addAll(parent.members);
            methods.addAll(parent.methods);
            parent = parent.baseClass;
        }
        result += getSummaryTableOf(members, methods);
        if(cls.descr.length() > 0)
            result += "<p>"+getLinked(cls.descr, doc, subFolderLevel)+"</p>";
        if(members.size() > 0) {
            result += "<div class=\"memberList\"><h2>Members</h2>";
            for (Member mem : members) {
                result += "<div class=\"member\" id=\"Member_"+mem.name+"\"><p>\t" + (mem.isStatic ? "<i>static</i> " : "") + "<b>" + mem.name + "</b> : " + getLinked(mem.type, doc, subFolderLevel)+"</p>";
                if(mem.descr.length() > 0)
                    result += "<div class=\"memberDescr\"><p>" + getLinked(mem.descr, doc, subFolderLevel)+"</p></div>";
                result += "</div>";
            }
            result += "</div>";
        }
        if(methods.size() > 0) {
            result += "<div class=\"methodList\"><h2>Methods</h2>";
            for (Method meth : methods)
                result += "<div class=\"method\"><hr>" + getFunctionHTML(meth.func, doc, subFolderLevel, meth.isStatic ? "<i>Static</i> method" : "Method")+"</div>";
            result += "</div>";
        }
        return result + "</div>";
    }

    private static String getSummaryTableOf(List<Member> members, List<Method> methods)
    {
        String result = "";
        boolean addSummaryTable = methods.size() > 0;
        if(addSummaryTable) {
            result += "<table class=\"classSummaryTable\">";
            result += "<tr><th>Members</th><th>Methods</th></tr>";
            List<List<String>> rows = new ArrayList<>();
            for (int k = 0; k < members.size(); k++) {
                rows.add(new ArrayList<>());
                Member mem = members.get(k);
                String memberName = mem.name;
                rows.get(k).add("<span class=\"typeIdentifier\">" + (mem.isStatic ? "<i>static</i> " : "") + mem.type + " : </span><span class=\"nameIdentifier\">" + memberName + "</span>");
                rows.get(k).add("#Member_" + memberName);
            }
            for (int k = 0; k < methods.size(); k++) {
                if (k >= rows.size()) {
                    rows.add(new ArrayList<>());
                    rows.get(k).add(""); //add empty elements for member cells
                    rows.get(k).add("");
                }
                Method meth = methods.get(k);
                String methodName = meth.func.name;
                rows.get(k).add("<span class=\"typeIdentifier\">" +(meth.isStatic ? "<i>static</i> " : "") + (meth.func.returnValue == null ? "void" : meth.func.returnValue.type) + " : </span><span class=\"nameIdentifier\">" + getFullName(meth.func) + "</span>");
                rows.get(k).add("#Function_" + methodName);
            }
            for (List<String> rowInfo : rows) {
                result += "<tr>";
                for (int k = 0; k < rowInfo.size(); k += 2) {
                    String cellName = rowInfo.get(k);
                    String cellLink = rowInfo.get(k + 1);
                    result += "<td><a href=\"" + cellLink + "\">" + cellName + "</a></td>";
                }
                if(rowInfo.size() < 4)
                    result += "<td></td>";
                result += "</tr>";
            }
            result += "</table>";
        }
        return result;
    }

    private static String getHeaderHTML(int subFolderLevel, ScriptDocument doc)
    {
        String result = "";
        result += "<div style=\"width:100%;height:50px;border:1px solid #ccc\">";
        result += "<table id=\"headerTable\"><tr>";

        String relativeStart = "";
        for(int k = 0; k < subFolderLevel; k++)
            relativeStart += "../";

        //home
        result += "<td class=\"clickable headerButton\" onclick=\"window.location.href='"+ relativeStart + "index.html'\">Home</td>";

        //function navs
        result += "<td class=\"navParent clickable headerButton\">Functions<div class=\"navList\">";
        List<String> funcsInNav = new ArrayList<>();
        for(Function f : doc.funcs) {
            if (!funcsInNav.contains(f.name)) {
                result += "<a href=\"" + relativeStart + getRootFunctionURL(f).substring(1) + "\"><p class=\"navChild clickable\">" + f.name + "</p></a>";
                funcsInNav.add(f.name);
            }
        }
        result += "</div></td>";

        List<String> categories = new ArrayList<>();
        for(Class cls : doc.classes)
            if(!categories.contains(cls.category))
                categories.add(cls.category);

        result += "<td class=\"navParent clickable headerButton\">Classes<div class=\"navList\">";
        for(String cat : categories)
        {
            result += "<div class=\"navChild clickable\"><p>"+cat+"</p><div class=\"navSubList\">";
            for(Class cls : doc.classes)
                if(cls.category.equals(cat))
                    result += "<a href=\""+relativeStart + getRootClassURL(cls).substring(1)+"\"><p class=\"navChild clickable\">"+cls.name+"</p></a>";
            result += "</div></div>";
        }
        result += "</div></td>";

        result += "</tr></table>";
        result += "</div>";
        result += "<div id=\"backToTop\" class=\"clickable\" onclick=\"window.scrollTo(0, 0)\">^</div>";
        return result;
    }

    private static String getOpenHTML(String pageTitle, int subFolderLevel)
    {
        String dirUp = "";
        for(int k = 0; k < subFolderLevel; k++)
            dirUp += "../";

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>"+pageTitle+"</title>\n" +
                "<link rel=\"stylesheet\" href=\""+dirUp+"style.css\">\n" +
                "</head>";
    }
    private static String getRootFunctionURL(Function func) { return "/funcs/"+func.name+".html"; }

    private static String getRootClassURL(Class cls)
    {
        return "/classes/"+cls.name+".html";
    }

    private static String getMainPageFileName(String dstFolder) { return dstFolder + "/index.html"; }

    private static void createFunctionPage(String dstFolder, Function func, ScriptDocument doc)
    {
        List<Function> overloads = new ArrayList<>();
        for(Function f : doc.funcs)
            if(f.name.equals(func.name))
                overloads.add(f);
        String pageBody = "";
        if(overloads.size() > 1)
        {
            pageBody += "<h3>Function: "+func.name+"</h3><table class=\"overloadTable\"><tr><th>Overloads</th></tr>";
            for(Function f : overloads)
                pageBody += "<tr><td><a href=\"#Function_"+f.name+f.args.size()+"\">"+(f.returnValue == null ? "void" : f.returnValue.type)+" : "+getFullName(f)+"</a></td></tr>";
            pageBody += "</table>";
        }
        for(int k = 0; k < overloads.size(); k++)
        {
            Function f = overloads.get(k);
            pageBody += getFunctionHTML(f, doc, 1, (overloads.size() > 1 ? "Overload" : "Function")) + (overloads.size() > k + 1 ? "<hr>" : "");
        }
        String result = getOpenHTML("Function | "+func.name, 1) + "<body>" + getHeaderHTML(1, doc) + pageBody + "</body>";
        writeFile(dstFolder + getRootFunctionURL(func), result);
    }

    private static void createClassPage(String dstFolder, Class cls, ScriptDocument doc)
    {
        String result = getOpenHTML("Class | "+cls.name, 1) + "<body>" + getHeaderHTML(1, doc) + getClassHTML(cls, doc, 1) + "</body>";
        writeFile(dstFolder + getRootClassURL(cls), result);
    }

    private static void createMainPage(String dstFolder, ScriptDocument doc)
    {
        String result = getOpenHTML("Home", 0);
        result += "<body>"+getHeaderHTML(0, doc)+"<h1>Mythical Pokemon Randomizer | Scripting Reference</h1>";
        result += "<h2>Functions</h2>";
        List<String> addedFuncNames = new ArrayList<>();
        for(Function f : doc.funcs)
        {
            if(!addedFuncNames.contains(f.name))
            {
                result += "<div class=\"mainFuncLink\"><a href=\""+getRootFunctionURL(f).substring(1)+"\">"+f.name+"</a></div>";
                addedFuncNames.add(f.name);
            }
        }
        result += "<br><br><h2>Classes</h2>";
        HashMap<String, List<Class>> classesByCategory = new HashMap<>();
        for(Class c : doc.classes)
        {
            if(!classesByCategory.containsKey(c.category))
                classesByCategory.put(c.category, new ArrayList<>());
            classesByCategory.get(c.category).add(c);
        }
        List<String> categories = new ArrayList<>(classesByCategory.keySet());
        Collections.sort(categories, Collections.reverseOrder());
        for(String category : categories)
        {
            result += "<div class=\"classCategory\"><h3>"+category+"</h3>";
            for(Class c : classesByCategory.get(category))
                result += "<a href=\""+getRootClassURL(c).substring(1)+"\">"+c.name+"</a><br>";
            result += "</div>";
        }
        result += "</body>";

        writeFile(getMainPageFileName(dstFolder), result);
    }

    private static String getFullName(Function func)
    {
        String fullName = func.name+"(";
        for(Argument arg : func.args)
            fullName += arg.name + (arg.defaultValue.length() > 0 ? " = "+arg.defaultValue : "") + ", ";
        if(func.args.size() > 0)
            fullName = fullName.substring(0, fullName.length() - 2);
        fullName += ")";
        return fullName;
    }

    private static String replaceWordWithDelimiter(String source, String toReplace, String replaceWith, String delimiter)
    {
        source = source.replaceAll(delimiter+toReplace+" ", delimiter+replaceWith+" ");
        source = source.replaceAll(delimiter+toReplace+delimiter, delimiter+replaceWith+delimiter);
        source = source.replaceAll(" "+toReplace+delimiter, " "+replaceWith+delimiter);
        return source;
    }

    private static String replaceWordWithDelimiter(String source, String toReplace, String replaceWith, String delimiter, String escapedDelimiter)
    {
        source = source.replaceAll(escapedDelimiter+toReplace+" ", delimiter+replaceWith+" ");
        source = source.replaceAll(escapedDelimiter+toReplace+escapedDelimiter, delimiter+replaceWith+delimiter);
        source = source.replaceAll(" "+toReplace+escapedDelimiter, " "+replaceWith+delimiter);
        return source;
    }
    private static String replaceWholeWord(String source, String toReplace, String replaceWith)
    {
        if(source.equals(toReplace)){ return replaceWith; }
        source = source.replaceAll(" "+toReplace+" ", " "+replaceWith+" ");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, ".", "\\.");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, ",");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, "/");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, ";");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, ":");
        source = replaceWordWithDelimiter(source, toReplace, replaceWith, "!");
        if(source.length() > toReplace.length()) {
            if (source.substring(0, toReplace.length()).equals(toReplace))
                source = toReplace + source.substring(toReplace.length());
            if (source.substring(source.length() - toReplace.length()).equals(toReplace))
                source = source.substring(0, source.length() - toReplace.length()) + toReplace;
        }
        return source;
    }
    private static String getLinked(String source, ScriptDocument doc, int subFolderLevel)
    {
        for(int i = 0; i < 2; i++) {
            boolean dotsOnly = (i == 0); //prioritize names with dots in them, otherwise nested classes won't be linked
            for (Function func : doc.funcs) {
                if(dotsOnly && !func.name.contains(".")) { continue; }
                String link = "";
                for (int k = 0; k < subFolderLevel; k++)
                    link += "../";
                link += getRootFunctionURL(func).substring(link.length() > 0 ? 1 : 0);
                source = replaceWholeWord(source, func.name, "<a href=\"" + link + "\">" + func.name + "</a>");
            }
            for (Class cls : doc.classes) {
                if(dotsOnly && !cls.name.contains(".")) { continue; }
                String link = "";
                for (int k = 0; k < subFolderLevel; k++)
                    link += "../";
                link += getRootClassURL(cls).substring(link.length() > 0 ? 1 : 0);
                source = replaceWholeWord(source, cls.name, "<a href=\"" + link + "\">" + cls.name + "</a>");
            }
        }
        return source;
    }

    private static void writeFile(String filename, String data)
    {
        try {
            Files.createDirectories(Paths.get(filename).getParent());
        }
        catch(IOException e){}
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(data.replaceAll("\t", "&emsp;"));
            out.close();
        } catch (FileNotFoundException e) {}
    }

    private static ScriptDocument generateDocument(String filename)
    {
        ScriptDocument scriptDoc = new ScriptDocument();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(DocGenerator.class.getResourceAsStream(filename));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList functionList = root.getElementsByTagName("functions").item(0).getChildNodes();
            NodeList classList = root.getElementsByTagName("classes").item(0).getChildNodes();

            for(int k = 0; k < functionList.getLength(); k++)
            {
                Function func = getParsedFunction(functionList.item(k));
                if(func != null)
                    scriptDoc.funcs.add(func);
            }

            for(int k = 0; k < classList.getLength(); k++)
            {
                Class cls = getParsedClass(classList.item(k));
                if(cls != null)
                    scriptDoc.classes.add(cls);
            }

            //add base classes after parsing them so that their definition order won't matter
            addBaseClasses(classList, scriptDoc);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return scriptDoc;
    }

    private static Function getParsedFunction(Node func)
    {
        Function resultFunc = new Function();
        NamedNodeMap attr = func.getAttributes();
        if(attr == null) { return null; }
        resultFunc.name = attr.getNamedItem("name").getNodeValue();
        resultFunc.descr = attr.getNamedItem("descr").getNodeValue();
        NodeList children = func.getChildNodes();
        Node argumentList = null;
        Node returnNode = null;
        for(int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            switch(child.getNodeName())
            {
                case "arguments":
                    argumentList = child;
                    break;
                case "return":
                    returnNode = child;
                    break;
            }
        }
        if(argumentList != null)
        {
            NodeList argNodes = argumentList.getChildNodes();
            for(int i = 0; i < argNodes.getLength(); i++)
            {
                Node arg = argNodes.item(i);
                NamedNodeMap argAttr = arg.getAttributes();
                if(argAttr == null){ continue; }
                Argument created = new Argument();
                created.name = argAttr.getNamedItem("name").getNodeValue();
                created.type = argAttr.getNamedItem("type").getNodeValue();
                created.descr = argAttr.getNamedItem("descr").getNodeValue();
                Node defaultValNode = argAttr.getNamedItem("default");
                if(defaultValNode != null)
                    created.defaultValue = defaultValNode.getNodeValue();
                resultFunc.args.add(created);
            }
        }
        if(returnNode != null)
        {
            resultFunc.returnValue = new Return();
            NamedNodeMap returnAttr = returnNode.getAttributes();
            resultFunc.returnValue.descr = returnAttr.getNamedItem("descr").getNodeValue();
            resultFunc.returnValue.type = returnAttr.getNamedItem("type").getNodeValue();
        }
        return resultFunc;
    }

    private static Class getParsedClass(Node cls)
    {
        Class resultClass = new Class();
        NamedNodeMap attr = cls.getAttributes();
        if(attr == null) { return null; }
        resultClass.name = attr.getNamedItem("name").getNodeValue();
        resultClass.descr = attr.getNamedItem("descr").getNodeValue();
        resultClass.category = attr.getNamedItem("category").getNodeValue();
        NodeList children = cls.getChildNodes();
        Node membersList = null;
        Node methodsList = null;
        for(int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            switch(child.getNodeName())
            {
                case "members":
                    membersList = child;
                    break;
                case "methods":
                    methodsList = child;
                    break;
            }
        }
        if(membersList != null)
        {
            NodeList members = membersList.getChildNodes();
            for(int i = 0; i < members.getLength(); i++)
            {
                Node mem = members.item(i);
                NamedNodeMap memAttr = mem.getAttributes();
                if(memAttr == null){ continue; }
                Member createdMem = new Member();
                createdMem.name = memAttr.getNamedItem("name").getNodeValue();
                createdMem.descr = memAttr.getNamedItem("descr").getNodeValue();
                createdMem.type = memAttr.getNamedItem("type").getNodeValue();
                Node isStaticNode = memAttr.getNamedItem("static");
                if(isStaticNode != null)
                    createdMem.isStatic = isStaticNode.getNodeValue().toLowerCase().equals("true");
                else
                    createdMem.isStatic = false;
                resultClass.members.add(createdMem);
            }
        }
        if(methodsList != null)
        {
            NodeList methods = methodsList.getChildNodes();
            for(int i = 0; i < methods.getLength(); i++)
            {
                Node meth = methods.item(i);
                NamedNodeMap methAttr = meth.getAttributes();
                if(methAttr == null){ continue; }
                Method createdMeth = new Method();
                createdMeth.func = getParsedFunction(meth);
                Node isStaticNode = methAttr.getNamedItem("static");
                if(isStaticNode != null)
                    createdMeth.isStatic = isStaticNode.getNodeValue().toLowerCase().equals("true");
                else
                    createdMeth.isStatic = false;
                resultClass.methods.add(createdMeth);
            }
        }
        return resultClass;
    }

    private static void addBaseClasses(NodeList classList,ScriptDocument scriptDoc)
    {
        for(int k = 0; k < classList.getLength(); k++)
        {
            Node classNode = classList.item(k);
            NamedNodeMap attr = classNode.getAttributes();
            if(attr == null) { continue; }
            String className = attr.getNamedItem("name").getNodeValue();
            Node baseNode = attr.getNamedItem("base");
            if(baseNode != null)
            {
                String baseName = baseNode.getNodeValue();
                Class baseClass = null;
                for(Class cls : scriptDoc.classes) {
                    if (cls.name.equals(baseName)) {
                        baseClass = cls;
                        break;
                    }
                }
                if(baseClass == null) { throw new RuntimeException("Invalid ScriptDocData.xml! Base class \""+baseNode+"\" was not found!"); }
                for(Class cls : scriptDoc.classes){
                    if(cls.name.equals(className)) {
                        cls.baseClass = baseClass;
                        break;
                    }
                }
            }
        }
    }

    private static void copyStyleFile(String sourceFile, String targetFolder)
    {
        InputStream streamToCopy = DocGenerator.class.getResourceAsStream(sourceFile);

        //from https://www.baeldung.com/java-copy-file
        try
        {
            File copiedFile = new File(targetFolder+"/style.css");
            OutputStream out = new BufferedOutputStream(new FileOutputStream(copiedFile));

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = streamToCopy.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }
        catch(FileNotFoundException e) {}
        catch(IOException e) {}
    }
}
