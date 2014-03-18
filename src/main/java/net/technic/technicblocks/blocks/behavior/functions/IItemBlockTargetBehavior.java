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

package net.technic.technicblocks.blocks.behavior.functions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.items.DataDrivenItemBlock;

public interface IItemBlockTargetBehavior {
    boolean transformShouldPlaceBlock(EntityPlayer player, DataDrivenBlock block, DataDrivenItemBlock item, ItemStack stack, World world, int x, int y, int z, ForgeDirection face, float hitX, float hitY, float hitZ, boolean shouldPlaceBlock);
    boolean itemUsedOnBlock(EntityPlayer player, DataDrivenBlock block, DataDrivenItemBlock item, ItemStack stack, World world, int x, int y, int z, ForgeDirection face, float hitX, float hitY, float hitZ);
}
