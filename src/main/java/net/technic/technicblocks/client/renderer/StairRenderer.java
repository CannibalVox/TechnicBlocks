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
import net.technic.technicblocks.client.renderer.tessellator.TessellatorInstance;

public class StairRenderer extends DataDrivenRenderer {

    public StairRenderer(int renderId) {
        super(renderId);
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        ForgeDirection facing = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection opposite = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[facing.ordinal()]];
        ForgeDirection top = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);
        ForgeDirection bottom = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[top.ordinal()]];

        ForgeDirection leftSide = facing.getRotation(top);
        ForgeDirection rightSide = facing.getRotation(bottom);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        renderBasicSlab(subBlock, connectionContext, tessellatorInstance, top, bottom, facing, opposite);

        ItemStack northConnection = connectionContext.getConnectedBlock(opposite);
        if (northConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(northConnection, bottom);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                renderQuarterStair(subBlock, connectionContext, tessellatorInstance, top, bottom, facing, opposite, connectionFacing == rightSide);
                return true;
            }
        }

        renderHalfStair(subBlock, connectionContext, tessellatorInstance, top, facing);

        ItemStack southConnection = connectionContext.getConnectedBlock(facing);
        if (southConnection != null) {
            ForgeDirection connectionFacing = getConnectionFacing(southConnection, bottom);

            if (connectionFacing == leftSide || connectionFacing == rightSide) {
                renderQuarterStair(subBlock, connectionContext, tessellatorInstance, top, bottom, opposite, facing, connectionFacing == leftSide);
            }
        }

        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getOpacity() {
        return 255;
    }

    @Override
    public boolean shouldForceUseNeighborBrightness() {
        return true;
    }

    @Override
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int metadata = world.getBlockMetadata(x, y, z);
        side = block.transformBlockFacing(metadata, side);

        if (side == ForgeDirection.DOWN || side == ForgeDirection.SOUTH)
            return true;
        return false;
    }

    @Override
    public String getDefaultCollisionType() { return "stairs"; }

    @Override
    public String getDefaultSelectionType() { return "cube"; }

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
        } else if (bottom == ForgeDirection.UP || bottom == ForgeDirection.DOWN) {
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

    private void renderBasicSlab(DataDrivenSubBlock subBlock, IRenderContext connectionContext, TessellatorInstance tessellatorInstance, ForgeDirection top, ForgeDirection bottom, ForgeDirection front, ForgeDirection back) {
        renderFaceIfVisible(bottom, 0, 0, 1, 1, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, front);
        renderFace(top, 0, 0, 1, 1, 0.5f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, front);

        for (int i = 0; i < 4; i++) {
            renderFaceIfVisible(front, 0, 0.5f, 1, 1, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, top);
            front = front.getRotation(top);
        }
    }

    private void renderQuarterStair(DataDrivenSubBlock subBlock, IRenderContext renderContext, TessellatorInstance tessellatorInstance, ForgeDirection up, ForgeDirection down, ForgeDirection facing, ForgeDirection back, boolean isFacingLeft) {
        ForgeDirection left = facing.getRotation(up);
        ForgeDirection right = facing.getRotation(down);

        renderFaceIfVisible((isFacingLeft?left:right), (isFacingLeft?0:0.5f), 0, (isFacingLeft?0.5f:1.0f), 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, up);
        renderFace((isFacingLeft?right:left), (isFacingLeft?0.5f:0), 0, (isFacingLeft?1.0f:0.5f), 0.5f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, up);
        renderFace(facing, (isFacingLeft?0:0.5f), 0, (isFacingLeft?0.5f:1.0f), 0.5f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, up);
        renderFaceIfVisible(back, (isFacingLeft?0.5f:0), 0, (isFacingLeft?1.0f:0.5f), 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, up);

        float startX = 0;
        float startY = 0.5f;
        float endX = 0.5f;
        float endY = 1.0f;

        if (isFacingLeft) {
            startX += 0.5f;
            endX += 0.5f;
        }
        renderFaceIfVisible(up, startX, startY, endX, endY, subBlock.getTextureScheme(), renderContext, tessellatorInstance, facing);
    }

    private void renderHalfStair(DataDrivenSubBlock subBlock, IRenderContext renderContext, TessellatorInstance tessellatorInstance, ForgeDirection top, ForgeDirection facing) {
        //Render the front and back of the stair
        ForgeDirection backSide = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[facing.ordinal()]];
        ForgeDirection leftSide = facing.getRotation(top);
        ForgeDirection rightSide = facing.getRotation(ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[top.ordinal()]]);

        renderFace(facing, 0, 0, 1.0f, 0.5f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, top);
        renderFaceIfVisible(backSide, 0, 0, 1.0f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, top);
        renderFaceIfVisible(rightSide, 0.5f, 0, 1.0f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, top);
        renderFaceIfVisible(leftSide, 0.0f, 0, 0.5f, 0.5f, subBlock.getTextureScheme(), renderContext, tessellatorInstance, top);

        renderFaceIfVisible(top, 0, 0.5f, 1, 1, subBlock.getTextureScheme(), renderContext, tessellatorInstance, facing);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }
}
