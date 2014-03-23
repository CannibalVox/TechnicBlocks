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
import net.minecraft.world.World;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.behavior.functions.IBlockDropBehavior;

import java.util.ArrayList;

public class ReplaceDropsBehavior extends BlockBehavior implements IBlockDropBehavior {

    ArrayList<String> itemNames = new ArrayList<String>();
    ArrayList<Integer> itemCounts = new ArrayList<Integer>();
    ArrayList<Integer> itemDamage = new ArrayList<Integer>();

    public ReplaceDropsBehavior(String[] args) {
        super(args);

        if (args.length == 0)
            throw TechnicBlocks.getProxy().createParseException("Behavior 'replaceDrops' requires a list of drops to replace in its arguments.");
        else if (args.length % 3 != 0)
            throw TechnicBlocks.getProxy().createParseException("The number of arguments for behavior 'replaceDrops' should be in groups of three, because it should alternate between item names, counts, and damage keys.");

        for (int i = 0; i < args.length; i += 3) {
            itemNames.add(args[i]);
            String itemCount = args[i+1];
            String itemMeta = args[i+2];

            try {
                itemCounts.add(Integer.parseInt(itemCount));
            } catch (NumberFormatException ex) {
                throw TechnicBlocks.getProxy().createParseException("Argument "+(i+2)+" of behavior 'replaceDrops' should have been a stack size, but couldn't parse as a number.");
            }

            if (itemMeta == null || itemMeta.equalsIgnoreCase("meta"))
                itemDamage.add(null);
            else {
                try {
                    itemDamage.add(Integer.parseInt(itemMeta));
                } catch (NumberFormatException ex) {
                    throw TechnicBlocks.getProxy().createParseException("Argument "+(i+3)+" of behavior 'replaceDrops' should have been a damage value or 'meta' but couldn't parse as either.");
                }
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
    public boolean doesDropDefault(World world, int x, int y, int z, int metadata, int fortune) {
        return false;
    }

    @Override
    public void addExtraDrops(ArrayList<ItemStack> drops, World world, int x, int y, int z, int metadata, int fortune) {
        for (int i = 0; i < itemNames.size(); i++) {
            Item parsedItem = (Item)Item.itemRegistry.getObject(itemNames.get(i));

            if (parsedItem != null) {
                int damage = metadata;
                if (itemDamage.get(i) != null)
                    damage = itemDamage.get(i);

                drops.add(new ItemStack(parsedItem, itemCounts.get(i), damage));
            }
        }
    }
}
