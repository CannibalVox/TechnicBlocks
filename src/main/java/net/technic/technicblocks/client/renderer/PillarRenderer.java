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
import net.technic.technicblocks.client.renderer.tessellator.TessellatorInstance;

public class PillarRenderer extends CubeRenderer {
    public PillarRenderer(int renderId) {
        super(renderId);
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        ForgeDirection actualTop = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);
        ForgeDirection actualBottom = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[actualTop.ordinal()]];

        boolean result = renderFaceIfVisible(actualTop, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance);
        result = renderFaceIfVisible(actualBottom, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance) || result;

        ForgeDirection actualNorth = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection actualSouth = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[actualNorth.ordinal()]];
        ForgeDirection actualEast = block.reverseTransformBlockFacing(metadata, ForgeDirection.EAST);
        ForgeDirection actualWest = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[actualEast.ordinal()]];
        result = renderFaceIfVisible(actualNorth, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, actualTop) || result;
        result = renderFaceIfVisible(actualSouth, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, actualTop) || result;
        result = renderFaceIfVisible(actualEast, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, actualTop) || result;
        result = renderFaceIfVisible(actualWest, 0.0f, 0.0f, 1.0f, 1.0f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, actualTop) || result;
        return result;
    }
}
