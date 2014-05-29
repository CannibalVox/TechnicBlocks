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
        block.setBlockBoundsBasedOnState(world, x, y, z);
        ForgeDirection up = block.reverseTransformBlockFacing(world.getBlockMetadata(x, y, z), ForgeDirection.UP);

        float minX = (float)block.getBlockBoundsMinX();
        float minY = (float)block.getBlockBoundsMinY();
        float minZ = (float)block.getBlockBoundsMinZ();
        float maxX = (float)block.getBlockBoundsMaxX();
        float maxY = (float)block.getBlockBoundsMaxY();
        float maxZ = (float)block.getBlockBoundsMaxZ();

        float modX = up.offsetX * 0.5f;
        float modY = up.offsetY * 0.5f;
        float modZ = up.offsetZ * 0.5f;

        if (modX < 0)
            minX += modX;
        else
            maxX += modX;

        if (modY < 0)
            minY += modY;
        else
            maxY += modY;

        if (modZ < 0)
            minZ += modZ;
        else
            maxZ += modZ;

        block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        addCollisionBox(block, mask, world, x, y, z, list);
    }

    private void addCollisionBox(DataDrivenBlock block, AxisAlignedBB mask, World world, int x, int y, int z, List list) {
        AxisAlignedBB axisalignedbb1 = block.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1)) {
            list.add(axisalignedbb1);
        }
    }
}
