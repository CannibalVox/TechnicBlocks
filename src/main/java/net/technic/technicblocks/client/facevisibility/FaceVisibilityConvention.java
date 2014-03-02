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

package net.technic.technicblocks.client.facevisibility;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenBlockRegistry;
import net.technic.technicblocks.client.renderer.context.IRenderContext;

public abstract class FaceVisibilityConvention {
    protected abstract boolean isSideVisible(IBlockAccess world, int x, int y, int z, ForgeDirection direction, DataDrivenBlock thisBlock, Block otherBlock, IRenderContext connections);
    protected abstract boolean isSideVisible(IBlockAccess world, int x, int y, int z, ForgeDirection direction, DataDrivenBlock thisBlock, DataDrivenBlock otherBlock, IRenderContext connections);

    public final boolean isSideVisible(IBlockAccess world, int x, int y, int z, ForgeDirection side, IRenderContext connections) {
        Block thisBlock = world.getBlock(x,y,z);

        DataDrivenBlock thisDDBlock = DataDrivenBlockRegistry.getDataDrivenBlock(thisBlock);

        if (thisDDBlock == null)
            return true;

        int otherX = x + side.offsetX;
        int otherY = y + side.offsetY;
        int otherZ = z + side.offsetZ;

        Block otherBlock = world.getBlock(otherX, otherY, otherZ);
        DataDrivenBlock otherDDBlock = DataDrivenBlockRegistry.getDataDrivenBlock(otherBlock);

        if (otherDDBlock == null)
            return isSideVisible(world, x, y, z, side, thisDDBlock, otherBlock, connections);
        else
            return isSideVisible(world, x, y, z, side, thisDDBlock, otherDDBlock, connections);
    }
}
