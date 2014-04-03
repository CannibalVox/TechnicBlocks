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
import net.technic.technicblocks.blocks.DataDrivenBlock;

import java.util.List;

public class SelectionVolumeCollision extends BlockCollision {

    public SelectionVolumeCollision() {

    }

    @Override
    public AxisAlignedBB getCentralCollisionBox(DataDrivenBlock block, World world, int x, int y, int z) {
        return AxisAlignedBB.getAABBPool().getAABB((double)x + block.getBlockBoundsMinX(), (double)y + block.getBlockBoundsMinY(), (double)z + block.getBlockBoundsMinZ(), (double)x + block.getBlockBoundsMaxX(), (double)y + block.getBlockBoundsMaxY(), (double)z + block.getBlockBoundsMaxZ());
    }

    @Override
    public MovingObjectPosition traceCollision(DataDrivenBlock block, World world, int x, int y, int z, Vec3 start, Vec3 end) {
        block.setBlockBoundsBasedOnState(world, x, y, z);
        start = start.addVector((double)(-x), (double)(-y), (double)(-z));
        end = end.addVector((double)(-x), (double)(-y), (double)(-z));
        Vec3 vec32 = start.getIntermediateWithXValue(end, block.getBlockBoundsMinX());
        Vec3 vec33 = start.getIntermediateWithXValue(end, block.getBlockBoundsMaxX());
        Vec3 vec34 = start.getIntermediateWithYValue(end, block.getBlockBoundsMinY());
        Vec3 vec35 = start.getIntermediateWithYValue(end, block.getBlockBoundsMaxY());
        Vec3 vec36 = start.getIntermediateWithZValue(end, block.getBlockBoundsMinZ());
        Vec3 vec37 = start.getIntermediateWithZValue(end, block.getBlockBoundsMaxZ());

        if (!block.isVecYZContained(vec32))
        {
            vec32 = null;
        }

        if (!block.isVecYZContained(vec33))
        {
            vec33 = null;
        }

        if (!block.isVecXZContained(vec34))
        {
            vec34 = null;
        }

        if (!block.isVecXZContained(vec35))
        {
            vec35 = null;
        }

        if (!block.isVecXYContained(vec36))
        {
            vec36 = null;
        }

        if (!block.isVecXYContained(vec37))
        {
            vec37 = null;
        }

        Vec3 vec38 = null;

        if (vec32 != null && (vec38 == null || start.squareDistanceTo(vec32) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec32;
        }

        if (vec33 != null && (vec38 == null || start.squareDistanceTo(vec33) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec33;
        }

        if (vec34 != null && (vec38 == null || start.squareDistanceTo(vec34) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec34;
        }

        if (vec35 != null && (vec38 == null || start.squareDistanceTo(vec35) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec35;
        }

        if (vec36 != null && (vec38 == null || start.squareDistanceTo(vec36) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec36;
        }

        if (vec37 != null && (vec38 == null || start.squareDistanceTo(vec37) < start.squareDistanceTo(vec38)))
        {
            vec38 = vec37;
        }

        if (vec38 == null)
        {
            return null;
        }
        else
        {
            byte b0 = -1;

            if (vec38 == vec32)
            {
                b0 = 4;
            }

            if (vec38 == vec33)
            {
                b0 = 5;
            }

            if (vec38 == vec34)
            {
                b0 = 0;
            }

            if (vec38 == vec35)
            {
                b0 = 1;
            }

            if (vec38 == vec36)
            {
                b0 = 2;
            }

            if (vec38 == vec37)
            {
                b0 = 3;
            }

            return new MovingObjectPosition(x, y, z, b0, vec38.addVector((double)x, (double)y, (double)z));
        }
    }

    @Override
    public void collectCollisionBoxes(DataDrivenBlock block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        AxisAlignedBB axisalignedbb1 = block.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1))
        {
            list.add(axisalignedbb1);
        }
    }
}
