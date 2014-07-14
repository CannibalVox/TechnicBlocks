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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.technic.technicblocks.parser.data.CreativeTabData;

public class DataDrivenCreativeTab extends CreativeTabs {

    private CreativeTabData data;

    public DataDrivenCreativeTab(CreativeTabData data) {
        super(data.getDisplayName());
        this.data = data;
    }

    @Override
    public Item getTabIconItem() {
        String fullName = data.getItemName();
        String blockName = fullName;

        if (fullName.indexOf(':') != fullName.lastIndexOf(':')) {
            blockName = fullName.substring(0, fullName.lastIndexOf(':'));
        }

       return (Item)Item.itemRegistry.getObject(blockName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int func_151243_f() {
        String fullName = data.getItemName();
        int damage = 0;

        if (fullName.indexOf(':') != fullName.lastIndexOf(':')) {
            try {
                damage = Integer.parseInt(fullName.substring(fullName.lastIndexOf(':')+1));
            } catch (NumberFormatException ex) {
                //That was kind of disastrous, let's stick with meta 0 for now
            }
        }

        return damage;
    }
}
