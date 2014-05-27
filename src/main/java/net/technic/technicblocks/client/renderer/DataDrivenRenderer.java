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

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenBlockRegistry;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import net.technic.technicblocks.client.renderer.context.InventoryRenderContext;
import net.technic.technicblocks.client.renderer.context.WorldRenderContext;
import net.technic.technicblocks.client.renderer.tessellator.Tessellator;
import net.technic.technicblocks.client.renderer.tessellator.TessellatorInstance;
import net.technic.technicblocks.client.renderer.tessellator.facehandlers.SingleSidedHandler;
import net.technic.technicblocks.client.renderer.tessellator.preposthandlers.InventoryLightingHandler;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.opengl.GL11;

public abstract class DataDrivenRenderer implements ISimpleBlockRenderingHandler {

    private int rendererId;
    private boolean isInventoryMode = false;

    public DataDrivenRenderer(int rendererId) {
        this.rendererId = rendererId;
    }

    public boolean isOpaqueCube() {
        return true;
    }

    public int getOpacity() {
        return this.isOpaqueCube() ? 255 : 0;
    }

    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return world.getLightBrightnessForSkyBlocks(x, y, z, block.getLightValue(world, x, y, z));
    }

    public boolean shouldForceUseNeighborBrightness() {
        return false;
    }

    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return true;
    }

    public abstract String getDefaultCollisionType();
    public abstract String getDefaultSelectionType();

    protected abstract boolean tesselate(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext);

    protected boolean renderFaceIfVisible(ForgeDirection face, float startX, float startY, float endX, float endY, BlockTextureScheme textureScheme, IRenderContext connectionContext, TessellatorInstance tessellatorInstance, ForgeDirection upDir) {
        if (connectionContext.isFaceVisible(face)) {
            renderFace(face, startX, startY, endX, endY, 0.0f, textureScheme, connectionContext, tessellatorInstance, upDir);
            return true;
        }

        return false;
    }

    protected void renderFace(ForgeDirection face, float startX, float startY, float endX, float endY, float depth, BlockTextureScheme textureScheme, IRenderContext posContext, TessellatorInstance tessellatorInstance, ForgeDirection upDir) {
        tessellatorInstance.renderFace(face, startX, startY, endX, endY, depth, textureScheme, posContext, upDir);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        DataDrivenBlock ddBlock = DataDrivenBlockRegistry.getDataDrivenBlock(block);

        if (ddBlock == null)
            return;

        DataDrivenSubBlock subBlock = ddBlock.getSubBlock(metadata);

        GL11.glRotatef(180.0f, 0, 1.0f, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        isInventoryMode = true;
        Tessellator blockTess = ddBlock.getBlockModel().getTessellator();
        Tessellator tess = new Tessellator(new InventoryLightingHandler(), blockTess.getFaceHandler());
        tesselate(ddBlock, metadata, tess.getInstance(renderer), new InventoryRenderContext(ddBlock, subBlock.getMetadata()));
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        DataDrivenBlock ddBlock = DataDrivenBlockRegistry.getDataDrivenBlock(block);

        if (ddBlock == null)
            return false;

        int metadata = world.getBlockMetadata(x,y,z);
        DataDrivenSubBlock subBlock = ddBlock.getSubBlock(metadata);

        isInventoryMode = false;
        return tesselate(ddBlock, metadata, ddBlock.getBlockModel().getTessellatorInstance(renderer), new WorldRenderContext(ddBlock, subBlock, world, x, y, z));
    }

    @Override
    public int getRenderId() {
        return rendererId;
    }
}
