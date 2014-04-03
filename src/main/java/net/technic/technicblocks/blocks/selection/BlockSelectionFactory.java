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

package net.technic.technicblocks.blocks.selection;

import net.technic.technicblocks.TechnicBlocks;

import java.util.HashMap;
import java.util.Map;

public class BlockSelectionFactory {
    private Map<String, BlockSelection> selectionMap = new HashMap<String, BlockSelection>();

    public BlockSelectionFactory() {

    }

    public void addSelection(String key, BlockSelection collision) {
        selectionMap.put(key, collision);
    }

    public BlockSelection getSelection(String selectionKey) {
        if (!selectionMap.containsKey(selectionKey))
            throw TechnicBlocks.getProxy().createParseException("Selection type '"+selectionKey+"' does not exist.");

        return selectionMap.get(selectionKey);
    }
}
