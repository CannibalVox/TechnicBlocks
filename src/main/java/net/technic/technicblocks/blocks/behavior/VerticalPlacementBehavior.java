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
import net.technic.technicblocks.blocks.behavior.functions.IBlockPlacementBehavior;

public class VerticalPlacementBehavior extends BlockBehavior implements IBlockPlacementBehavior {

    public VerticalPlacementBehavior(String[] args) {
        super(args);
    }

    @Override
    public boolean isMetadataReserved() {
        return true;
    }

    @Override
    public int getMetadataBitSize() {
        return 1;
    }

    @Override
    public ForgeDirection transformBlockFacing(int metadata, ForgeDirection currentFacing) {
        if (getMetadataValue(metadata) != 0 && (currentFacing == ForgeDirection.DOWN || currentFacing == ForgeDirection.UP)) {
            currentFacing = currentFacing.getRotation(ForgeDirection.NORTH).getRotation(ForgeDirection.NORTH);
        }

        return currentFacing;
    }

    @Override
    public ForgeDirection reverseTransformBlockFacing(int metadata, ForgeDirection currentFacing) {
        return transformBlockFacing(metadata, currentFacing);
    }

    @Override
    public int transformPlacementMetadata(DataDrivenBlock block, World world, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int metadata) {
        if (side == ForgeDirection.UP)
            return metadata;

        if (side == ForgeDirection.DOWN || hitY > 0.5D)
            return setMetadataValue(metadata, 1);

        return metadata;
    }

    @Override
    public void triggerBlockPlacement(DataDrivenBlock block, World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {

    }
}
