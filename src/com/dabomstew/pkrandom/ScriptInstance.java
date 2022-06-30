package com.dabomstew.pkrandom;

import com.dabomstew.pkrandom.pokemon.Pokemon;
import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.List;

public class ScriptInstance {
    private PythonInterpreter interp;

    ScriptInstance(String source)
    {
        interp = new PythonInterpreter();
        interp.exec(source);
    }

    public List<Pokemon> getScriptedStarters(List<Pokemon> pokepool)
    {
        PyFunction func = (PyFunction)interp.get("selectStarter");
        List<Pokemon> starters = new ArrayList<Pokemon>();
        for(int k = 0; k < 3; k++)
        {
            PyObject pkm = func.__call__(Py.java2py(pokepool), new PyInteger(k));
            starters.add(Py.tojava(pkm, Pokemon.class));
        }
        return starters;
    }
}
