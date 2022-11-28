package com.dabomstew.pkrandom.Script;

import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Items;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ScriptInstance {
    private PythonInterpreter interp;
    private Settings settings;
    private RomHandler rom;
    public ScriptInstance(String source, RomHandler rom, Settings settings)
    {
        this.rom = rom;
        this.settings = settings;

        interp = new PythonInterpreter();
        interp.exec(Helper.DefinitionString());
        addROMInfo();
        source = source.replaceAll("Abilities.static",  "Abilities.staticTheAbilityNotTheKeyword"); //funny edge case
        source = source.replaceAll("Moves.return",  "Moves.returnTheMoveNotTheKeyword"); //funny edge case
        interp.exec(source);
    }

    public List<Pokemon> getLimitedPokepool(List<Pokemon> pokepool)
    {
        PyFunction func = (PyFunction)interp.get("limitPokemon");
        List<Pokemon> result = toJavaList((PySequence) func.__call__(pyPokePool(pokepool)), poke -> Py.tojava(poke, Pokemon.class));
        if(!inPool(pokepool, result))
        {
            throw new RuntimeException("One or more Pokemon detected that weren't in the given pokepool in function \"limitPokemon\"!");
        }
        return result;
    }

    public List<Pokemon> getScriptedStarters(List<Pokemon> pokepool, int count)
    {
        PyFunction func = (PyFunction)interp.get("selectStarter");
        List<Pokemon> starters = new ArrayList<Pokemon>();
        for(int k = 0; k < count; k++)
        {
            Pokemon pkm = Py.tojava(func.__call__(pyPokePool(pokepool), new PyInteger(k)), Pokemon.class);
            if(!inPool(pokepool, pkm))
            {
                throw new RuntimeException("Pokemon "+pkm.name+" was not in the given pokepool in function \"selectStarter\"!");
            }
            starters.add(pkm);
        }
        return starters;
    }

    public StaticEncounter getScriptedStatic(List<Pokemon> pokepool, StaticEncounter oldEncounter, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectStaticPokemon");
        oldEncounter.heldItem = convertedIndex(oldEncounter.heldItem, rom.getItemClass(), Items.class);
        StaticEncounter result = Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldEncounter), new PyBoolean(megaSwap)), StaticEncounter.class);
        if(!inPool(pokepool, result.pkmn))
        {
            throw new RuntimeException("Pokemon "+result.pkmn.name+" was not in the given pokepool in function \"selectStaticPokemon\"!");
        }
        if(result.level <= 0 || result.level > 100)
        {
            throw new RuntimeException("Static encounter of pokemon "+result.pkmn.name+" selected in function \"selectStaticPokemon\" has a level outside of the [1-100] range! The given level was "+result.level);
        }
        if(result.maxLevel < 0 || result.maxLevel > 100)
        {
            throw new RuntimeException("Static encounter of pokemon " + result.pkmn.name + " selected in function \"selectStaticPokemon\" has a maxLevel outside of the [0-100] range! The given level was " + result.maxLevel);
        }
        List<Integer> itempool = toItemPool(rom.getAllowedItems());
        if(!itempool.contains(result.heldItem) && result.heldItem != Items.none)
        {
            throw new RuntimeException("Held item Items."+Helper.toStr(result.heldItem, Helper.Index.ITEM).asString()+" selected in function \"selectStaticPokemon\" is not in the itempool! You can get the itempool from ROM.getItempool()");
        }
        if(result.heldItem != Items.none)
            result.heldItem = convertedIndex(result.heldItem, Items.class, rom.getItemClass());
        return result;
    }

    public IngameTrade getScriptedInGameTrade(List<Pokemon> pokepool, IngameTrade oldTrade)
    {
        PyFunction func = (PyFunction)interp.get("selectInGameTradePokemon");
        oldTrade.item = convertedIndex(oldTrade.item, rom.getItemClass(), Items.class);
        IngameTrade result = Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldTrade)), IngameTrade.class);

        //treat IVs so they aren't higher than the max value before being passed to the script (this happens apparrently)
        final int maxIV = rom.hasDVs() ? 16 : 32;
        for(int k = 0; k < result.ivs.length; k++)
            result.ivs[k] = Math.min(result.ivs[k], maxIV);

        if(!inPool(pokepool, result.givenPokemon))
        {
            throw new RuntimeException("Given Pokemon "+result.givenPokemon.name+" was not in the given pokepool in function \"selectInGameTradePokemon\"!");
        }
        if(!inPool(pokepool, result.requestedPokemon))
        {
            throw new RuntimeException("Requested Pokemon "+result.requestedPokemon.name+" was not in the given pokepool in function \"selectInGameTradePokemon\"!");
        }
        for(int iv : result.ivs)
        {
            if(iv < 0 || iv > maxIV)
            {
                String IVString = "[";
                for(Integer thisIV : result.ivs)
                    IVString += thisIV + ", ";
                IVString = IVString.substring(0, IVString.length() - 2) + "]";
                throw new RuntimeException("IVs given Pokemon "+result.givenPokemon.name+" selected in function \"selectInGameTradePokemon\" has values outside of the [0-"+maxIV+"] range! The given values are "+IVString+". You can get the maximum IV value in your script with ROM.maxIV");
            }
        }
        if(result.otName.length() > rom.maxTradeOTNameLength())
        {
            throw new RuntimeException("OT name \""+result.otName+"\" selected in function \"selectInGameTradePokemon\" exceeds the maximum length of "+rom.maxTradeOTNameLength()+"! The length of the given name was "+result.otName.length()+". You can get the maximum OT name length in your script from ROM.maxOTLen");
        }
        if(result.nickname.length() > rom.maxTradeNicknameLength())
        {
            throw new RuntimeException("Trade Pokemon nickname \""+result.nickname+"\" selected in function \"selectInGameTradePokemon\" exceeds the maximum length of "+rom.maxTradeNicknameLength()+"! The length of the given name was "+result.nickname.length()+". You can get the maximum nickname length in your script from ROM.maxNicknameLen");
        }
        List<Integer> itempool = toItemPool(rom.getAllowedItems());
        if (!itempool.contains(result.item) && result.item != Items.none) {
            throw new RuntimeException("Held item Items." + Helper.toStr(result.item, Helper.Index.ITEM).asString() + " selected in function \"selectInGameTradePokemon\" is not in the itempool! You can get the itempool from ROM.getItempool()");
        }
        if (result.item != Items.none)
            result.item = convertedIndex(result.item, Items.class, rom.getItemClass());
        return result;
    }

    public EncounterSet getScriptedWildEncounterSet(List<Pokemon> pokepool, EncounterSet oldArea)
    {
        PyFunction func = (PyFunction)interp.get("selectWildEncountersForArea");
        EncounterSet result = Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(oldArea)), EncounterSet.class);
        for(Encounter enc : result.encounters)
        {
            if(!inPool(pokepool, enc.pokemon))
            {
                throw new RuntimeException("Pokemon "+enc.pokemon.name+" was not in the given pokepool in function \"selectWildEncountersForArea\"!");
            }
            if(enc.level <= 0 || enc.level > 100)
            {
                throw new RuntimeException("Level of wild pokemon "+enc.pokemon.name+" selected in function \"selectWildEncountersForArea\" was not in the [1-100] range! The returned value was "+enc.level);
            }
            if(enc.maxLevel < 0 || enc.maxLevel > 100)
            {
                throw new RuntimeException("Max level of wild pokemon "+enc.pokemon.name+" selected in function \"selectWildEncountersForArea\" was not in the [0-100] range! The returned value was "+enc.maxLevel);
            }
        }
        return result;
    }

    public TrainerPokemon getScriptedTrainerPokemon(List<Pokemon> pokepool, Trainer trainer, TrainerPokemon oldPokemon, boolean megaSwap)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemon");
        TrainerPokemon result = Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(trainer), Py.java2py(oldPokemon), new PyBoolean(megaSwap)), TrainerPokemon.class);
        if(!inPool(pokepool, result.pokemon))
        {
            throw new RuntimeException("Trainer Pokemon "+result.pokemon.name+" was not in the given pokepool in function \"selectTrainerPokemon\"!");
        }
        if(result.level < 0 || result.level > 100)
        {
            throw new RuntimeException("Level of trainer Pokemon "+result.pokemon.name+" selected in function \"selectTrainerPokemon\" was not in the [1-100] range! The given value was "+result.level);
        }
        if(result.abilitySlot < 0 || result.abilitySlot > rom.abilitiesPerPokemon())
        {
            throw new RuntimeException("Ability slot of trainer Pokemon "+result.pokemon.name+" selected in function \"selectTrainerPokemon\" was not int he [0-"+rom.abilitiesPerPokemon()+"> range! The given value was "+result.abilitySlot+". You can use ROM.maxAbilities to get the number of abilities per pokemon in your script.");
        }
        return result;
    }

    public Integer getScriptedTrainerPokemonHeldItem(List<Integer> itempool, Trainer trainer, TrainerPokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectTrainerPokemonItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        int result = convertedIndex(func.__call__(pyItempool, Py.java2py(trainer), Py.java2py(pokemon)).asInt(), Items.class, rom.getItemClass());
        if(!itempool.contains(result) && result != Items.none)
        {
            throw new RuntimeException("Item "+Helper.toStr(result, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectTrainerPokemonItem\"!");
        }
        return result;
    }

    public Pokemon getScriptedWildHeldItemPokemon(ItemList itempool, Pokemon pokemon, boolean supportCommon, boolean supportRare, boolean supportGuaranteed, boolean supportDarkGrass)
    {
        PyFunction func = (PyFunction)interp.get("selectWildPokemonHeldItem");
        List<Integer> javaPool = toItemPool(itempool);
        PySequence pyPool = toPythonArray(javaPool, PyInteger.class, i -> new PyInteger(i));
        PyObject[] args = {pyPool, Py.java2py(pokemon), new PyBoolean(supportCommon), new PyBoolean(supportRare), new PyBoolean(supportGuaranteed), new PyBoolean(supportDarkGrass)};
        PyDictionary result = (PyDictionary)(func.__call__(args));
        int common = result.has_key(new PyString("common")) ? ((PyInteger)result.get(new PyString("common"))).asInt() : -2;
        int rare = result.has_key(new PyString("rare")) ? ((PyInteger)result.get(new PyString("rare"))).asInt() : -2;
        int guaranteed = result.has_key(new PyString("guaranteed")) ? ((PyInteger)result.get(new PyString("guaranteed"))).asInt() : -2;
        int darkGrass = result.has_key(new PyString("darkGrass")) ? ((PyInteger)result.get(new PyString("darkGrass"))).asInt() : -2;
        if(supportCommon && common != -2)
        {
            if(!javaPool.contains(common))
            {
                throw new RuntimeException("Common held item "+Helper.toStr(common, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectWildPokemonHeldItem\"!");
            }
            pokemon.commonHeldItem = common == -1 ? 0 : convertedIndex(common, Items.class, rom.getItemClass());
        }
        if(supportRare && rare != -2)
        {
            if(!javaPool.contains(rare))
            {
                throw new RuntimeException("Rare held item "+Helper.toStr(common, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectWildPokemonHeldItem\"!");
            }
            pokemon.rareHeldItem = rare == -1 ? 0 : convertedIndex(rare, Items.class, rom.getItemClass());;
        }
        if(supportGuaranteed && guaranteed != -2)
        {
            if(!javaPool.contains(guaranteed))
            {
                throw new RuntimeException("Guaranteed held item "+Helper.toStr(common, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectWildPokemonHeldItem\"!");
            }
            pokemon.guaranteedHeldItem = guaranteed == -1 ? 0 : convertedIndex(guaranteed, Items.class, rom.getItemClass());;
        }
        if(supportDarkGrass && darkGrass != -2)
        {
            if(!javaPool.contains(darkGrass))
            {
                throw new RuntimeException("Dark grass held item "+Helper.toStr(common, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectWildPokemonHeldItem\"!");
            }
            pokemon.darkGrassHeldItem = darkGrass == -1 ? 0 : convertedIndex(darkGrass, Items.class, rom.getItemClass());;
        }
        return pokemon;
    }

    public Move getScriptedMoveData(Move oldMove, boolean hasPhysicalSpecialSplit)
    {
        PyFunction func = (PyFunction)interp.get("setMoveData");
        Move result = Py.tojava(func.__call__(Py.java2py(oldMove), new PyBoolean(hasPhysicalSpecialSplit)), Move.class);
        if(!rom.typeInGame(result.type))
        {
            throw new RuntimeException("Move type Type."+result.type.name()+" of Move "+result.name+" selected in function \"setMoveData\" is not supported in generation "+rom.generationOfPokemon()+"!");
        }
        if(result.pp < 1)
        {
            throw new RuntimeException("PP of Move "+result.name+" selected in function \"setMoveData\" must be at least 1! The given value was "+result.pp);
        }
        if(result.power < 1)
        {
            throw new RuntimeException("Power of Move "+result.name+" selected in function \"setMoveData\" must be at least 1! The given value was "+result.power);
        }
        if(result.hitratio <= 0)
        {
            throw new RuntimeException("Power of Move "+result.name+" selected in function \"setMoveData\" must be at least 1! The given value was "+result.power);
        }
        return result;
    }

    public List<MoveLearnt> getScriptedLearntMoveset(Pokemon pokemon, List<MoveLearnt> oldMoveset)
    {
        PyFunction func = (PyFunction)interp.get("setLearntMoveset");
        List<Move> movepool = getMovePool(true, false);
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        PyArray pyMoveset = toPythonArray(oldMoveset, PyObject.class, move -> Py.java2py(move));
        List<MoveLearnt> result = toJavaList((PySequence) func.__call__(pyMovepool, Py.java2py(pokemon), pyMoveset), move -> Py.tojava(move, MoveLearnt.class));
        for(MoveLearnt ml : result)
        {
            if(ml.level <= 0 || ml.level > 100)
            {
                throw new RuntimeException("Learnt Move "+Helper.toStr(ml.move, Helper.Index.MOVE)+" selected in function \"setLearntMoveset\" has a level of "+ml.level+", which is outside of the [1-100] range!");
            }
            boolean foundMove = false;
            for(Move m : movepool){
                if(m == null){System.out.println("m is null"); }
                if(ml == null){System.out.println("ml is null"); }

                if(m.number == ml.move){
                    foundMove = true;
                    break;
                }
            }
            if(!foundMove)
            {
                throw new RuntimeException("Learnt Move "+Helper.toStr(ml.move, Helper.Index.MOVE)+" selected in function \"setLearntMoveset\" was not in the given movepool!");
            }
        }
        return result;
    }

    public List<MoveLearnt> getPostScriptedLearntMoveset(Pokemon pokemon, List<MoveLearnt> oldMoveset)
    {
        PyFunction func = (PyFunction)interp.get("setLearntMovesetPost");
        List<Move> movepool = getMovePool(true, false);
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        PyArray pyMoveset = toPythonArray(oldMoveset, PyObject.class, move -> Py.java2py(move));
        List<MoveLearnt> result = toJavaList((PySequence)func.__call__(pyMovepool, Py.java2py(pokemon), pyMoveset), move -> Py.tojava(move, MoveLearnt.class));
        for(MoveLearnt ml : result)
        {
            if(ml.level <= 0 || ml.level > 100)
            {
                throw new RuntimeException("Learnt move "+Helper.toStr(ml.move, Helper.Index.MOVE)+" selected in function \"setLearntMovesetPost\" has a level of "+ml.level+", which is outside of the [1-100] range!");
            }
            boolean foundMove = false;
            for(Move m : movepool){ if(m.number == ml.move){ foundMove = true; break; } }
            if(!foundMove)
            {
                throw new RuntimeException("Learnt Move "+Helper.toStr(ml.move, Helper.Index.MOVE)+" selected in function \"setLearntMoveset\" was not in the given movepool!");
            }
        }
        return result;
    }

    public List<Integer> getScriptedEggMoveset(Pokemon pokemon, List<Integer> oldMoveset)
    {
        int oldSize = oldMoveset.size();
        PyFunction func = (PyFunction)interp.get("setEggMoveset");
        List<Move> movepool = getMovePool(false, true);
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        PyArray pyMoveset = toPythonArray(oldMoveset, PyObject.class, i -> Helper.find(pyMovepool, i));
        List<Integer> result = toJavaList((PySequence)(func.__call__(pyMovepool, Py.java2py(pokemon), pyMoveset)), move -> Py.tojava(move, Move.class).number);
        if(result.size() != oldSize)
        {
            throw new RuntimeException("Egg moveset of pokemon "+pokemon.name+" selected in function \"setEggMoveset\" does not have the same number of moves as the original egg moveset! The original set had "+oldSize+" and the returned set has "+result.size()+". If you want to have less egg moves, you can repeat the same move multiple times. You cannot have more egg moves.");
        }
        for(Integer em : result)
        {
            boolean moveFound = false;
            for(Move m : movepool){ if(m.number == em){ moveFound = true; break; } }
            if(!moveFound)
            {
                throw new RuntimeException("Egg move Moves."+Helper.toStr(em, Helper.Index.MOVE).asString()+" selected in function \"setEggMoveset\" was not in the given movepool!");
            }
        }
        return result;
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
        if(hp <= 0)
        {
            throw new RuntimeException("HP of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+hp);
        }
        if(atk <= 0)
        {
            throw new RuntimeException("Attack of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+atk);
        }
        if(def <= 0)
        {
            throw new RuntimeException("Defense of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+def);
        }
        if(spatk <= 0)
        {
            throw new RuntimeException("Special attack of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+spatk);
        }
        if(spdef <= 0)
        {
            throw new RuntimeException("Special defense of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+spdef);
        }
        if(spd <= 0)
        {
            throw new RuntimeException("Speed of pokemon "+pokemon.name+" selected in function \"setBaseStats\" must be more than 0! The returned value was "+spd);
        }
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

        if(!rom.typeInGame(primary))
        {
            throw new RuntimeException("Selected primary type Type."+primary.name()+" is not supported in generation "+rom.generationOfPokemon());
        }
        if(secondary != null && !rom.typeInGame(secondary))
        {
            throw new RuntimeException("Selected secondary type Type."+secondary.name()+" is not supported in generation "+rom.generationOfPokemon());
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
                throw new Exception("Chose unavailable ability \""+Helper.toStr(ability, Helper.Index.ABILITY).asString()+"\" in function \"selectPokemonAbilities\"!");
            }
            chosen[chosenIter++] = ability;
        }

        //set abilities
        if(maxAbilities >= 1)
        {
            if(chosen[0] <= 0)
            {
                throw new Exception("No ability given to pokemon "+pokemon.name+" in function \"selectPokemonAbilities\"!");
            }
            pokemon.ability1 = chosen[0];
        }
        if(maxAbilities >= 2) {
            if(chosen[1] <= 0)
            {
                throw new Exception("No second ability given to pokemon "+pokemon.name+" in function \"selectPokemonAbilities\"!");
            }
            pokemon.ability2 = chosen[1];
        }
        if(maxAbilities >= 3) {
            if(chosen[2] <= 0)
            {
                throw new Exception("No third ability given to pokemon "+pokemon.name+" in function \"selectPokemonAbilities\"!");
            }
            pokemon.ability3 = chosen[2];
        }
    }

    public ExpCurve getScriptedEXPCurve(Pokemon pokemon)
    {
        PyFunction func = (PyFunction)interp.get("selectPokemonEXPCurve");
        return Py.tojava(func.__call__(Py.java2py(pokemon)), ExpCurve.class);
    }

    public int getScriptedFieldItem(int oldItem, List<Integer> itempool, boolean isTM)
    {
        PyFunction func = (PyFunction)interp.get("selectFieldItem");
        List<Integer> convertedItemPool = new ArrayList<>();
        for(Integer item : itempool)
            convertedItemPool.add(convertedIndex(item, rom.getItemClass(), Items.class));
        PyArray pyItempool = toPythonArray(convertedItemPool, PyInteger.class, i -> new PyInteger(i));
        int result = func.__call__(new PyInteger(convertedIndex(oldItem, rom.getItemClass(), Items.class)), pyItempool, new PyBoolean(isTM)).asInt();
        result = convertedIndex(result, Items.class, rom.getItemClass());
        if(!itempool.contains(result))
        {
            throw new RuntimeException("Field item Items."+Helper.toStr(result, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectFieldItem\"!");
        }
        return result;
    }

    public int getScriptedPickupItem(int oldItem, List<Integer> itempool)
    {
        PyFunction func = (PyFunction)interp.get("selectPickupItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        int result = convertedIndex(func.__call__(new PyInteger(oldItem), pyItempool).asInt(), Items.class, rom.getItemClass());
        if(!itempool.contains(result))
        {
            throw new RuntimeException("Pickup item \""+Helper.toStr(result, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectPickupItem\"!");
        }
        return result;
    }

    public Move getScriptedTMMove(Move oldMove, List<Move> movepool, boolean forcedDamagingMove)
    {
        PyFunction func = (PyFunction)interp.get("selectTMMove");
        PyArray pyMovepool = toPythonArray(movepool, PyObject.class, move -> Py.java2py(move));
        Move result = Py.tojava(func.__call__(Py.java2py(oldMove), pyMovepool, new PyBoolean(forcedDamagingMove)), Move.class);
        if(!movepool.contains(result))
        {
            throw new RuntimeException("TM move \""+result.name+" was not in the given movePool in function \"selectTMMove\"!");
        }
        return result;
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
        Move result = Py.tojava(func.__call__(Py.java2py(oldMove), pyMovepool, new PyBoolean(forcedDamagingMove)), Move.class);
        if(!movepool.contains(result))
        {
            throw new RuntimeException("Tutor move \""+result.name+" was not in the given movePool in function \"selectTutorMove\"!");
        }
        return result;
    }

    public int getScriptedStarterHeldItem(int oldItem, List<Integer> itempool)
    {
        PyFunction func = (PyFunction)interp.get("selectStarterHeldItem");
        PyArray pyItempool = toPythonArray(itempool, PyInteger.class, i -> new PyInteger(i));
        int result = func.__call__(new PyInteger(oldItem), pyItempool).asInt();
        if(!itempool.contains(result) && result != Items.none)
        {
            throw new RuntimeException("Starter held item \""+Helper.toStr(result, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectStarterHeldItem\"!");
        }
        return convertedIndex(result, Items.class, rom.getItemClass());
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

            if(!inPool(pokepool, to))
            {
                throw new RuntimeException("Evolution pokemon "+to.name+" was not in the given pokepool in  function \"selectEvolutions\"!");
            }

            int level = 0;
            int extraInfo = 0;
            if(evoType.usesLevel())
            {
                if(!current.has_key(new PyString("level"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"level\" key!"); }
                level = current.get(new PyString("level")).asInt();
                if(level <= 0 || level > 100){
                    throw new RuntimeException("Evolution level for Evolution "+from.name+" => "+to.name+" selected in function \"selectEvolutions\" is not within the [1-100] range! The given value was "+level);
                }
                extraInfo = level;
            }
            else if(evoType.usesMove())
            {
                if(!current.has_key(new PyString("move"))){ throw new RuntimeException("Evolution dictionary with type EvolutionType."+evoType.name()+" must have a \"move\" key!"); }
                extraInfo = current.get(new PyString("move")).asInt();
                final int move = extraInfo;
                List<Move> movepool = rom.getAllMovesOf(from, true);
                boolean found = false;
                for(Move mv : movepool)
                {
                    if(mv.number == move){ found = true; break; }
                }
                if(!found)
                {
                    throw new RuntimeException("Evolution move for Evolution "+from.name+" => "+to.name+" selected in function \"selectEvolutions\" cannot be learned by the evolving pokemon or any of its pre-evolutions! The given move was Moves."+Helper.toStr(move, Helper.Index.MOVE).asString()+". You can get the moves available to a pokemon in your script from ROM.getAllMovesOf(pokemon, includePrevos)");
                }
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
                List<Pokemon> fullPool = rom.getPokemonInclFormes();
                boolean found = false;
                for(Pokemon fpoke : fullPool)
                {
                    if(fpoke.number == extraInfo){ found = true; break; }
                }
                if(!found)
                {
                    throw new RuntimeException("Pokemon Species."+Helper.toStr(extraInfo, Helper.Index.POKEMON).asString()+" selected for Evolution."+Helper.toStr(evoType)+" in function \"selectEvolutions\" is not available in the current ROM! Note: it might have been removed from the pool in pokemon limit settings or the \"limitPokemon\" function.");
                }
            }

            boolean carryStats = result.__len__() == 1 || evoType == EvolutionType.LEVEL_CREATE_EXTRA;
            Evolution finalEvo = new Evolution(from, to, carryStats, evoType, extraInfo);
            finalEvo.level = level;
            finalEvo.forme = to.formeNumber;
            finalEvo.formeSuffix = to.formeSuffix;

            toReturn.add(finalEvo);
        }

        //verify all evolution types are supported by the current generation
        for(Evolution evo : toReturn)
        {
            if(evo.type.toIndex(rom.generationOfPokemon()) == -1)
            {
                throw new RuntimeException("EvolutionType."+evo.type.name()+" selected in function \"selectEvolutions\" is not supported in generation "+rom.generationOfPokemon()+"!");
            }
            if(evo.type.usesItem())
            {
                int item = evo.extraInfo;
                List<Integer> itempool = toItemPool(rom.getAllowedItems());
                if(!itempool.contains(item))
                {
                    throw new RuntimeException("Evolution item Items."+Helper.toStr(item, Helper.Index.ITEM).asString()+" selected in function \"selectEvolutions\" is not in the itempool! You can get the itempool from ROM.getItempool()");
                }
                if(evo.type.needsEvolutionItem())
                {
                    List<Integer> evoItems = rom.getEvolutionItems();
                    if(!evoItems.contains(item))
                    {
                        throw new RuntimeException("Evolution item Items."+Helper.toStr(item, Helper.Index.ITEM).asString()+" selected in function \"selectEvolutions\" is not an official evolution item, while the selected EvolutionType.\""+Helper.toStr(evo.type).asString()+"\" does require this! You can get a list of evolution items from ROM.getEvolutionItems()");
                    }
                }
                evo.extraInfo = convertedIndex(item, Items.class, rom.getItemClass());
            }
        }

        //return result
        return toReturn;
    }

    public int getScriptedShopItem(ItemList itempool, Shop oldShop, int oldItem)
    {
        PyFunction func = (PyFunction)interp.get("selectShopItem");
        List<Integer> javaPool = toItemPool(itempool);
        PySequence pyPool = toPythonArray(javaPool, PyInteger.class, i -> new PyInteger(i));
        int result = func.__call__(pyPool, Py.java2py(oldShop), new PyInteger(oldItem)).asInt();
        if(!javaPool.contains(result))
        {
            throw new RuntimeException("Special shop item \""+Helper.toStr(result, Helper.Index.ITEM).asString()+" was not in the given ItemPool in function \"selectShopItem\"!");
        }
        return convertedIndex(result, Items.class, rom.getItemClass());
    }

    public int getScriptedShopItemPrice(int item, int balancedPrice)
    {
        PyFunction func = (PyFunction)interp.get("selectShopItemPrice");
        item = convertedIndex(item, rom.getItemClass(), Items.class);
        int result = ((PyInteger)func.__call__(new PyInteger(item), new PyInteger(balancedPrice * 10))).asInt() / 10;
        if(result <= 0)
            throw new RuntimeException("Shop item price for Items."+Helper.toStr(item, Helper.Index.ITEM).asString()+" selected in function \"selectShopItemPrice\" must be more than zero! The given value was "+result+"!");
        return result;
    }

    public TotemPokemon getScriptedTotemPokemon(List<Pokemon> pokepool, TotemPokemon old)
    {
        PyFunction func = (PyFunction)interp.get("selectTotemPokemon");
        TotemPokemon result = Py.tojava(func.__call__(pyPokePool(pokepool), Py.java2py(old)), TotemPokemon.class);
        if(!pokepool.contains(result.pkmn))
            throw new RuntimeException("Totem Pokemon \""+Helper.toStr(result.pkmn.name).asString()+"\" selected in function \"selectTotemPokemon\" was not in the given pokepool!");
        if(result.level <= 0 || result.level > 100)
            throw new RuntimeException("Totem Pokemon "+result.pkmn.name+" selected in function \"selectTotemPokemon\" has a level outside of the [1-100] range! The given level was "+result.level);
        if(result.maxLevel < 0 || result.maxLevel > 100)
            throw new RuntimeException("Totem Pokemon " + result.pkmn.name + " selected in function \"selectTotemPokemon\" has a maxLevel outside of the [0-100] range! The given level was " + result.maxLevel);
        List<Integer> itempool = toItemPool(rom.getAllowedItems());
        if(!itempool.contains(result.heldItem) && result.heldItem != Items.none)
            throw new RuntimeException("Held item Items."+Helper.toStr(result.heldItem, Helper.Index.ITEM).asString()+" selected in function \"selectTotemPokemon\" is not in the itempool! You can get the itempool from ROM.getItempool()");
        if(result.heldItem != Items.none)
            result.heldItem = convertedIndex(result.heldItem, Items.class, rom.getItemClass());
        return result;
    }

    public static int convertedIndex(int generalItem, Class startClass, Class endClass)
    {
        if(endClass == startClass) { //return general item if the itemclass used by the romhandler is the general one
            return generalItem;
        }

        //get the name of the variable in the start class
        String itemName = "";

        Field[] sourceFields = startClass.getFields();
        for(Field fld : sourceFields)
        {
            try {
                if ((int) fld.get(null) == generalItem) {
                    itemName = fld.getName();
                    break;
                }
            }
            catch(IllegalArgumentException e){}
            catch(IllegalAccessException e){}
        }

        HashMap<String, String> toGeneralItems = new HashMap<>();
        toGeneralItems.put("parlyzHeal", "paralyzeHeal");
        toGeneralItems.put("xDefend", "xDefense");
        toGeneralItems.put("xSpecial", "xSpAtk");
        toGeneralItems.put("orangeMail", "mail1");
        toGeneralItems.put("harborMail", "mail2");
        toGeneralItems.put("glitterMail", "mail3");
        toGeneralItems.put("mechMail", "mail4");
        toGeneralItems.put("woodMail", "mail5");
        toGeneralItems.put("waveMail", "mail6");
        toGeneralItems.put("beadMail", "mail7");
        toGeneralItems.put("shadowMail", "mail8");
        toGeneralItems.put("tropicMail", "mail9");
        toGeneralItems.put("dreamMail", "mail10");
        toGeneralItems.put("fabMail", "mail11");
        toGeneralItems.put("retroMail",	"mail12");
        toGeneralItems.put("stick", "leek");
        toGeneralItems.put("nothing", "none");
        HashMap<String, String> fromGeneralItems = new HashMap<>();
        for(Map.Entry<String, String> entry : toGeneralItems.entrySet())
            fromGeneralItems.put(entry.getValue(), entry.getKey());

        if(startClass.getName().equals("com.dabomstew.pkrandom.constants.Items") && fromGeneralItems.containsKey(itemName))
            itemName = fromGeneralItems.get(itemName);
        else if(endClass.getName().equals("com.dabomstew.pkrandom.constants.Items"))
        {
            if(toGeneralItems.containsKey(itemName))
                itemName = toGeneralItems.get(itemName);
            else if(itemName.toLowerCase().contains("unknown"))
                throw new RuntimeException("Item "+itemName+" is not in the general Items class!");
        }

        if(itemName.length() == 0)
            throw new RuntimeException("Item "+generalItem+" was not found in the current ROM!");

        //try to find the variable in the end class and return its value when found
        Field[] targetFields = endClass.getFields();
        for(Field fld : targetFields)
        {
            if(fld.getName().toLowerCase().equals(itemName.toLowerCase()))
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

    private void addROMInfo()
    {
        interp.set("temp", Py.java2py(rom));
        interp.set("temp2", Py.java2py(settings));
        String defs = "\n\n";
        defs += "from com.dabomstew.pkrandom.pokemon import Type\n\n";
        defs += "class ROM:";
        String startVar = "\n\t";
        defs += startVar + "generation = "+rom.generationOfPokemon();
        defs += startVar + "name = \""+rom.getROMName()+"\"";
        defs += startVar + "extension = \""+rom.getDefaultExtension()+"\"";
        defs += startVar + "code = \""+rom.getROMCode()+"\"";
        defs += startVar + "maxNicknameLen = "+rom.maxTradeNicknameLength();
        defs += startVar + "maxOTLen = "+rom.maxTradeOTNameLength();
        defs += startVar + "maxTrainerClassLen = "+rom.maxTrainerClassNameLength();
        defs += startVar + "maxTrainerNameLen = "+rom.maxTrainerNameLength();
        defs += startVar + "maxAbilities = "+rom.abilitiesPerPokemon();
        defs += startVar + "hasPhysicalSpecialSplit = "+(rom.hasPhysicalSpecialSplit() ? "True" : "False");
        defs += startVar + "hasMoveTutors = "+(rom.hasMoveTutors()  ? "True" : "False");
        defs += startVar + "maxIV = "+(rom.hasDVs() ? 16 : 32);
        defs += startVar + "__romHandler = temp";
        defs += startVar + "__settings = temp2";

        defs += startVar + "supportedTypes = [";
        for (Type typ : Type.values()) {
            if(rom.typeInGame(typ)){ defs += "Type."+typ.name()+","; }
        }
        defs = defs.substring(0, defs.length() - 1) + "]";

        defs += startVar + "HMs = [";

        if(rom.getHMCount() > 0)
        {
            for(Integer hm : rom.getHMMoves())
                defs += hm + ",";
            defs = defs.substring(0, defs.length() - 1);
        }
        defs += "]";

        defs +=   startVar + "@staticmethod"
                + startVar + "def getMovepool(levelup = None, excludeHMs = None):"
                + startVar + "\tif levelup is None:"
                + startVar + "\t\treturn Helper.getMoves(ROM.__romHandler)"
                + startVar + "\telif excludeHMs is None:"
                + startVar + "\t\treturn Helper.getMoves(ROM.__romHandler, levelup)"
                + startVar + "\telse:"
                + startVar + "\t\treturn Helper.getMoves(ROM.__romHandler, levelup, excludeHMs)";

        defs +=   startVar + "@staticmethod"
                + startVar + "def getPokepool(includeFormes = None):"
                + startVar + "\tif(includeFormes is None):"
                + startVar + "\t\treturn Helper.getPokepool(ROM.__romHandler)"
                + startVar + "\telse:"
                + startVar + "\t\treturn Helper.getPokepool(ROM.__romHandler, includeFormes)";

        defs += startVar + "@staticmethod" + startVar + "def getItempool():"          + startVar + "\treturn Helper.getItempool(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getConsumableItems():"   + startVar + "\treturn Helper.getConsumableItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getHeldItems():"         + startVar + "\treturn Helper.getHeldItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getEvolutionItems():"    + startVar + "\treturn Helper.getEvolutionItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getGoodItems():"         + startVar + "\treturn Helper.getGoodItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getOPShopItems():"       + startVar + "\treturn Helper.getOPShopItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getRegularFieldItems():" + startVar + "\treturn Helper.getRegularFieldItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getRegularShopItems():"  + startVar + "\treturn Helper.getRegularShopItems(ROM.__romHandler)";

        defs +=   startVar + "@staticmethod"
                + startVar + "def supportsEvolution(evoType):"
                + startVar + "\treturn Helper.supportsEvolutionType(ROM.__romHandler, evoType)";

        defs +=   startVar + "@staticmethod"
                + startVar + "def supportedEvolutionTypes():"
                + startVar + "\treturn Helper.getSupportedEvolutionTypes(ROM.__romHandler)";

        defs += startVar + "@staticmethod" + startVar + "def getAllMovesOf(poke, includePrevos = False):"  + startVar + "\treturn Helper.getAllPossibleMoves(ROM.__romHandler, poke, includePrevos)";
        defs += startVar + "@staticmethod" + startVar + "def getLearntMovesOf(poke, includePrevos = False):"  + startVar + "\treturn Helper.getLearntMoves(ROM.__romHandler, poke, includePrevos)";
        defs += startVar + "@staticmethod" + startVar + "def getTMMovesOf(poke, includePrevos = False):"  + startVar + "\treturn Helper.getTMMoves(ROM.__romHandler, poke, includePrevos)";
        defs += startVar + "@staticmethod" + startVar + "def getTutorMovesOf(poke, includePrevos = False):"  + startVar + "\treturn Helper.getTutorMoves(ROM.__romHandler, poke, includePrevos)";
        defs += startVar + "@staticmethod" + startVar + "def getEggMovesOf(poke):"  + startVar + "\treturn Helper.getEggMoves(ROM.__romHandler, poke)";

        defs += startVar + "@staticmethod" + startVar + "def getWildEncounterSets():" + startVar + "\treturn Helper.getWildEncounterSets(ROM.__romHandler, ROM.__settings)";
        defs += startVar + "@staticmethod" + startVar + "def getInGameTrades():" + startVar + "\treturn Helper.getInGameTrades(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getMegaEvolutions():" + startVar + "\treturn Helper.getMegaEvolutions(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getMoveTutorMoves():" + startVar + "\treturn Helper.getMoveTutorMoves(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getStarters():" + startVar + "\treturn Helper.getStarters(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getStaticPokemon():" + startVar + "\treturn Helper.getStaticPokemon(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getTMMoves():" + startVar + "\treturn Helper.getTMMoves(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getTrainers():" + startVar + "\treturn Helper.getTrainers(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getTotems():" + startVar + "\treturn Helper.getTotems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getTrainerNames():" + startVar + "\treturn Helper.getTrainerNames(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getTrainerClassNames():" + startVar + "\treturn Helper.getTrainerClassNames(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getUselessAbilities():" + startVar + "\treturn Helper.getUselessAbilities(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getXItems():" + startVar + "\treturn Helper.getXItems(ROM.__romHandler)";
        defs += startVar + "@staticmethod" + startVar + "def getAbilityPool():" + startVar + "\treturn Helper.getAbilityPool(ROM.__romHandler)";

        defs += "\n\n";
        interp.exec(defs);
        interp.set("temp", null);
        interp.set("temp2", null);
    }

    public static void initJythonDoc(JythonSyntaxDocument jdoc)
    {
        JythonScope dummy = jdoc.getGlobalScope();

        JythonScope.Class romCls = dummy.new Class("ROM", -1);
        romCls.methods.add(dummy.new Function("getMovePool", -1));
        romCls.methods.add(dummy.new Function("getPokepool", -1));
        romCls.methods.add(dummy.new Function("getItempool", -1));
        romCls.methods.add(dummy.new Function("getConsumableItems", -1));
        romCls.methods.add(dummy.new Function("getHeldItems", -1));
        romCls.methods.add(dummy.new Function("getEvolutionItems", -1));
        romCls.methods.add(dummy.new Function("getGoodItems", -1));
        romCls.methods.add(dummy.new Function("getOPShopItems", -1));
        romCls.methods.add(dummy.new Function("getRegularFieldItems", -1));
        romCls.methods.add(dummy.new Function("getRegularShopItems", -1));
        romCls.methods.add(dummy.new Function("supportsEvolution", -1));
        romCls.methods.add(dummy.new Function("supportedEvolutionTypes", -1));
        romCls.methods.add(dummy.new Function("getAllMovesOf", -1));
        romCls.methods.add(dummy.new Function("getLearntMovesOf", -1));
        romCls.methods.add(dummy.new Function("getTMMovesOf", -1));
        romCls.methods.add(dummy.new Function("getTutorMovesOf", -1));
        romCls.methods.add(dummy.new Function("getEggMovesOf", -1));
        romCls.methods.add(dummy.new Function("getWildEncounterSets", -1));
        romCls.methods.add(dummy.new Function("getInGameTrades", -1));
        romCls.methods.add(dummy.new Function("getMegaEvolutions", -1));
        romCls.methods.add(dummy.new Function("getMoveTutorMoves", -1));
        romCls.methods.add(dummy.new Function("getStarters", -1));
        romCls.methods.add(dummy.new Function("getStaticPokemon", -1));
        romCls.methods.add(dummy.new Function("getTMMoves", -1));
        romCls.methods.add(dummy.new Function("getTrainers", -1));
        romCls.methods.add(dummy.new Function("getTotems", -1));
        romCls.methods.add(dummy.new Function("getTrainerNames", -1));
        romCls.methods.add(dummy.new Function("getTrainerClassNames", -1));
        romCls.methods.add(dummy.new Function("getUselessAbilities", -1));
        romCls.methods.add(dummy.new Function("getXItems", -1));
        romCls.methods.add(dummy.new Function("getAbilityPool", -1));
        romCls.members.add(dummy.new Variable("name", -1));
        romCls.members.add(dummy.new Variable("extension", -1));
        romCls.members.add(dummy.new Variable("generation", -1));
        romCls.members.add(dummy.new Variable("code", -1));
        romCls.members.add(dummy.new Variable("maxNicknameLen", -1));
        romCls.members.add(dummy.new Variable("maxOTLen", -1));
        romCls.members.add(dummy.new Variable("maxTrainerClassLen", -1));
        romCls.members.add(dummy.new Variable("maxTrainerNameLen", -1));
        romCls.members.add(dummy.new Variable("maxAbilities", -1));
        romCls.members.add(dummy.new Variable("hasPhysicalSpecialSplit", -1));
        romCls.members.add(dummy.new Variable("hasMoveTutors", -1));
        romCls.members.add(dummy.new Variable("maxIV", -1));
        romCls.members.add(dummy.new Variable("supportedTypes", -1));
        romCls.members.add(dummy.new Variable("HMs", -1));

        jdoc.addExtraGlobalClass(romCls);
    }

    private static boolean inPool(List<Pokemon> pokepool, Pokemon poke)
    {
        return pokepool.contains(poke);
    }

    private static boolean inPool(List<Pokemon> pokepool, List<Pokemon> pokes)
    {
        return pokepool.containsAll(pokes);
    }

    private List<Integer> toItemPool(ItemList itemList)
    {
        List<Integer> itempool = new ArrayList<>();
        for(int k = 0; k <= itemList.getHighestIndex(); k++)
        {
            if(itemList.isAllowed(k))
            {
                itempool.add(convertedIndex(k, rom.getItemClass(), Items.class));
            }
        }
        return itempool;
    }

    private List<Move> getMovePool(boolean levelup, boolean egg)
    {
        ArrayList<Move> movepool = new ArrayList<>(rom.getMoves());
        movepool.removeIf(Objects::isNull);
        movepool.removeAll(rom.getIllegalMoves());
        if(levelup)
        {
            movepool.removeAll(rom.getMovesBannedFromLevelup());
            movepool.removeAll(rom.getHMMoves());
        }
        if(egg)
        {
            movepool.removeAll(rom.getHMMoves());
        }
        if(movepool == null)
        {
            System.out.println("MOVEPOOL IS NULL");
        }
        return movepool;
    }

}
