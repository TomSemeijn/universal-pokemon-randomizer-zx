package com.dabomstew.pkrandom.Script;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Items;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ScriptInstance {
    private PythonInterpreter interp;
    private RomHandler rom;
    public ScriptInstance(String source, RomHandler rom)
    {
        interp = new PythonInterpreter();
        interp.exec(Helper.DefinitionString());
        interp.exec(source);

        this.rom = rom;
    }

    public List<Pokemon> getLimitedPokepool(List<Pokemon> pokepool)
    {
        PyFunction func = (PyFunction)interp.get("limitPokemon");
        return toJavaList((PySequence) func.__call__(pyPokePool(pokepool)), poke -> Py.tojava(poke, Pokemon.class));
    }

    public List<Pokemon> getScriptedStarters(List<Pokemon> pokepool, int count)
    {
        PyFunction func = (PyFunction)interp.get("selectStarter");
        List<Pokemon> starters = new ArrayList<Pokemon>();
        for(int k = 0; k < count; k++)
        {
            PyObject pkm = func.__call__(pyPokePool(pokepool), new PyInteger(k));
            starters.add(Py.tojava(pkm, Pokemon.class));
        }
        return starters;
    }

    public StaticEncounter getScriptedStatic(List<Pokemon> pokepool, StaticEncounter oldEncounter, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectStaticPokemon");
        return Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldEncounter), new PyBoolean(megaSwap)), StaticEncounter.class);
    }

    public IngameTrade getScriptedInGameTrade(List<Pokemon> pokepool, IngameTrade oldTrade)
    {
        PyFunction func = (PyFunction)interp.get("selectInGameTradePokemon");
        return Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldTrade)), IngameTrade.class);
    }

    public EncounterSet getScriptedWildEncounterSet(List<Pokemon> pokepool, EncounterSet oldArea)
    {
        PyFunction func = (PyFunction)interp.get("selectWildEncountersForArea");
        return Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldArea)), EncounterSet.class);
    }

    public TrainerPokemon getScriptedTrainerPokemon(List<Pokemon> pokepool, Trainer trainer, TrainerPokemon oldPokemon, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemon");
        return Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(trainer), Py.java2py(oldPokemon), new PyBoolean(megaSwap)), TrainerPokemon.class);
    }

    public Integer getScriptedTrainerPokemonHeldItem(List<Integer> itempool, Trainer trainer, TrainerPokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemonItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        return convertedIndex(func.__call__(pyItempool, Py.java2py(trainer), Py.java2py(pokemon)).asInt(), Items.class, rom.getItemClass());
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
            pokemon.commonHeldItem = common == -1 ? 0 : convertedIndex(common, Items.class, rom.getItemClass());
        }
        if(supportRare && rare != 0)
        {
            pokemon.rareHeldItem = rare == -1 ? 0 : convertedIndex(rare, Items.class, rom.getItemClass());;
        }
        if(supportGuaranteed && guaranteed != 0)
        {
            pokemon.guaranteedHeldItem = guaranteed == -1 ? 0 : convertedIndex(guaranteed, Items.class, rom.getItemClass());;
        }
        if(supportDarkGrass && darkGrass != 0 && (!supportGuaranteed || guaranteed == 0))
        {
            pokemon.darkGrassHeldItem = darkGrass == -1 ? 0 : convertedIndex(darkGrass, Items.class, rom.getItemClass());;
        }
        return pokemon;
    }

    public Move getScriptedMoveData(Move oldMove, boolean hasPhysicalSpecialSplit)
    {
        PyFunction func = (PyFunction)interp.get("setMoveData");
        return Py.tojava(func.__call__(Py.java2py(oldMove), new PyBoolean(hasPhysicalSpecialSplit)), Move.class);
    }

    public List<MoveLearnt> getScriptedLearntMoveset(Pokemon pokemon, List<MoveLearnt> oldMoveset)
    {
        PyFunction func = (PyFunction)interp.get("setLearntMoveset");
        PyArray pyMoveset = toPythonArray(oldMoveset, PyObject.class, move -> Py.java2py(move));
        List<MoveLearnt> result = toJavaList((PySequence) func.__call__(Py.java2py(pokemon), pyMoveset), move -> Py.tojava(move, MoveLearnt.class));
        return result;
    }

    public List<MoveLearnt> getPostScriptedLearntMoveset(Pokemon pokemon, List<MoveLearnt> oldMoveset)
    {
        PyFunction func = (PyFunction)interp.get("setLearntMovesetPost");
        PyArray pyMoveset = toPythonArray(oldMoveset, PyObject.class, move -> Py.java2py(move));
        return toJavaList((PySequence)func.__call__(Py.java2py(pokemon), pyMoveset), move -> Py.tojava(move, MoveLearnt.class));
    }

    public List<Integer> getScriptedEggMoveset(Pokemon pokemon, List<Integer> oldMoveset)
    {
        PyFunction func = (PyFunction)interp.get("setEggMoveset");
        PyArray pyMoveset = toPythonArray(oldMoveset, PyInteger.class, i -> new PyInteger(i));
        return toJavaList((PySequence)(func.__call__(Py.java2py(pokemon), pyMoveset)), i -> new Integer(i.asInt()));
    }

    public void updateScriptedPokemonBaseStats(Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("setBaseStats");
        PyDictionary result = (PyDictionary)(func.__call__(Py.java2py(pokemon)));
        int hp = result.has_key(new PyString("hp")) ? ((PyInteger)result.get(new PyString("hp"))).asInt() : pokemon.hp;
        int atk = result.has_key(new PyString("atk")) ? ((PyInteger)result.get(new PyString("atk"))).asInt() : pokemon.attack;
        int def = result.has_key(new PyString("def")) ? ((PyInteger)result.get(new PyString("def"))).asInt() : pokemon.defense;
        int spatk = result.has_key(new PyString("spatk")) ? ((PyInteger)result.get(new PyString("spatk"))).asInt() : pokemon.spatk;
        int spdef = result.has_key(new PyString("spdef")) ? ((PyInteger)result.get(new PyString("spdef"))).asInt() : pokemon.spdef;
        int spd = result.has_key(new PyString("spd")) ? ((PyInteger)result.get(new PyString("spd"))).asInt() : pokemon.speed;
        pokemon.hp = hp;
        pokemon.attack = atk;
        pokemon.defense = def;
        pokemon.spatk = spatk;
        pokemon.spdef = spdef;
        pokemon.speed = spd;
    }

    public void updateScriptedPokemonTypes(Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectPokemonTypes");
        PyDictionary result = (PyDictionary)(func.__call__(Py.java2py(pokemon)));

        Type primary = result.has_key(new PyString("primary")) ? Py.tojava(result.get(new PyString("primary")), Type.class) : pokemon.primaryType;
        Type secondary = result.has_key(new PyString("secondary")) ? Py.tojava(result.get(new PyString("secondary")), Type.class) : pokemon.secondaryType;

        if(primary == null){
            primary = pokemon.primaryType;
        }
        if(secondary == primary)
        {
            secondary = null;
        }

        pokemon.primaryType = primary;
        pokemon.secondaryType = secondary;
    }

    //from https://stackoverflow.com/questions/271109/iterate-static-int-values-in-java
    private static List<Integer> getAllAbilities(Class<Abilities> c) {
        List<Integer> list  = new ArrayList<Integer>();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(int.class) && Modifier.isStatic(field.getModifiers())) {
                    list.add(field.getInt(null));
                }
            }
            catch (IllegalAccessException e) {
                // Handle exception here
            }
        }
        return list;
    }

    public void updateScriptedPokemonAbilities(Pokemon pokemon, int maxAbilities, List<Integer> bannedAbilities, int highestAbilityIndex) throws Exception
    {
        //create abilitypool
        List<Integer> abilities = getAllAbilities(Abilities.class);
        abilities.removeIf(n -> (n > highestAbilityIndex || bannedAbilities.contains(n)));

        //call funciton
        PyFunction func = (PyFunction)interp.get("selectPokemonAbilities");
        PyArray pyAbilities = toPythonArray(abilities, PyInteger.class, i -> new PyInteger(i));
        PyList result = (PyList)(func.__call__(Py.java2py(pokemon), pyAbilities, new PyInteger(maxAbilities)));

        //get and verify chosen abilities
        int chosen[] = { 0, 0, 0 };
        int chosenIter = 0;
        for(int k = 0; k < result.size(); k++)
        {
            int ability = (Integer)result.get(k);
            if(!abilities.contains(ability) && ability != 0)
            {
                throw new Exception("Chose unavailable ability!");
            }
            chosen[chosenIter++] = ability;
        }

        //throw exception if not even the first ability is 0
        if(chosen[0] <= 0)
        {
            throw new Exception("No ability given to pokemon "+pokemon.name+"!");
        }

        //set abilities
        pokemon.ability1 = chosen[0];
        if(maxAbilities >= 2) { pokemon.ability2 = chosen[1]; }
        if(maxAbilities >= 3) { pokemon.ability3 = chosen[2]; }
    }

    public ExpCurve getScriptedEXPCurve(Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectPokemonEXPCurve");
        return Py.tojava(func.__call__(Py.java2py(pokemon)), ExpCurve.class);
    }

    public int getScriptedFieldItem(int oldItem, List<Integer> itempool, boolean isTM)
    {
        PyFunction func = (PyFunction)interp.get("selectFieldItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        return convertedIndex(func.__call__(new PyInteger(oldItem), pyItempool, new PyBoolean(isTM)).asInt(), Items.class, rom.getItemClass());
    }

    public int getScriptedPickupItem(int oldItem, List<Integer> itempool)
    {
        PyFunction func = (PyFunction)interp.get("selectPickupItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        return convertedIndex(func.__call__(new PyInteger(oldItem), pyItempool).asInt(), Items.class, rom.getItemClass());
    }

    public Move getScriptedTMMove(Move oldMove, List<Move> movepool, boolean forcedDamagingMove)
    {
        PyFunction func = (PyFunction)interp.get("selectTMMove");
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        return Py.tojava(func.__call__(Py.java2py(oldMove), pyMovepool, new PyBoolean(forcedDamagingMove)), Move.class);
    }

    public boolean getScriptedTMMoveCompat(Move move, Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectTMCompatibility");
        return ((PyBoolean)(func.__call__(Py.java2py(move), Py.java2py(pokemon)))).getBooleanValue();
    }

    public boolean getScriptedTutorMoveCompat(Move move, Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectTutorCompatibility");
        return ((PyBoolean)(func.__call__(Py.java2py(move), Py.java2py(pokemon)))).getBooleanValue();
    }

    public Move getScriptedTutorMove(Move oldMove, List<Move> movepool, boolean forcedDamagingMove)
    {
        PyFunction func = (PyFunction)interp.get("selectTutorMove");
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        return Py.tojava(func.__call__(Py.java2py(oldMove), pyMovepool, new PyBoolean(forcedDamagingMove)), Move.class);
    }

    public int getScriptedStarterHeldItem(int oldItem, List<Integer> itempool)
    {
        PyFunction func = (PyFunction)interp.get("selectStarterHeldItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        return convertedIndex(func.__call__(new PyInteger(oldItem), pyItempool).asInt(), Items.class, rom.getItemClass());
    }

    public List<Evolution> getScriptedEvolutions(List<Pokemon> pokepool, Pokemon poke, List<Evolution> oldEvolutions)
    {
        PyFunction func = (PyFunction)interp.get("selectEvolutions");
        PyObject pypool = pyPokePool(pokepool);

        /*
        [evolveTo: Pokemon, type: EvolutionType, ?level: int, ?item: int, ?move: int]
         */

        //convert old evolutions to python dictionaries
        PyArray pyOldEvos = new PyArray(PyDictionary.class, null);
        for(Evolution evo : oldEvolutions)
        {
            PyDictionary oldEvos = new PyDictionary();
            oldEvos.put(new PyString("evolveTo"), Py.java2py(evo.to));
            oldEvos.put(new PyString("type"), Py.java2py(evo.type));
            if(evo.type.usesLevel())
            {
                oldEvos.put(new PyString("level"), new PyInteger(evo.extraInfo));
            }
            else if(evo.type.usesMove())
            {
                oldEvos.put(new PyString("move"), new PyInteger(evo.extraInfo));
            }
            else if(evo.type.usesItem())
            {
                oldEvos.put(new PyString("item"), new PyInteger(convertedIndex(evo.extraInfo, rom.getItemClass(), Items.class)));
            }
            else if(evo.type.usesSpecies())
            {
                oldEvos.put(new PyString("species"), new PyInteger(convertedIndex(evo.extraInfo, rom.getItemClass(), Items.class)));
            }
        }

        //call function
        PySequence result = (PySequence)(func.__call__(pypool, Py.java2py(poke), pyOldEvos));

        //convert new evos from pyhton dictionaries
        List<Evolution> toReturn = new ArrayList<>();
        for(int k = 0; k < result.__len__(); k++)
        {
            PyDictionary current = (PyDictionary)result.__getitem__(k);

            if(!current.has_key(new PyString("evolveTo"))){ throw new RuntimeException("Evolution dictionary must have a \"evolveTo\" field!"); }
            if(!current.has_key(new PyString("type"))){ throw new RuntimeException("Evolution dictionary must have a \"type\" field!"); }

            Pokemon from = poke;
            Pokemon to = Py.tojava(current.get(new PyString("evolveTo")), Pokemon.class);
            EvolutionType evoType = Py.tojava(current.get(new PyString("type")), EvolutionType.class);

            int level = 0;
            int extraInfo = 0;
            if(evoType.usesLevel())
            {
                if(!current.has_key(new PyString("level"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"level\" key!"); }
                level = current.get(new PyString("level")).asInt();
                extraInfo = level;
            }
            else if(evoType.usesMove())
            {
                if(!current.has_key(new PyString("move"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"move\" key!"); }
                extraInfo = current.get(new PyString("move")).asInt();
            }
            else if(evoType.usesItem())
            {
                if(!current.has_key(new PyString("item"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"item\" key!"); }
                extraInfo = convertedIndex(current.get(new PyString("item")).asInt(), Items.class, rom.getItemClass());
            }
            else if(evoType.usesSpecies())
            {
                if(!current.has_key(new PyString("species"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"species\" key!"); }
                extraInfo = current.get(new PyString("species")).asInt();
            }

            boolean carryStats = result.__len__() == 1 || evoType == EvolutionType.LEVEL_CREATE_EXTRA;
            Evolution finalEvo = new Evolution(from, to, carryStats, evoType, extraInfo);
            finalEvo.level = level;
            finalEvo.forme = to.formeNumber;
            finalEvo.formeSuffix = to.formeSuffix;

            toReturn.add(finalEvo);
        }

        //return result
        return toReturn;
    }

    private static int convertedIndex(int generalItem, Class startClass, Class endClass)
    {
        if(endClass == startClass) { //return general item if the itemclass used by the romhandler is the general one
            return generalItem;
        }

        //get the name of the variable in the start class
        String itemName = Helper.toStr(generalItem, Helper.Index.ITEM).asString();

        //try to find the variable in the end class and return its value when found
        Field[] targetFields = endClass.getFields();
        for(Field fld : targetFields)
        {
            if(fld.getName() == itemName)
            {
                try{
                    return (int)fld.get(null);
                }
                catch(IllegalAccessException e)
                {
                    throw new RuntimeException("IllegalAccessException thrown!");
                }
            }
        }

        //throw exception on failure
        throw new RuntimeException("Current ROM does not support item "+itemName+"!");
    }

    public interface ConvertOperator<T1, T2>
    {
        public T2 op(T1 source);
    }
    public static <T, PyT extends PyObject> PyArray toPythonArray(List<T> list, Class<PyT> classType, ConvertOperator<T, PyT> convertFunc)
    {
        PyArray arr = new PyArray(classType, null);
        for(T current : list)
        {
            arr.append(convertFunc.op(current));
        }
        return arr;
    }

    public static <T, PyT extends PyObject> List<T> toJavaList(PySequence arr, ConvertOperator<PyT, T> convertFunc)
    {
        List<T> result = new ArrayList<T>();
        for(int k = 0; k < arr.__len__(); k++)
        {
            result.add(convertFunc.op((PyT)arr.__getitem__(k)));
        }
        return result;
    }

    private PyArray pyPokePool(List<Pokemon> pokepool)
    {
        return toPythonArray(pokepool, PyObject.class, poke -> Py.java2py(poke));
    }

}
