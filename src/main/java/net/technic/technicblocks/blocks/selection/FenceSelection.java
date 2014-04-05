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

package net.technic.technicblocks.blocks.selection;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;

public class FenceSelection extends BlockSelection {
    @Override
    public void setBlockBounds(DataDrivenBlock block, IBlockAccess world, int x, int y, int z) {
        float minX = 0.375f;
        float minY = 0;
        float minZ = 0.375f;
        float maxX = 0.625f;
        float maxY = 1;
        float maxZ = 0.625f;

        ConnectionConvention connection = block.getBlockModel().getModelConnectionsConvention();
        int metadata = world.getBlockMetadata(x, y, z);

        Block north = world.getBlock(x, y, z-1);
        int northMetadata = world.getBlockMetadata(x, y, z-1);

        Block south = world.getBlock(x, y, z+1);
        int southMetadata = world.getBlockMetadata(x, y, z+1);

        Block east = world.getBlock(x+1, y, z);
        int eastMetadata = world.getBlockMetadata(x+1, y, z);

        Block west = world.getBlock(x-1, y, z);
        int westMetadata = world.getBlockMetadata(x-1, y, z);

        if (connection.testConnection(block, metadata, north, northMetadata))
            minZ = 0;
        if (connection.testConnection(block, metadata, south, southMetadata))
            maxZ = 1;
        if (connection.testConnection(block, metadata, east, eastMetadata))
            maxX = 1;
        if (connection.testConnection(block, metadata, west, westMetadata))
            minX = 0;

        block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
