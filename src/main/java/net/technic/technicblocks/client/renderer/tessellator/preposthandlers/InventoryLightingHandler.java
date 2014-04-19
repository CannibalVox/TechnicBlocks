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

package net.technic.technicblocks.client.renderer.tessellator.preposthandlers;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.client.renderer.context.IRenderContext;

public class InventoryLightingHandler implements IPrePostFaceHandler {
    @Override
    public void preDrawFace(IRenderContext renderContext, ForgeDirection dir, boolean internal, float startX, float startY, float endX, float endY, RenderBlocks blocks, Tessellator tessellator) {
        tessellator.startDrawingQuads();
        tessellator.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);
        blocks.enableAO = false;
    }

    @Override
    public void postDrawFace(RenderBlocks blocks, Tessellator tessellator) {
        tessellator.draw();
    }
}
