package com.dabomstew.pkrandom;

import com.dabomstew.pkrandom.pokemon.*;
import org.python.core.*;
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

    public Pokemon getScriptedStatic(List<Pokemon> pokepool, StaticEncounter oldEncounter, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectStaticPokemon");
        return Py.tojava(func.__call__(Py.java2py(pokepool), Py.java2py(oldEncounter), new PyBoolean(megaSwap)), Pokemon.class);
    }

    public StaticEncounter getScriptedStaticFull(List<Pokemon> pokepool, StaticEncounter oldEncounter, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectStaticPokemonFull");
        return Py.tojava(func.__call__(Py.java2py(pokepool), Py.java2py(oldEncounter), new PyBoolean(megaSwap)), StaticEncounter.class);
    }

    public IngameTrade getScriptedInGameTrade(List<Pokemon> pokepool, IngameTrade oldTrade)
    {
        PyFunction func = (PyFunction)interp.get("selectInGameTradePokemon");
        return Py.tojava(func.__call__(Py.java2py(pokepool), Py.java2py(oldTrade)), IngameTrade.class);
    }

    public EncounterSet getScriptedWildEncounterSet(List<Pokemon> pokepool, EncounterSet oldArea)
    {
        PyFunction func = (PyFunction)interp.get("selectWildEncountersForArea");
        return Py.tojava(func.__call__(Py.java2py(pokepool), Py.java2py(oldArea)), EncounterSet.class);
    }
}
