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
import net.technic.technicblocks.client.renderer.tessellator.TessellatorInstance;
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
    protected boolean tesselate(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        if (connectionContext instanceof InventoryRenderContext)
            return tesselateInventory(block, metadata, tessellatorInstance, connectionContext);

        ForgeDirection up = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);
        ForgeDirection down = block.reverseTransformBlockFacing(metadata, ForgeDirection.DOWN);
        ForgeDirection front = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);
        renderPost(subBlock.getTextureScheme(), connectionContext, tessellatorInstance, up, down, front);

        ForgeDirection north = front;

        for (int i = 0; i < 4; i++) {
            if (connectionContext.isModelConnected(north))
                renderFencing(north, up, down, subBlock.getTextureScheme(), connectionContext, tessellatorInstance);

            north = north.getRotation(up);
        }

        return true;
    }

    private boolean tesselateInventory(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        GL11.glTranslatef(0, 0, -0.4375f);
        renderPost(subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH);
        renderFencing(ForgeDirection.SOUTH, ForgeDirection.UP, ForgeDirection.DOWN, subBlock.getTextureScheme(), connectionContext, tessellatorInstance);

        GL11.glTranslatef(0, 0, 1);
        renderPost(subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH);
        renderFencing(ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.DOWN, subBlock.getTextureScheme(), connectionContext, tessellatorInstance);

        GL11.glTranslatef(0, 0, -0.5625f);

        return true;
    }

    private void renderPost(BlockTextureScheme textureScheme, IRenderContext renderContext, TessellatorInstance tessellatorInstance, ForgeDirection up, ForgeDirection down, ForgeDirection front) {
        renderFaceIfVisible(up, 0.375f, 0.375f, 0.625f, 0.625f, textureScheme, renderContext, tessellatorInstance, front);
        renderFaceIfVisible(down, 0.375f ,0.375f, 0.625f, 0.625f, textureScheme, renderContext, tessellatorInstance, front);

        for (int i = 0; i < 4; i++) {
            renderFace(front, 0.375f, 0, 0.625f, 1, 0.375f, textureScheme, renderContext, tessellatorInstance, up);
            front = front.getRotation(up);
        }
    }

    private void renderFencing(ForgeDirection runningDirection, ForgeDirection top, ForgeDirection bottom, BlockTextureScheme textureScheme, IRenderContext renderContext, TessellatorInstance tessellatorInstance) {
        renderFaceIfVisible(runningDirection, 0.4375f, 0.0625f, 0.5625f, 0.25f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(top), 0.625f, 0.0625f, 1.0f, 0.25f, 0.4375f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(bottom), 0, 0.0625f, 0.4375f, 0.25f, 0.4375f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(top, 0.4375f, 0, 0.5625f, 0.375f, 0.0625f, textureScheme, renderContext, tessellatorInstance, runningDirection);
        renderFace(bottom, 0.4375f, 0, 0.5625f, 0.375f, 0.75f, textureScheme, renderContext, tessellatorInstance, runningDirection);

        renderFaceIfVisible(runningDirection, 0.4375f, 0.4375f, 0.5625f, 0.625f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(top), 0.625f, 0.4375f, 1.0f, 0.625f, 0.4375f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(bottom), 0, 0.4375f, 0.4375f, 0.625f, 0.4375f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(top, 0.4375f, 0, 0.5625f, 0.375f, 0.4375f, textureScheme, renderContext, tessellatorInstance, runningDirection);
        renderFace(bottom,  0.4375f, 0, 0.5625f, 0.375f, 0.375f, textureScheme, renderContext, tessellatorInstance, runningDirection);
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
