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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.behavior.functions.ICreativePickerBehavior;

public class ReplacePickerBehavior extends BlockBehavior implements ICreativePickerBehavior {
    public String itemName;
    public Integer itemDamage;

    public ReplacePickerBehavior(String[] args) {
        super(args);

        if (args.length != 2) {
            throw TechnicBlocks.getProxy().createParseException("Behavior 'replacePicker' should have two args: an item name, and a meta value (or the word 'meta'.");
        }

        itemName = args[0];

        if (args[1] == null || args[1].equalsIgnoreCase("meta"))
            itemDamage = null;
        else {
            try {
                itemDamage = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                throw TechnicBlocks.getProxy().createParseException("Arg 2 of behavior 'replacePicker' should be the word 'meta' or a numerical value for a specified meta.  It was unable to be parsed as either.", ex);
            }
        }
    }

    @Override
    public boolean isMetadataReserved() {
        return false;
    }

    @Override
    public int getMetadataBitSize() {
        return 0;
    }

    @Override
    public ItemStack transformPickResult(MovingObjectPosition target, World world, int x, int y, int z, ItemStack pickResult) {
        Item item = (Item)Item.itemRegistry.getObject(itemName);

        if (item == null)
            return pickResult;

        int damage = pickResult.getItemDamage();

        if (itemDamage != null)
            damage = itemDamage;

        return new ItemStack(item, 1, damage);
    }
}
