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

package net.technic.technicblocks.client.facevisibility;

import net.technic.technicblocks.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class FaceVisibilityFactory {
    private Map<String, FaceVisibilityConvention> conventions = new HashMap<String, FaceVisibilityConvention>();

    public void addConvention(String name, FaceVisibilityConvention convention) {
        conventions.put(name, convention);
    }

    public FaceVisibilityConvention getConvention(String name) throws ParseException {
        if (!conventions.containsKey(name))
            throw new ParseException("Face Visibility type '"+name+"' does not exist.");

        return conventions.get(name);
    }
}
