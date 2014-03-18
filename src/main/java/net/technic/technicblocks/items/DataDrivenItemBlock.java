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

package net.technic.technicblocks.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;

public class DataDrivenItemBlock extends ItemBlock {
    private DataDrivenBlock ddBlock;

    public DataDrivenItemBlock(Block block) {
        super(block);

        if (block instanceof DataDrivenBlock) {
            ddBlock = (DataDrivenBlock)block;
        }
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        DataDrivenSubBlock block = ddBlock.getSubBlock(this.getMetadata(par1ItemStack.getItemDamage()));

        return block.getUnlocalizedName();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (itemStack.stackSize == 0)
        {
            return false;
        }
        else if (!entityPlayer.canPlayerEdit(x, y, z, side, itemStack))
        {
            return false;
        }
        else if (y == 255 && this.field_150939_a.getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            if (block instanceof DataDrivenBlock) {
                DataDrivenBlock dataDrivenBlock = (DataDrivenBlock)block;
                if (!dataDrivenBlock.shouldPlaceBlock(entityPlayer, this, itemStack, world, x, y, z, side, hitX, hitY, hitZ)) {
                    if (dataDrivenBlock.itemUsedOnBlock(entityPlayer, this, itemStack, world, x, y, z, side, hitX, hitY, hitZ)) {
                        world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                        --itemStack.stackSize;
                    }

                    return true;
                }
            }

            if (world.canPlaceEntityOnSide(this.field_150939_a, x, y, z, false, side, entityPlayer, itemStack)) {
                int i1 = this.getMetadata(itemStack.getItemDamage());
                int j1 = this.field_150939_a.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, i1);

                if (placeBlockAt(itemStack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ, j1))
                {
                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), this.field_150939_a.stepSound.func_150496_b(), (this.field_150939_a.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a.stepSound.getPitch() * 0.8F);
                    --itemStack.stackSize;
                }

                return true;
            }

            return false;
        }
    }
}
