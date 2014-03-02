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

package net.technic.technicblocks.client.renderer;

import net.technic.technicblocks.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class RendererFactory {
    private Map<String, DataDrivenRenderer> renderers = new HashMap<String, DataDrivenRenderer>();

    public void addRenderer(String name, DataDrivenRenderer renderer) {
        renderers.put(name, renderer);
    }

    public DataDrivenRenderer getRenderer(String name) throws ParseException {
        if (!renderers.containsKey(name)) {
            throw new ParseException("There is no block model named '"+name+"'");
        }

        return renderers.get(name);
    }
}
