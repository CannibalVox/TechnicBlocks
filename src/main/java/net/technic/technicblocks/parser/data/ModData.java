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

package net.technic.technicblocks.parser.data;

import java.util.ArrayList;
import java.util.Collection;

public class ModData {

    private String modId;
    private Collection<CreativeTabData> customCreativeTabs = new ArrayList<CreativeTabData>();
    private Collection<MaterialData> customMaterials = new ArrayList<MaterialData>();
    private Collection<BlockData> blocks = new ArrayList<BlockData>();

    public Collection<CreativeTabData> getCreativeTabs() {
        return customCreativeTabs;
    }

    public Collection<MaterialData> getCustomMaterials() {
        return customMaterials;
    }

    public Collection<BlockData> getBlocks() {
        return blocks;
    }

    public String getModId() { return modId; }
}
