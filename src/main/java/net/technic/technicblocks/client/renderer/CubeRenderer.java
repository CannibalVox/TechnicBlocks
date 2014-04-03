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
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;

public class CubeRenderer extends DataDrivenRenderer {

    public CubeRenderer(int renderId) {
        super(renderId);
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext) {
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        boolean result = renderFaceIfVisible(ForgeDirection.UP, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer);
        result = renderFaceIfVisible(ForgeDirection.DOWN, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer) || result;
        result = renderFaceIfVisible(ForgeDirection.NORTH, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer) || result;
        result = renderFaceIfVisible(ForgeDirection.SOUTH, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer) || result;
        result = renderFaceIfVisible(ForgeDirection.EAST, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer) || result;
        result = renderFaceIfVisible(ForgeDirection.WEST, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, renderer) || result;
        return result;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public String getDefaultCollisionType() { return "cube"; }

    @Override
    public String getDefaultSelectionType() { return "cube"; }
}
