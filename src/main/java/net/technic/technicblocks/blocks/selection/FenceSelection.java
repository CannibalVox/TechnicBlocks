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
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;

public class FenceSelection extends BlockSelection {
    @Override
    public void setBlockBounds(DataDrivenBlock block, IBlockAccess world, int x, int y, int z) {

        block.setBlockBounds(0, 0, 0, 1, 1, 1);

        ConnectionConvention connection = block.getBlockModel().getModelConnectionsConvention();
        int metadata = world.getBlockMetadata(x, y, z);

        ForgeDirection north = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection south = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[north.ordinal()]];
        ForgeDirection up = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);

        for (int i = 0; i < 4; i++) {
            Block connect = world.getBlock(x + north.offsetX, y + north.offsetY, z + north.offsetZ);
            int connectMeta = world.getBlockMetadata(x + north.offsetX, y + north.offsetY, z + north.offsetZ);

            if (!connection.testConnection(block, metadata, connect, connectMeta))
                trimDirection(block, south, 0.375f);

            north = north.getRotation(up);
            south = south.getRotation(up);
        }
    }
}
