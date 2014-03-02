/*
        This mod is Technic Blocks, a basic mod to allow new connected texture
        and cosmetic blocks to be created from data.
        Copyright (C) 2014, Stephen Baynham

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.technic.technicblocks.blocks.behavior;

import net.technic.technicblocks.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BlockBehaviorFactory {
    private Map<String, Class<BlockBehavior>> behaviors = new HashMap<String, Class<BlockBehavior>>();

    public void addBehavior(String name, Class<BlockBehavior> behavior) {
        behaviors.put(name, behavior);
    }

    public BlockBehavior createBehavior(String name, String[] args) throws ParseException {
        if (!behaviors.containsKey(name)) {
            throw new ParseException("Block behavior '"+name+"' does not exist.");
        }

        Class<BlockBehavior> clazz = behaviors.get(name);
        try {
            return (clazz.getConstructor(String[].class).newInstance(new Object[] {args}));
        } catch (NoSuchMethodException ex) {
            throw new ParseException("Class '"+clazz.getName()+"' was expected to have a constructor with a single String[] param, but did not.", ex);
        } catch (InstantiationException ex) {
            throw new ParseException("An error occurred while instantiating an object of class '"+clazz.getName()+"'", ex);
        } catch (IllegalAccessException ex) {
            throw new ParseException("An error occurred while instantiating an object of class '"+clazz.getName()+"'", ex);
        } catch (InvocationTargetException ex) {
            throw new ParseException("An error occurred while instantiating an object of class '"+clazz.getName()+"'", ex);
        }
    }
}
