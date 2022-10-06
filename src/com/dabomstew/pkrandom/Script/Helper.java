package com.dabomstew.pkrandom.Script;

import org.python.core.*;

import java.util.List;

public class Helper {

    //turns a java string into a (sanitized) python string
    public static PyString Str(String value)
    {
        //clean string by filtering out all non-ascii characters
        String cleaned = "";
        for(char c : value.toCharArray())
        {
            if(c >= 0 && c <= 127)
            {
                cleaned += c;
            }
        }

        //create & return PyString
        PyString result = new PyString(cleaned);
        return result;
    }

    //turns a java list into a python list
    public static <T> PyList Seq(List<T> list)
    {
        PyList result = new PyList();
        for(T item : list)
        {
            result.append(Py.java2py(item));
        }
        return result;
    }

}
