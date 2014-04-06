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

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import net.technic.technicblocks.client.renderer.context.InventoryRenderContext;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.opengl.GL11;

public class FenceRenderer extends DataDrivenRenderer {

    public FenceRenderer(int rendererId) {
        super(rendererId);
    }

    @Override
    public String getDefaultCollisionType() {
        return "fence";
    }

    @Override
    public String getDefaultSelectionType() {
        return "fence";
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext) {
        if (connectionContext instanceof InventoryRenderContext)
            return tesselateInventory(block, metadata, renderer, connectionContext);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);
        renderPost(subBlock.getTextureScheme(), connectionContext, renderer);

        if (connectionContext.isModelConnected(ForgeDirection.NORTH))
            renderFencing(ForgeDirection.NORTH, subBlock.getTextureScheme(), connectionContext, renderer);

        if (connectionContext.isModelConnected(ForgeDirection.SOUTH))
            renderFencing(ForgeDirection.SOUTH, subBlock.getTextureScheme(), connectionContext, renderer);

        if (connectionContext.isModelConnected(ForgeDirection.EAST))
            renderFencing(ForgeDirection.EAST, subBlock.getTextureScheme(), connectionContext, renderer);

        if (connectionContext.isModelConnected(ForgeDirection.WEST))
            renderFencing(ForgeDirection.WEST, subBlock.getTextureScheme(), connectionContext, renderer);

        return true;
    }

    private boolean tesselateInventory(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext) {
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        GL11.glTranslatef(0, 0, -0.4375f);
        renderPost(subBlock.getTextureScheme(), connectionContext, renderer);
        renderFencing(ForgeDirection.SOUTH, subBlock.getTextureScheme(), connectionContext, renderer);

        GL11.glTranslatef(0, 0, 1);
        renderPost(subBlock.getTextureScheme(), connectionContext, renderer);
        renderFencing(ForgeDirection.NORTH, subBlock.getTextureScheme(), connectionContext, renderer);

        GL11.glTranslatef(0, 0, -0.5625f);

        return true;
    }

    private void renderPost(BlockTextureScheme textureScheme, IRenderContext renderContext, RenderBlocks renderer) {
        renderFaceIfVisible(ForgeDirection.UP, 0.375f, 0.375f, 0.625f, 0.625f, textureScheme, renderContext, renderer);
        renderFaceIfVisible(ForgeDirection.DOWN, 0.375f ,0.375f, 0.625f, 0.625f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.NORTH, 0.375f, 0, 0.625f, 1, 0.375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.SOUTH, 0.375f, 0, 0.625f, 1, 0.375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.WEST, 0.375f, 0, 0.625f, 1, 0.375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.EAST, 0.375f, 0, 0.625f, 1, 0.375f, textureScheme, renderContext, renderer);
    }

    private void renderFencing(ForgeDirection runningDirection, BlockTextureScheme textureScheme, IRenderContext renderContext, RenderBlocks renderer) {

        float startX;
        float topStartY;
        boolean eastWest;

        switch(runningDirection) {
            case NORTH:
                startX = 0.4375f;
                topStartY = 0;
                eastWest = false;
                break;
            case SOUTH:
                startX = 0.4375f;
                topStartY = 0.625f;
                eastWest = false;
                break;
            case WEST:
                startX = 0;
                topStartY = 0.4375f;
                eastWest = true;
                break;
            default:
                startX = 0.625f;
                topStartY = 0.4375f;
                eastWest = true;
        }

        float endX;
        float topEndY;

        if (eastWest) {
            endX = startX + 0.375f;
            topEndY = topStartY + 0.125f;
        } else {
            endX = startX + 0.125f;
            topEndY = topStartY + 0.375f;
        }

        float bottomStartY = 1.0f - topEndY;
        float bottomEndY = 1.0f - topStartY;

        renderFaceIfVisible(runningDirection, 0.4375f, 0.0625f, 0.5625f, 0.25f, textureScheme, renderContext, renderer);
        renderFace(runningDirection.getRotation(ForgeDirection.UP), 0.625f, 0.0625f, 1.0f, 0.25f, 0.4375f, textureScheme, renderContext, renderer);
        renderFace(runningDirection.getRotation(ForgeDirection.DOWN), 0, 0.0625f, 0.4375f, 0.25f, 0.4375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.UP, startX, topStartY, endX, topEndY, 0.0625f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.DOWN, startX, bottomStartY, endX, bottomEndY, 0.75f, textureScheme, renderContext, renderer);

        renderFaceIfVisible(runningDirection, 0.4375f, 0.4375f, 0.5625f, 0.625f, textureScheme, renderContext, renderer);
        renderFace(runningDirection.getRotation(ForgeDirection.UP), 0.625f, 0.4375f, 1.0f, 0.625f, 0.4375f, textureScheme, renderContext, renderer);
        renderFace(runningDirection.getRotation(ForgeDirection.DOWN), 0, 0.4375f, 0.4375f, 0.625f, 0.4375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.UP, startX, topStartY, endX, topEndY, 0.4375f, textureScheme, renderContext, renderer);
        renderFace(ForgeDirection.DOWN, startX, bottomStartY, endX, bottomEndY, 0.375f, textureScheme, renderContext, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return false;
    }
}