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
    private SelectionVolumeCollision selectionCollision;

    public StairsCollision(SelectionVolumeCollision selectionCollision) {
        this.selectionCollision = selectionCollision;
    }

    @Override
    public MovingObjectPosition traceCollision(DataDrivenBlock block, World world, int x, int y, int z, Vec3 start, Vec3 end) {
        MovingObjectPosition[] amovingobjectposition = new MovingObjectPosition[8];
        int metadata = world.getBlockMetadata(x, y,z);
        ForgeDirection bottom = block.reverseTransformBlockFacing(metadata, ForgeDirection.DOWN);
        ForgeDirection front = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        boolean[] validOctals = { false, false, false, false, false, false, false, false };

        for (int j1 = 0; j1 < 8; ++j1)
        {
            block.setBlockBounds(0.5F * (float)(j1 % 2), 0.5F * (float)(j1 / 2 % 2), 0.5F * (float)(j1 / 4 % 2), 0.5F + 0.5F * (float)(j1 % 2), 0.5F + 0.5F * (float)(j1 / 2 % 2), 0.5F + 0.5F * (float)(j1 / 4 % 2));
            amovingobjectposition[j1] = selectionCollision.traceCollisionForBounds(block, world, x, y, z, start, end);
        }

        validateOctals(validOctals, bottom);
        validateOctals(validOctals, front);

        MovingObjectPosition bestPosition = null;
        double bestDistance = 0.0D;

        for (int j2 = 0; j2 < amovingobjectposition.length; ++j2)
        {
            MovingObjectPosition movingobjectposition = amovingobjectposition[j2];

            if (validOctals[j2] && movingobjectposition != null)
            {
                double distance = movingobjectposition.hitVec.squareDistanceTo(end);

                if (distance > bestDistance)
                {
                    bestPosition = movingobjectposition;
                    bestDistance = distance;
                }
            }
        }

        return bestPosition;
    }

    private void validateOctals(boolean[] validOctals, ForgeDirection direction) {
        switch(direction) {
            case UP:
                validOctals[2] = true;
                validOctals[3] = true;
                validOctals[6] = true;
                validOctals[7] = true;
                break;
            case DOWN:
                validOctals[0] = true;
                validOctals[1] = true;
                validOctals[4] = true;
                validOctals[5] = true;
                break;
            case SOUTH:
                validOctals[0] = true;
                validOctals[1] = true;
                validOctals[2] = true;
                validOctals[3] = true;
                break;
            case NORTH:
                validOctals[4] = true;
                validOctals[5] = true;
                validOctals[6] = true;
                validOctals[7] = true;
                break;
            case EAST:
                validOctals[0] = true;
                validOctals[2] = true;
                validOctals[4] = true;
                validOctals[6] = true;
                break;
            case WEST:
                validOctals[1] = true;
                validOctals[3] = true;
                validOctals[5] = true;
                validOctals[7] = true;
                break;
        }
    }

    private ForgeDirection getConnectionFacing(ItemStack connection, ForgeDirection bottom) {
        Item item = connection.getItem();
        int metadata = connection.getItemDamage();

        if (!(item instanceof ItemBlock))
            return ForgeDirection.UNKNOWN;

        ItemBlock itemBlock = (ItemBlock)item;
        Block block = itemBlock.field_150939_a;

        if (block instanceof DataDrivenBlock) {
            if (bottom != ((DataDrivenBlock)block).reverseTransformBlockFacing(metadata, ForgeDirection.DOWN))
                return ForgeDirection.UNKNOWN;

            return ((DataDrivenBlock) block).reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        } else if (bottom == ForgeDirection.DOWN || bottom == ForgeDirection.UP) {
            //Assume it's a regular staircase?
            if ((bottom == ForgeDirection.DOWN) != ((metadata & 4) == 0))
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
        } else
            return ForgeDirection.UNKNOWN;
    }

    @Override
    public void collectCollisionBoxes(DataDrivenBlock block, World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        int metadata = world.getBlockMetadata(x,y,z);
        ForgeDirection bottom = block.reverseTransformBlockFacing(metadata, ForgeDirection.DOWN);
        ForgeDirection top = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[bottom.ordinal()]];

        WorldRenderContext context = new WorldRenderContext(block, block.getSubBlock(metadata), world, x, y, z);

        ForgeDirection front = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection back = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[front.ordinal()]];

        ForgeDirection leftSide = front.getRotation(ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[bottom.ordinal()]]);
        ForgeDirection rightSide = front.getRotation(bottom);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        //Basic slab
        setBasicSlabFromDir(bottom, block);
        addCollisionBox(block, mask, world, x, y, z, list);

        //Top quarter slab
        ItemStack northConnection = context.getConnectedBlock(back);
        if (northConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(northConnection, bottom);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                setQuarterTopBounds(block, back, ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[connectionFacing.ordinal()]], top);
                addCollisionBox(block, mask, world, x, y, z, list);

                block.getBlockModel().setBlockBounds(block, world, x, y, z);
                return;
            }
        }

        //Top half slab
        setHalfTopBounds(block, back, top);
        addCollisionBox(block, mask, world, x, y, z, list);

        //Turn half slab into 3-quarter slab
        ItemStack southConnection = context.getConnectedBlock(front);
        if (southConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(southConnection, bottom);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                setQuarterTopBounds(block, back, connectionFacing, top);
                addCollisionBox(block, mask, world, x, y, z, list);
            }
        }

        block.getBlockModel().setBlockBounds(block, world, x, y, z);
    }

    private void setBasicSlabFromDir(ForgeDirection bottomSide, DataDrivenBlock block) {
        block.setBlockBounds(0, 0, 0, 1, 1, 1);
        trimDirection(block, bottomSide);
    }

    private void setQuarterTopBounds(DataDrivenBlock block, ForgeDirection stairDirection, ForgeDirection connectionDirection, ForgeDirection bottom) {
        setHalfTopBounds(block, stairDirection, bottom);
        trimDirection(block, connectionDirection);
    }

    private void setHalfTopBounds(DataDrivenBlock block, ForgeDirection front, ForgeDirection bottom) {
        setBasicSlabFromDir(bottom, block);
        trimDirection(block, front);
    }

    private void addCollisionBox(DataDrivenBlock block, AxisAlignedBB mask, World world, int x, int y, int z, List list) {
        AxisAlignedBB axisalignedbb1 = block.getCollisionBoundingBoxFromPool(world, x, y, z);

        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1))
        {
            list.add(axisalignedbb1);
        }
    }
}
