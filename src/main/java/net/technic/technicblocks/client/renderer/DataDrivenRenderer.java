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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenBlockRegistry;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import net.technic.technicblocks.client.renderer.context.InventoryRenderContext;
import net.technic.technicblocks.client.renderer.context.WorldRenderContext;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public abstract class DataDrivenRenderer implements ISimpleBlockRenderingHandler {

    private int rendererId;
    private boolean isInventoryMode = false;

    public DataDrivenRenderer(int rendererId) {
        this.rendererId = rendererId;
    }

    protected abstract boolean tesselate(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext);

    protected boolean renderFaceIfVisible(ForgeDirection face, float startX, float startY, float endX, float endY, BlockTextureScheme textureScheme, IRenderContext connectionContext, RenderBlocks renderer) {
        if (connectionContext.isFaceVisible(face)) {
            renderFace(face, startX, startY, endX, endY, 0.0f, textureScheme, connectionContext, renderer);
            return true;
        }

        return false;
    }

    protected void renderFace(ForgeDirection face, float startX, float startY, float endX, float endY, float depth, BlockTextureScheme textureScheme, IRenderContext posContext, RenderBlocks renderer) {
        IIcon icon = posContext.getTexture(face);

        Vector3f topLeft = posContext.getTopLeft(face);

        ForgeDirection xDir = textureScheme.getAxisSide(face, 1, 0);
        ForgeDirection yDir = textureScheme.getAxisSide(face, 0, 1);
        Vector3f xVec = new Vector3f(xDir.offsetX, xDir.offsetY, xDir.offsetZ);
        Vector3f yVec = new Vector3f(yDir.offsetX, yDir.offsetY, yDir.offsetZ);

        ForgeDirection intoDir = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[face.ordinal()]];
        Vector3f zVec = new Vector3f(intoDir.offsetX, intoDir.offsetY, intoDir.offsetZ);

        posContext.preDrawFace(face, depth > 0.0001f, startX, startY, endX, endY, renderer, Tessellator.instance);
        tesselateFace(icon, renderer, Tessellator.instance, topLeft, xVec, yVec, zVec, startX, startY, endX, endY, depth);
        posContext.postDrawFace(renderer, Tessellator.instance);
    }

    private void tesselateFace(IIcon icon, RenderBlocks renderer, Tessellator tessellator, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float startX, float startY, float endX, float endY, float depth) {
        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedTopLeft, renderer.colorGreenTopLeft, renderer.colorBlueTopLeft);
            tessellator.setBrightness(renderer.brightnessTopLeft);
        }
        addVertex(icon, tessellator, topLeft, xVec, yVec, zVec, startX, startY, depth);

        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedBottomLeft, renderer.colorGreenBottomLeft, renderer.colorBlueBottomLeft);
            tessellator.setBrightness(renderer.brightnessBottomLeft);
        }
        addVertex(icon, tessellator, topLeft, xVec, yVec, zVec, startX, endY, depth);

        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedBottomRight, renderer.colorGreenBottomRight, renderer.colorBlueBottomRight);
            tessellator.setBrightness(renderer.brightnessBottomRight);
        }
        addVertex(icon, tessellator, topLeft, xVec, yVec, zVec, endX, endY, depth);

        if (renderer.enableAO) {
            tessellator.setColorOpaque_F(renderer.colorRedTopRight, renderer.colorGreenTopRight, renderer.colorBlueTopRight);
            tessellator.setBrightness(renderer.brightnessTopRight);
        }
        addVertex(icon, tessellator, topLeft, xVec, yVec, zVec, endX, startY, depth);
     }

    private void addVertex(IIcon icon, Tessellator tessellator, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float x, float y, float z) {
        double vertX = topLeft.x + (xVec.x * x) + (yVec.x * y) + (zVec.x * z);
        double vertY = topLeft.y + (xVec.y * x) + (yVec.y * y) + (zVec.y * z);
        double vertZ = topLeft.z + (xVec.z * x) + (yVec.z * y) + (zVec.z * z);

        double u = icon.getInterpolatedU(16.0f * x);
        double v = icon.getInterpolatedV(16.0f * y);

        tessellator.addVertexWithUV(vertX, vertY, vertZ, u, v);
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
        tesselate(ddBlock, metadata, renderer, new InventoryRenderContext(ddBlock, subBlock.getMetadata()));
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
        return tesselate(ddBlock, metadata, renderer, new WorldRenderContext(ddBlock, subBlock, world, x, y, z));
    }

    @Override
    public int getRenderId() {
        return rendererId;
    }
}
