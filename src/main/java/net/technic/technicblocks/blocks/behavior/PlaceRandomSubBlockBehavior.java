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

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;

public class PlaceRandomSubBlockBehavior extends BlockBehavior implements IBlockPlacementBehavior {

    public PlaceRandomSubBlockBehavior(String[] args) {
        super(args);
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
    public int transformPlacementMetadata(DataDrivenBlock block, World world, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int metadata) {
        int subBlockIndex = world.rand.nextInt(block.getSubBlockCount());
        int subBlockMetadata = block.getSubBlockMetadataByIndex(subBlockIndex);
        return setMetadataValueWithMask(metadata, subBlockMetadata, block.getSubBlockMask());
    }

    @Override
    public void triggerBlockPlacement(DataDrivenBlock block, World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        //Do nothin'!
    }
}
