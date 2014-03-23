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

public class SlabRenderer extends DataDrivenRenderer {
    public SlabRenderer(int renderId) {
        super(renderId);
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, RenderBlocks renderer, IRenderContext connectionContext) {
        boolean isOnFloor = block.isOnFloor(metadata);
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

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

        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        if (side != ForgeDirection.DOWN && side != ForgeDirection.UP)
            return false;

        int metadata = world.getBlockMetadata(x,y,z);
        boolean isOnFloor = block.isOnFloor(metadata);

        ForgeDirection solidSide = (isOnFloor)?ForgeDirection.DOWN:ForgeDirection.UP;

        return side == solidSide;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }
}
