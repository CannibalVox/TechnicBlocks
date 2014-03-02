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

package net.technic.technicblocks.blocks.connections;

import net.technic.technicblocks.parser.ParseException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionConventionFactory {
    Map<String, Class<? extends ConnectionConvention>> connectionConventions = new HashMap<String, Class<? extends ConnectionConvention>>();

    public void addConvention(String name, Class<? extends ConnectionConvention> conventionClass) {
        connectionConventions.put(name, conventionClass);
    }

    public ConnectionConvention createConvention(String name, String[] args) throws ParseException {
        if (!connectionConventions.containsKey(name)) {
            throw new ParseException("No connection convention named '"+name+"' exists.");
        }

        Class<? extends ConnectionConvention> clazz = connectionConventions.get(name);
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
