package com.dabomstew.pkrandom.Script;

import com.dabomstew.pkrandom.constants.Abilities;
import com.dabomstew.pkrandom.constants.Items;
import com.dabomstew.pkrandom.constants.Moves;
import com.dabomstew.pkrandom.constants.Species;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import org.python.core.*;
import sun.font.Script;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static boolean HasType(Pokemon poke, Type theType)
    {
        return poke.primaryType == theType || poke.secondaryType == theType;
    }

    public static PyString Str(Enum val)
    {
        return new PyString(val.name());
    }

    public enum Index{
        ABILITY, ITEM, MOVE, POKEMON
    }
    public static PyString Str(int val, Index valType)throws IllegalAccessException, Exception
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
                fields = Moves.class.getFields();
                break;
            case POKEMON:
                fields = Species.class.getFields();
                break;
            default:
                throw new Exception("Unsupported Index type given to Helper.Str(int val, Helper.Index valType)!");
        }

        for(Field current : fields)
        {
            int currentVal = (int)current.get(null);
            if(currentVal == val)
            {
                return new PyString(current.getName());
            }
        }

        throw new Exception("Name of "+valType.name()+" not found!");
    }

    public static PyObject Find(PySequence seq, int number)
    {
        for(int k = 0; k < seq.__len__(); k++)
        {
            if(Py.tojava(seq.__getitem__(k), Pokemon.class).number == number)
            {
                return seq.__getitem__(k);
            }
        }
        return null;
    }

    public static PySequence SimilarStrength(PySequence pyPokepool, Pokemon poke)
    {
        return SimilarStrength(pyPokepool, poke, 3);
    }

    public static PySequence SimilarStrength(PySequence pyPokepool, Pokemon poke, int targetSize)
    {
        List<Pokemon> pokepool = ScriptInstance.toJavaList(pyPokepool, pkmn -> Py.tojava(pkmn, Pokemon.class));

        // start with within 10% and add 5% either direction till we find
        // something
        int currentBST = poke.bstForPowerLevels();
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

}
