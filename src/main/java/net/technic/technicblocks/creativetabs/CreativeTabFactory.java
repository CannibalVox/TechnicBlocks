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

package net.technic.technicblocks.creativetabs;

import net.minecraft.creativetab.CreativeTabs;
import net.technic.technicblocks.parser.ParseException;
import net.technic.technicblocks.parser.data.CreativeTabData;

import java.util.HashMap;

public class CreativeTabFactory {
    private HashMap<String, CreativeTabs> creativeTabs = new HashMap<String, CreativeTabs>();

    public CreativeTabFactory() {
        creativeTabs.put("blocks", CreativeTabs.tabBlock);
        creativeTabs.put("decorations", CreativeTabs.tabDecorations);
        creativeTabs.put("redstone", CreativeTabs.tabRedstone);
        creativeTabs.put("transportation", CreativeTabs.tabTransport);
        creativeTabs.put("misc", CreativeTabs.tabMisc);
        creativeTabs.put("food", CreativeTabs.tabFood);
        creativeTabs.put("tools", CreativeTabs.tabTools);
        creativeTabs.put("combat", CreativeTabs.tabCombat);
        creativeTabs.put("brewing", CreativeTabs.tabBrewing);
        creativeTabs.put("materials", CreativeTabs.tabMaterials);
    }

    public void addCreativeTab(CreativeTabData data) {
        creativeTabs.put(data.getName(), new DataDrivenCreativeTab(data));
    }

    public CreativeTabs getCreativeTabByName(String name) {
        if (creativeTabs.containsKey(name))
            return creativeTabs.get(name);
        else
            return null;
    }

    public void verifyCreativeTabs() throws ParseException {
        for(CreativeTabs tab : creativeTabs.values()) {
            if (tab.getTabIconItem() == null) {
                throw new ParseException("Could not locate the specified icon item for creative tab "+tab.getTranslatedTabLabel());
            }
        }
    }
}
