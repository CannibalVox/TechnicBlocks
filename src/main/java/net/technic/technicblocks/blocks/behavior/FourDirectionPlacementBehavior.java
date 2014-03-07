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
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class FourDirectionPlacementBehavior extends BlockBehavior implements  IBlockPlacementBehavior {

    public FourDirectionPlacementBehavior(String[] args) {
        super(args);
    }

    @Override
    public boolean isMetadataReserved() {
        return true;
    }

    @Override
    public int getMetadataBitSize() {
        return 2;
    }

    @Override
    public ForgeDirection transformBlockFacing(int metadata, ForgeDirection currentFacing) {
        int value = getMetadataValue(metadata);

        while (value > 0) {
            currentFacing = currentFacing.getRotation(ForgeDirection.UP);
            value--;
        }

        return currentFacing;
    }

    @Override
    public int transformPlacementMetadata(IBlockAccess world, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int metadata) {
        return metadata;
    }

    @Override
    public void triggerBlockPlacement(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        int quartile = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int metadata = world.getBlockMetadata(x, y, z);

        switch (quartile) {
            case 0:
                metadata = setMetadataValue(metadata, 0);
                break;
            case 1:
                metadata = setMetadataValue(metadata, 3);
                break;
            case 2:
                metadata = setMetadataValue(metadata, 2);
                break;
            default:
                metadata = setMetadataValue(metadata, 1);
        }

        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }
}
