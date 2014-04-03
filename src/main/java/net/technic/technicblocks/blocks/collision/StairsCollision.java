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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.client.renderer.context.WorldRenderContext;

import java.util.List;

public class StairsCollision extends BlockCollision {
    private static final int[][] traceValues = new int[][] {{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};

    private SelectionVolumeCollision selectionCollision;

    public StairsCollision(SelectionVolumeCollision selectionCollision) {
        this.selectionCollision = selectionCollision;
    }

    @Override
    public AxisAlignedBB getCentralCollisionBox(DataDrivenBlock block, World world, int x, int y, int z) {
        return selectionCollision.getCentralCollisionBox(block, world, x, y, z);
    }

    private int getRotationFrom(ForgeDirection side) {
        switch (side) {
            case EAST:
                return 0;
            case NORTH:
                return 1;
            case WEST:
                return 2;
            default:
                return 3;
        }
    }

    @Override
    public MovingObjectPosition traceCollision(DataDrivenBlock block, World world, int x, int y, int z, Vec3 start, Vec3 end) {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        int metadata = world.getBlockMetadata(x, y,z);
        int rotation = getRotationFrom(block.transformBlockFacing(metadata, ForgeDirection.NORTH));
        boolean isOnCeiling = !block.isOnFloor(metadata);
        int[] aint = traceValues[rotation + (isOnCeiling?4:0)];

        for (int j1 = 0; j1 < 8; ++j1)
        {
            block.setBlockBounds(0.5F * (float)(j1 % 2), 0.5F * (float)(j1 / 2 % 2), 0.5F * (float)(j1 / 4 % 2), 0.5F + 0.5F * (float)(j1 % 2), 0.5F + 0.5F * (float)(j1 / 2 % 2), 0.5F + 0.5F * (float)(j1 / 4 % 2));
            amovingobjectposition[j1] = selectionCollision.traceCollisionForBounds(block, world, x, y, z, start, end);
        }

        int[] aint2 = aint;
        int k2 = aint.length;

        for (int k1 = 0; k1 < k2; ++k1)
        {
            int l1 = aint2[k1];
            amovingobjectposition[l1] = null;
        }

        MovingObjectPosition movingobjectposition1 = null;
        double d1 = 0.0D;
        MovingObjectPosition[] amovingobjectposition1 = amovingobjectposition;

        for (int j2 = 0; j2 < amovingobjectposition.length; ++j2)
        {
            MovingObjectPosition movingobjectposition = amovingobjectposition1[j2];

            if (movingobjectposition != null)
            {
                double d0 = movingobjectposition.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    movingobjectposition1 = movingobjectposition;
                    d1 = d0;
                }
            }
        }

        return movingobjectposition1;
    }

    private ForgeDirection getConnectionFacing(ItemStack connection, boolean isOnFloor) {
        Item item = connection.getItem();
        int metadata = connection.getItemDamage();

        if (!(item instanceof ItemBlock))
            return ForgeDirection.UNKNOWN;

        ItemBlock itemBlock = (ItemBlock)item;
        Block block = itemBlock.field_150939_a;

        if (block instanceof DataDrivenBlock) {
            if (isOnFloor != ((DataDrivenBlock)block).isOnFloor(metadata))
                return ForgeDirection.UNKNOWN;

            return ((DataDrivenBlock) block).transformBlockFacing(metadata, ForgeDirection.NORTH);
        } else {
            //Assume it's a regular staircase?
            if (isOnFloor != ((metadata & 4) == 0))
                return ForgeDirection.UNKNOWN;

            int dir = metadata & 3;

            switch (dir) {
                case 0:
                    return ForgeDirection.NORTH;
                case 1:
                    return ForgeDirection.SOUTH;
                case 2:
                    return ForgeDirection.EAST;
                default:
                    return ForgeDirection.WEST;
            }
        }
    }

    @Override
    public void collectCollisionBoxes(DataDrivenBlock block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        int metadata = world.getBlockMetadata(x,y,z);
        boolean isOnCeiling = !block.isOnFloor(metadata);

        WorldRenderContext context = new WorldRenderContext(block, block.getSubBlock(metadata), world, x, y, z);

        ForgeDirection front = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection back = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[front.ordinal()]];

        ForgeDirection leftSide = front.getRotation(ForgeDirection.UP);
        ForgeDirection rightSide = front.getRotation(ForgeDirection.DOWN);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        //Basic slab
        if (isOnCeiling)
        {
            block.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        selectionCollision.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);

        ItemStack northConnection = context.getConnectedBlock(back);
        if (northConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(northConnection, !isOnCeiling);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                setQuarterTopBounds(block, front, connectionFacing, isOnCeiling);
                selectionCollision.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
                block.getBlockModel().setBlockBounds(block, world, x, y, z);
                return;
            }
        }

        setHalfTopBounds(block, front, isOnCeiling);
        selectionCollision.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);

        ItemStack southConnection = context.getConnectedBlock(front);
        if (southConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(southConnection, !isOnCeiling);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                setQuarterTopBounds(block, back, connectionFacing, isOnCeiling);
                selectionCollision.collectCollisionBoxes(block, world, x, y, z, mask, list, entity);
            }
        }

        block.getBlockModel().setBlockBounds(block, world, x, y, z);
    }

    private void setQuarterTopBounds(DataDrivenBlock block, ForgeDirection stairDirection, ForgeDirection connectionDirection, boolean isOnCeiling) {
        setHalfTopBounds(block, stairDirection, isOnCeiling);

        double startX = block.getBlockBoundsMinX();
        double startY = block.getBlockBoundsMinY();
        double startZ = block.getBlockBoundsMinZ();
        double endX = block.getBlockBoundsMaxX();
        double endY = block.getBlockBoundsMaxY();
        double endZ = block.getBlockBoundsMaxZ();

        switch(connectionDirection) {
            case NORTH:
                endZ -= 0.5f;
                break;
            case SOUTH:
                startZ += 0.5f;
                break;
            case EAST:
                startX += 0.5f;
                break;
            case WEST:
                endX -= 0.5f;
                break;
        }

        block.setBlockBounds((float)startX, (float)startY, (float)startZ, (float)endX, (float)endY, (float)endZ);
    }

    private void setHalfTopBounds(DataDrivenBlock block, ForgeDirection front, boolean isOnCeiling) {
        float startX = 0;
        float startY = 0;
        float startZ = 0;
        float endX = 1;
        float endY = 1;
        float endZ = 1;

        if (isOnCeiling)
            endY -= 0.5f;
        else
            startY += 0.5f;

        switch (front) {
            case SOUTH:
                endZ -= 0.5f;
                break;
            case NORTH:
                startZ += 0.5f;
                break;
            case WEST:
                startX += 0.5f;
                break;
            case EAST:
                endX -= 0.5f;
                break;
        }

        block.setBlockBounds(startX, startY, startZ, endX, endY, endZ);
    }
}
