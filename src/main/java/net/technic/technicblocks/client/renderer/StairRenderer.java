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

package net.technic.technicblocks.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;

public class StairRenderer extends DataDrivenRenderer {

    public StairRenderer(int renderId) {
        super(renderId);
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext) {
        boolean isOnFloor = block.isOnFloor(metadata);
        ForgeDirection facing = block.transformBlockFacing(metadata, ForgeDirection.NORTH);

        ForgeDirection leftSide = facing.getRotation(ForgeDirection.UP);
        ForgeDirection rightSide = facing.getRotation(ForgeDirection.DOWN);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        renderBasicSlab(subBlock, connectionContext, renderer, isOnFloor);

        ForgeDirection opposite = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[facing.ordinal()]];
        ItemStack northConnection = connectionContext.getConnectedBlock(opposite);
        if (northConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(northConnection, isOnFloor);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                renderQuarterStair(subBlock, connectionContext, renderer, isOnFloor, facing, connectionFacing == rightSide);
                return true;
            }
        }

        renderHalfStair(subBlock, connectionContext, renderer, isOnFloor, facing);

        ItemStack southConnection = connectionContext.getConnectedBlock(facing);
        if (southConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(southConnection, isOnFloor);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                renderQuarterStair(subBlock, connectionContext, renderer, isOnFloor, opposite, connectionFacing == leftSide);
            }
        }

        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int metadata = world.getBlockMetadata(x, y, z);

        if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
            boolean isOnFloor = block.isOnFloor(metadata);

            return (isOnFloor == (side == ForgeDirection.DOWN));
        }

        ForgeDirection solidSide = block.transformBlockFacing(metadata, ForgeDirection.SOUTH);
        return (side == solidSide);
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

    private void renderBasicSlab(DataDrivenSubBlock subBlock, IRenderContext connectionContext, RenderBlocks renderer, boolean isOnFloor) {
        if (isOnFloor) {
            renderFaceIfVisible(ForgeDirection.DOWN, 0, 0, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer);
            renderFace(ForgeDirection.UP, 0, 0, 1.0f, 1.0f, 0.5f, subBlock.getTextureScheme(), connectionContext, renderer);
        } else {
            renderFaceIfVisible(ForgeDirection.UP, 0, 0, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer);
            renderFace(ForgeDirection.DOWN.DOWN, 0, 0, 1.0f, 1.0f, 0.5f, subBlock.getTextureScheme(), connectionContext, renderer);
        }

        float startY = 0.5f;
        float endY = 1.0f;

        if (!isOnFloor) {
            startY -= 0.5f;
            endY -= 0.5f;
        }

        renderFaceIfVisible(ForgeDirection.NORTH, 0, startY, 1.0f, endY, subBlock.getTextureScheme(), connectionContext, renderer);
        renderFaceIfVisible(ForgeDirection.EAST, 0, startY, 1.0f, endY, subBlock.getTextureScheme(), connectionContext, renderer);
        renderFaceIfVisible(ForgeDirection.WEST, 0, startY, 1.0f, endY, subBlock.getTextureScheme(), connectionContext, renderer);
        renderFaceIfVisible(ForgeDirection.SOUTH, 0, startY, 1.0f, endY, subBlock.getTextureScheme(), connectionContext, renderer);
    }

    private void renderQuarterStair(DataDrivenSubBlock subBlock, IRenderContext renderContext, RenderBlocks renderer, boolean isOnFloor, ForgeDirection facing, boolean isFacingLeft) {
        float startY = 0;
        float endY = 0.5f;

        if (!isOnFloor) {
            startY += 0.5f;
            endY += 0.5f;
        }

        ForgeDirection left = facing.getRotation(ForgeDirection.UP);
        ForgeDirection right = facing.getRotation(ForgeDirection.DOWN);

        renderFaceIfVisible((isFacingLeft?left:right), (isFacingLeft?0:0.5f), startY, (isFacingLeft?0.5f:1.0f), endY, subBlock.getTextureScheme(), renderContext, renderer);
        renderFace((isFacingLeft?right:left), (isFacingLeft?0.5f:0), startY, (isFacingLeft?1.0f:0.5f), endY, 0.5f, subBlock.getTextureScheme(), renderContext, renderer);
        renderFace(facing, (isFacingLeft?0:0.5f), startY, (isFacingLeft?0.5f:1.0f), endY, 0.5f, subBlock.getTextureScheme(), renderContext, renderer);

        ForgeDirection back = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[facing.ordinal()]];
        renderFaceIfVisible(back, (isFacingLeft?0.5f:0), startY, (isFacingLeft?1.0f:0.5f), endY, subBlock.getTextureScheme(), renderContext, renderer);

        float startX = 0, endX = 0;
        if ((facing == ForgeDirection.NORTH && isFacingLeft) || (facing == ForgeDirection.WEST && !isFacingLeft)) {
            startX = 0.5f;
            startY = 0.5f;
            endX = 1.0f;
            endY = 1.0f;
        } else if ((facing == ForgeDirection.SOUTH && isFacingLeft) || (facing == ForgeDirection.EAST && !isFacingLeft)) {
            startX = 0;
            startY = 0;
            endX = 0.5f;
            endY = 0.5f;
        } else if ((facing == ForgeDirection.NORTH && !isFacingLeft) || (facing == ForgeDirection.EAST && isFacingLeft)) {
            startX = 0;
            startY = 0.5f;
            endX = 0.5f;
            endY = 1.0f;
        } else {
            startX = 0.5f;
            startY = 0;
            endX = 1.0f;
            endY = 0.5f;
        }

        ForgeDirection drawFace = ForgeDirection.UP;

        if (!isOnFloor) {
            float tempEndY = 1.0f - startY;
            startY = 1.0f - endY;
            endY = tempEndY;
            drawFace = ForgeDirection.DOWN;
        }

        renderFaceIfVisible(drawFace, startX, startY, endX, endY, subBlock.getTextureScheme(), renderContext, renderer);
    }

    private void renderHalfStair(DataDrivenSubBlock subBlock, IRenderContext renderContext, RenderBlocks renderer, boolean isOnFloor, ForgeDirection facing) {
        //Render the front and back of the stair
        ForgeDirection backSide = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[facing.ordinal()]];
        float startY = 0;
        float endY = 0.5f;

        if (!isOnFloor) {
            startY += 0.5f;
            endY += 0.5f;
        }

        renderFace(facing, 0, startY, 1.0f, endY, 0.5f, subBlock.getTextureScheme(), renderContext, renderer);
        renderFaceIfVisible(backSide, 0, startY, 1.0f, endY, subBlock.getTextureScheme(), renderContext, renderer);

        ForgeDirection leftSide = facing.getRotation(ForgeDirection.UP);
        ForgeDirection rightSide = facing.getRotation(ForgeDirection.DOWN);

        renderFaceIfVisible(rightSide, 0.5f, startY, 1.0f, endY, subBlock.getTextureScheme(), renderContext, renderer);
        renderFaceIfVisible(leftSide, 0.0f, startY, 0.5f, endY, subBlock.getTextureScheme(), renderContext, renderer); 
        float startX = 0, endX = 0;
        switch (facing) {
            case NORTH:
                startX = 0;
                startY = 0.5f;
                endX = 1.0f;
                endY = 1.0f;
                break;
            case SOUTH:
                startX = 0;
                startY = 0;
                endX = 1.0f;
                endY = 0.5f;
                break;
            case EAST:
                startX = 0;
                startY = 0;
                endX = 0.5f;
                endY = 1.0f;
                break;
            default:
                startX = 0.5f;
                startY = 0;
                endX = 1.0f;
                endY = 1.0f;
        }

        ForgeDirection drawFace = ForgeDirection.UP;

        if (!isOnFloor) {
            float tempEndY = 1.0f - startY;
            startY = 1.0f - endY;
            endY = tempEndY;
            drawFace = ForgeDirection.DOWN;
        }

        renderFaceIfVisible(drawFace, startX, startY, endX, endY, subBlock.getTextureScheme(), renderContext, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }
}
