package com.dabomstew.pkrandom.Script;

import com.dabomstew.pkrandom.Settings;
import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Items;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.constants.Species;
import com.dabomstew.pkrandom.pokemon.*;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import org.python.core.*;
import sun.font.Script;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Helper {

    //turns a java string into a (sanitized) python string
    public static PyString toStr(String value)
    {
        if(value == null) { return new PyString(""); }
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
    public static <T> PyList seq(List<T> list)
    {
        PyList result = new PyList();
        for(T item : list)
        {
            result.append(Py.java2py(item));
        }
        return result;
    }

    public static boolean hasType(Pokemon poke, Type theType)
    {
        return poke.primaryType == theType || poke.secondaryType == theType;
    }

    public static PyString toStr(Enum val)
    {
        return new PyString(val.name());
    }

    public enum Index{
        ABILITY, ITEM, MOVE, POKEMON
    }
    public static PyString toStr(int val, Index valType)throws RuntimeException
    {
        Field[] fields = null;
        switch(valType)
        {
            case ABILITY:
                if(val == Abilities.staticTheAbilityNotTheKeyword){ return new PyString("static"); } //funny edge case
                fields = Abilities.class.getFields();
                break;
            case ITEM:
                fields = Items.class.getFields();
                break;
            case MOVE:
                if(val == Moves.returnTheMoveNotTheKeyword) { return new PyString("return"); } //funny edge case
                fields = Moves.class.getFields();
                break;
            case POKEMON:
                fields = Species.class.getFields();
                break;
            default:
                throw new RuntimeException("Unsupported Index type given to Helper.Str(int val, Helper.Index valType)!");
        }

        for(Field current : fields)
        {
            try{
                int currentVal = (int)current.get(null);
                if(currentVal == val)
                {
                    return new PyString(current.getName());
                }
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
                throw new RuntimeException("IllegalAccessException thrown!");
            }
        }

        throw new RuntimeException("Name of "+valType.name()+" not found!");
    }

    public static int index(String name, Index valType)throws RuntimeException
    {
        Field[] fields = null;
        switch(valType)
        {
            case ABILITY:
                if(name.equals("static")){ return Abilities.staticTheAbilityNotTheKeyword; } //funny edge case
                fields = Abilities.class.getFields();
                break;
            case ITEM:
                fields = Items.class.getFields();
                break;
            case MOVE:
                if(name.equals("return")) { return Moves.returnTheMoveNotTheKeyword; } //funny edge case
                fields = Moves.class.getFields();
                break;
            case POKEMON:
                fields = Species.class.getFields();
                break;
            default:
                throw new RuntimeException("Unsupported Index type given to Helper.index(String name, Helper.Index valType)!");
        }

        for(Field current : fields)
        {
            if(current.getName() == name)
            {
                try{
                    return (int)current.get(null);
                }
                catch(IllegalAccessException e)
                {
                    e.printStackTrace();
                    throw new RuntimeException("IllegalAccessException thrown!");
                }
            }
        }

        throw new RuntimeException("Name of "+valType.name()+" not found!");
    }

    public static PyObject find(PySequence seq, int number)
    {
        for(int k = 0; k < seq.__len__(); k++)
        {
            PyObject item = seq.__getitem__(k);
            try{
                Pokemon poke = Py.tojava(item, Pokemon.class);
                if(poke != null) {
                    if (poke.number == number) {
                        return item;
                    }
                }
            }
            catch(PyException ex){}
            try{
                Move move = Py.tojava(item, Move.class);
                if(move != null)
                {
                    if(move.number == number)
                    {
                        return item;
                    }
                }
            }
            catch(PyException ex){}
            try{
                MoveLearnt move = Py.tojava(item, MoveLearnt.class);
                if(move != null)
                {
                    if(move.move == number)
                    {
                        return item;
                    }
                }
            }
            catch(PyException ex){}
        }
        return null;
    }

    public static PySequence similarStrength(PySequence pyPokepool, Pokemon poke)
    {
        return similarStrength(pyPokepool, poke, 3);
    }

    public static PySequence similarStrength(PySequence pyPokepool, Pokemon poke, int targetSize)
    {
        return similarStrength(pyPokepool, poke, targetSize, (Integer.MAX_VALUE / 10) - 255, true);
    }

    public static PySequence similarStrength(PySequence pyPokepool, Pokemon poke, int targetSize, int averageLevel)
    {
        return similarStrength(pyPokepool, poke, targetSize, averageLevel, true);
    }

    public static PySequence similarStrength(PySequence pyPokepool, TrainerPokemon tpoke)
    {
        return similarStrength(pyPokepool, tpoke, 3);
    }

    public static PySequence similarStrength(PySequence pyPokepool, TrainerPokemon tpoke, int targetSize)
    {
        return similarStrength(pyPokepool, tpoke.pokemon, targetSize, tpoke.level, true);
    }

    public static PySequence similarStrength(PySequence pyPokepool, Encounter wpoke)
    {
        return similarStrength(pyPokepool, wpoke, 3);
    }

    public static PySequence similarStrength(PySequence pyPokepool, Encounter wpoke, int targetSize)
    {
        return similarStrength(pyPokepool, wpoke.pokemon, targetSize, (wpoke.level + wpoke.maxLevel) / 2, true);
    }

    public static PySequence similarStrength(PySequence pyPokepool, StaticEncounter spoke)
    {
        return similarStrength(pyPokepool, spoke, 3);
    }

    public static PySequence similarStrength(PySequence pyPokepool, StaticEncounter spoke, int targetSize)
    {
        return similarStrength(pyPokepool, spoke.pkmn, targetSize, (spoke.level + spoke.maxLevel) / 2, true);
    }

    private static PySequence similarStrength(PySequence pyPokepool, Pokemon poke, int targetSize, int bstBalanceLevel, boolean trueCall)
    {
        List<Pokemon> pokepool = ScriptInstance.toJavaList(pyPokepool, pkmn -> Py.tojava(pkmn, Pokemon.class));

        // start with within 10% and add 5% either direction till we find
        // something
        int balancedBST = bstBalanceLevel * 10 + 250;
        int currentBST = Math.min(poke.bstForPowerLevels(), balancedBST);
        int minTarget = currentBST - currentBST / 10;
        int maxTarget = currentBST + currentBST / 10;
        List<Pokemon> canPick = new ArrayList<>();
        int expandRounds = 0;
        while (canPick.isEmpty() || (canPick.size() < targetSize && expandRounds < 2)) {
            for (Pokemon pk : pokepool) {
                if (pk.bstForPowerLevels() >= minTarget
                        && pk.bstForPowerLevels() <= maxTarget) {
                    canPick.add(pk);
                }
            }
            minTarget -= currentBST / 20;
            maxTarget += currentBST / 20;
            expandRounds++;
        }

        return ScriptInstance.toPythonArray(canPick, PyObject.class, pkmn -> Py.java2py(pkmn));
    }

    public static PySequence getMoves(RomHandler rom) { return getMoves(rom, false); }
    public static PySequence getMoves(RomHandler rom, boolean levelup) { return getMoves(rom, levelup, false); }
    public static PySequence getMoves(RomHandler rom, boolean levelup, boolean excludeHM)
    {
        ArrayList<Move> filtered = new ArrayList<>();
        List<Move> moves = rom.getMoves();
        List<Integer> illegalMoves = rom.getIllegalMoves();
        List<Integer> HMs = rom.getHMMoves();
        List<Integer> bannedFromLevel = rom.getMovesBannedFromLevelup();
        for(Move move : moves)
        {
            if(move == null){ continue; }
            if(illegalMoves.contains(move.number)){ continue; }
            if(levelup && bannedFromLevel.contains(move.number)){ continue; }
            if(excludeHM && HMs.contains(move.number)){ continue; }
            filtered.add(move);
        }
        return ScriptInstance.toPythonArray(filtered, PyObject.class, move -> Py.java2py(move));
    }

    public static PySequence getPokepool(RomHandler rom){ return getPokepool(rom, false); }
    public static PySequence getPokepool(RomHandler rom, boolean includeFormes)
    {
        List<Pokemon> pokes = (includeFormes ? rom.getPokemonInclFormes() : rom.getPokemon());
        ArrayList<Pokemon> filteredPokes = new ArrayList<>();
        for(Pokemon poke : pokes){
            if(poke != null)
            {
                filteredPokes.add(poke);
            }
        }
        return ScriptInstance.toPythonArray(filteredPokes, PyObject.class, poke -> Py.java2py(poke));
    }

    private static PySequence toSeq(RomHandler rom, ItemList list)
    {
        final int highest = list.getHighestIndex();
        PyArray seq = new PyArray(PyInteger.class, null);
        for(int k = 0; k <= highest; k++)
        {
            if(list.isAllowed(k))
            {
                seq.append(new PyInteger(ScriptInstance.convertedIndex(k, rom.getItemClass(), Items.class)));
            }
        }
        return seq;
    }

    private static PySequence toSeq(RomHandler rom, ItemList list, List<Integer> req)
    {
        List<Integer> result = new ArrayList<>();
        for(Integer item : req)
        {
            if(list.isAllowed(item)){result.add(ScriptInstance.convertedIndex(item, rom.getItemClass(), Items.class));}
        }
        return ScriptInstance.toPythonArray(result, PyInteger.class, PyInteger::new);
    }

    public static PySequence getItempool(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems());
    }

    public static PySequence getConsumableItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getAllConsumableHeldItems());
    }

    public static PySequence getHeldItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getAllHeldItems());
    }

    public static PySequence getEvolutionItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getEvolutionItems());
    }

    public static PySequence getGoodItems(RomHandler rom)
    {
        return toSeq(rom, rom.getNonBadItems());
    }

    public static PySequence getOPShopItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getOPShopItems());
    }

    public static PySequence getRegularFieldItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getRegularFieldItems());
    }

    public static PySequence getRegularShopItems(RomHandler rom)
    {
        return toSeq(rom, rom.getAllowedItems(), rom.getRegularShopItems());
    }

    public static PySequence getAbilityPool(RomHandler rom)
    {
        int highest = rom.highestAbilityIndex();
        List<Integer> pool = new ArrayList<>();
        for(int k = 1; k <= highest; k++)
            pool.add(k);
        return ScriptInstance.toPythonArray(pool, PyInteger.class, PyInteger::new);
    }

    public static PyBoolean supportsEvolutionType(RomHandler rom, EvolutionType evo)
    {
        return new PyBoolean(evo.toIndex(rom.generationOfPokemon()) != -1);
    }

    public static PySequence getSupportedEvolutionTypes(RomHandler rom)
    {
        final int gen = rom.generationOfPokemon();
        List<EvolutionType> result = new ArrayList<>();
        for(EvolutionType evo : EvolutionType.values())
        {
            if(evo.toIndex(gen) != -1){ result.add(evo); }
        }
        return ScriptInstance.toPythonArray(result, PyObject.class, evo -> Py.java2py(evo));
    }

    public static PySequence getAllPossibleMoves(RomHandler rom, Pokemon poke, boolean includePrevos)
    {
        return ScriptInstance.toPythonArray(rom.getAllMovesOf(poke, includePrevos), PyObject.class, move -> Py.java2py(move));
    }

    public static PySequence getLearntMoves(RomHandler rom, Pokemon poke, boolean includePrevos)
    {
        return ScriptInstance.toPythonArray(rom.getLearntMovesOf(poke, includePrevos), PyObject.class, move -> Py.java2py(move));
    }

    public static PySequence getTMMoves(RomHandler rom, Pokemon poke, boolean includePrevos)
    {
        return ScriptInstance.toPythonArray(rom.getTMMovesOf(poke, includePrevos), PyObject.class, move -> Py.java2py(move));
    }

    public static PySequence getTutorMoves(RomHandler rom, Pokemon poke, boolean includePrevos)
    {
        if(!rom.hasMoveTutors())
        {
            throw new RuntimeException("Tried to call ROM.getTutorMovesOf() while this ROM does not support move tutors! You can check this in your script with ROM.hasMoveTutors");
        }
        return ScriptInstance.toPythonArray(rom.getTutorMovesOf(poke, includePrevos), PyObject.class, Py::java2py);
    }

    public static PySequence getEggMoves(RomHandler rom, Pokemon poke)
    {
        return ScriptInstance.toPythonArray(rom.getEggMovesOf(poke), PyObject.class, Py::java2py);
    }

    public static PySequence getWildEncounterSets(RomHandler rom, Settings settings)
    {
        return ScriptInstance.toPythonArray(rom.getEncounters(settings.isUseTimeBasedEncounters()), PyObject.class, enc -> Py.java2py(enc));
    }

    public static PySequence getInGameTrades(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getIngameTrades(), PyObject.class, Py::java2py);
    }

    public static PySequence getMegaEvolutions(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getMegaEvolutions(), PyObject.class, Py::java2py);
    }

    public static PySequence getMoveTutorMoves(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getMoveTutorMoves(), PyObject.class, Py::java2py);
    }

    public static PySequence getStarters(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getStarters(), PyObject.class, Py::java2py);
    }

    public static PySequence getStaticPokemon(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getStaticPokemon(), PyObject.class, Py::java2py);
    }

    public static PySequence getTMMoves(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getTMMoves(), PyObject.class, PyInteger::new);
    }

    public static PySequence getTrainers(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getTrainers(), PyObject.class, Py::java2py);
    }

    public static PySequence getTotems(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getTotemPokemon(), PyObject.class, Py::java2py);
    }

    public static PySequence getTrainerNames(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getTrainerNames(), PyObject.class, PyString::new);
    }

    public static PySequence getTrainerClassNames(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getTrainerClassNames(), PyObject.class, PyString::new);
    }

    public static PySequence getUselessAbilities(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getUselessAbilities(), PyObject.class, PyInteger::new);
    }

    public static PySequence getXItems(RomHandler rom)
    {
        return ScriptInstance.toPythonArray(rom.getXItems(), PyObject.class, PyInteger::new);
    }

    public static String DefinitionString()
    {
        String helperImport = "from com.dabomstew.pkrandom.Script import Helper";
        String toStrDef = "def toStr(val, index = None):\n\tif index is None:\n\t\treturn Helper.toStr(val)\n\treturn Helper.toStr(val, index)";
        String indexDef = "def index(name, index):\n\treturn Helper.index(name, index)";
        String simStrengthDef = "def similarStrength(pokepool, poke, targetSize = None, balancedBST = None):\n\tif(balancedBST is None):\n\t\tif targetSize is None:\n\t\t\treturn Helper.similarStrength(pokepool, poke)\n\t\treturn Helper.similarStrength(pokepool, poke, targetSize)\n\treturn Helper.similarStrength(pokepool, poke, targetSize, balancedBST)";
        String findDef = "def find(pokepool, num):\n\treturn Helper.find(pokepool, num)";
        String seqDef = "def seq(list):\n\treturn Helper.seq(list)";
        String hasTypeDef = "def hasType(poke, type):\n\treturn Helper.hasType(poke, type)";
        String indexTypeDef = "class Index:\n\tABILITY = Helper.Index.ABILITY\n\tITEM = Helper.Index.ITEM\n\tMOVE = Helper.Index.MOVE\n\tPOKEMON = Helper.Index.POKEMON";
        String newLn = "\n\n";
        return newLn + helperImport + newLn + toStrDef + newLn + indexDef + newLn + simStrengthDef + newLn + findDef + newLn + seqDef + newLn + hasTypeDef + newLn + indexTypeDef + newLn;
    }

    public static void initJythonDoc(JythonSyntaxDocument jdoc)
    {
        JythonScope dummy = jdoc.getGlobalScope();

        jdoc.addExtraGlobalFunc(dummy.new Function("toStr", -1));
        jdoc.addExtraGlobalFunc(dummy.new Function("index", -1));
        jdoc.addExtraGlobalFunc(dummy.new Function("similarStrength", -1));
        jdoc.addExtraGlobalFunc(dummy.new Function("find", -1));
        jdoc.addExtraGlobalFunc(dummy.new Function("seq", -1));
        jdoc.addExtraGlobalFunc(dummy.new Function("hasType", -1));

        JythonScope.Class indexCls = dummy.new Class("Index", -1);
        indexCls.members.add(dummy.new Variable("ABILITY", 1));
        indexCls.members.add(dummy.new Variable("ITEM", 1));
        indexCls.members.add(dummy.new Variable("MOVE", 1));
        indexCls.members.add(dummy.new Variable("POKEMON", 1));
        jdoc.addExtraGlobalClass(indexCls);
    }

}
