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

package net.technic.technicblocks.client;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityConvention;
import net.technic.technicblocks.client.renderer.DataDrivenRenderer;

public class BlockModel {
    private ConnectionConvention modelConnectionsConvention;
    private FaceVisibilityConvention faceVisibilityConvention;
    private DataDrivenRenderer renderer;

    public BlockModel(DataDrivenRenderer renderer, ConnectionConvention modelConnectionsConvention, FaceVisibilityConvention faceVisibilityConvention) {
        this.modelConnectionsConvention = modelConnectionsConvention;
        this.faceVisibilityConvention = faceVisibilityConvention;
        this.renderer = renderer;
    }

    public ConnectionConvention getModelConnectionsConvention() { return modelConnectionsConvention; }
    public FaceVisibilityConvention getFaceVisibilityConvention() { return faceVisibilityConvention; }
    public int getRendererId() { return renderer.getRenderId(); }
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return renderer.isSideSolid(block, world, x, y, z, side);
    }
    public boolean isOpaqueCube() {
        return renderer.isOpaqueCube();
    }
}
