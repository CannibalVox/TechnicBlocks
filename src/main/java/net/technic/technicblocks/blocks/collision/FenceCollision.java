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

package net.technic.technicblocks.blocks.collision;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.client.renderer.context.WorldRenderContext;

import java.util.List;

public class FenceCollision extends SelectionVolumeCollision {
    @Override
    public void collectCollisionBoxes(DataDrivenBlock block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {

        int metadata = world.getBlockMetadata(x, y, z);
        WorldRenderContext context = new WorldRenderContext(block, block.getSubBlock(metadata), world, x, y, z);

        block.setBlockBounds(0.375f, 0, 0.375f, 0.625f, 1.5f, 0.625f);
        super.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);

        if (context.isModelConnected(ForgeDirection.NORTH)) {
            block.setBlockBounds(0.375f, 0, 0, 0.625f, 1.5f, 0.375f);
            super.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
        }

        if (context.isModelConnected(ForgeDirection.SOUTH)) {
            block.setBlockBounds(0.375f, 0, 0.625f, 0.625f, 1.5f, 1.0f);
            super.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
        }

        if (context.isModelConnected(ForgeDirection.EAST)) {
            block.setBlockBounds(0.625f, 0, 0.375f, 1.0f, 1.5f, 0.625f);
            super.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
        }

        if (context.isModelConnected(ForgeDirection.WEST)) {
            block.setBlockBounds(0, 0, 0.375f, 0.375f, 1.5f, 0.625f);
            super.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
        }

        block.setBlockBoundsBasedOnState(world, x, y, z);
    }
}
