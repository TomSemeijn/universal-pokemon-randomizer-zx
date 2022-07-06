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

    public List<Pokemon> getLimitedPokepool(List<Pokemon> pokepool)
    {
        PyFunction func = (PyFunction)interp.get("limitPokemon");
        return Py.tojava(func.__call__(Py.java2py(pokepool)), List.class);
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

    public StaticEncounter getScriptedStatic(List<Pokemon> pokepool, StaticEncounter oldEncounter, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectStaticPokemon");
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

    public TrainerPokemon getScriptedTrainerPokemon(List<Pokemon> pokepool, Trainer trainer, TrainerPokemon oldPokemon, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemon");
        return Py.tojava(func.__call__(Py.java2py(pokepool), Py.java2py(trainer), Py.java2py(oldPokemon), new PyBoolean(megaSwap)), TrainerPokemon.class);
    }

    public Integer getScriptedTrainerPokemonHeldItem(List<Integer> itempool, Trainer trainer, TrainerPokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemonItem");
        return Py.tojava(func.__call__(Py.java2py(itempool), Py.java2py(trainer), Py.java2py(pokemon)), Integer.class);
    }

    public Pokemon getScriptedWildHeldItemPokemon(ItemList itempool, Pokemon pokemon, boolean supportCommon, boolean supportRare, boolean supportGuaranteed, boolean supportDarkGrass)
    {
        PyFunction func = (PyFunction)interp.get("selectWildPokemonHeldItem");
        PyObject[] args = {Py.java2py(itempool), Py.java2py(pokemon), new PyBoolean(supportCommon), new PyBoolean(supportRare), new PyBoolean(supportGuaranteed), new PyBoolean(supportDarkGrass)};
        PyDictionary result = (PyDictionary)(func.__call__(args));
        int common = result.has_key(new PyString("common")) ? ((PyInteger)result.get(new PyString("common"))).asInt() : 0;
        int rare = result.has_key(new PyString("rare")) ? ((PyInteger)result.get(new PyString("rare"))).asInt() : 0;
        int guaranteed = result.has_key(new PyString("guaranteed")) ? ((PyInteger)result.get(new PyString("guaranteed"))).asInt() : 0;
        int darkGrass = result.has_key(new PyString("darkGrass")) ? ((PyInteger)result.get(new PyString("darkGrass"))).asInt() : 0;
        if(supportCommon && common != 0)
        {
            pokemon.commonHeldItem = common == -1 ? 0 : common;
        }
        if(supportRare && rare != 0)
        {
            pokemon.rareHeldItem = rare == -1 ? 0 : rare;
        }
        if(supportGuaranteed && guaranteed != 0)
        {
            pokemon.guaranteedHeldItem = guaranteed == -1 ? 0 : guaranteed;
        }
        if(supportDarkGrass && darkGrass != 0 && (!supportGuaranteed || guaranteed == 0))
        {
            pokemon.darkGrassHeldItem = darkGrass == -1 ? 0 : darkGrass;
        }
        return pokemon;
    }

    public Move getScriptedMoveData(Move oldMove, boolean hasPhysicalSpecialSplit)
    {
        PyFunction func = (PyFunction)interp.get("setMoveData");
        return Py.tojava(func.__call__(Py.java2py(oldMove), new PyBoolean(hasPhysicalSpecialSplit)), Move.class);
    }
}
