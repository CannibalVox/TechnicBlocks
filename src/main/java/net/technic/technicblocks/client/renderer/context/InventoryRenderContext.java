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

package net.technic.technicblocks.client.renderer.context;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import org.lwjgl.util.vector.Vector3f;

public class InventoryRenderContext implements IRenderContext {

    private DataDrivenBlock block;
    private int metadata;

    public InventoryRenderContext(DataDrivenBlock block, int metadata) {
        this.metadata = metadata;
        this.block = block;
    }

    @Override
    public boolean isModelConnected(ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isTextureConnected(ForgeDirection side, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public boolean isFaceVisible(ForgeDirection side) {
        return true;
    }

    @Override
    public IIcon getTexture(ForgeDirection side) {
        return block.getIcon(side.ordinal(), metadata);
    }

    @Override
    public Vector3f getTopLeft(ForgeDirection side) {
        return block.getSubBlock(metadata).getTextureScheme().getTopLeft(side);
    }

    @Override
    public void preDrawFace(ForgeDirection dir, boolean internal, float startX, float startY, float endX, float endY, RenderBlocks renderer, Tessellator tessellator) {
        tessellator.startDrawingQuads();
        tessellator.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
        renderer.enableAO = false;
    }

    @Override
    public void postDrawFace(RenderBlocks renderer, Tessellator tessellator) {
        tessellator.draw();
    }
}
