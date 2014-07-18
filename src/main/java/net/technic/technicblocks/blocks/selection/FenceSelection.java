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

    protected float getTrimAmount() {
        return 0.375f;
    }
    protected float getVerticalTrimAmount() { return 0; }
    protected float getNoPostTrimAmount() { return 0; }

    @Override
    public void setBlockBounds(DataDrivenBlock block, IBlockAccess world, int x, int y, int z) {

        block.setBlockBounds(0, 0, 0, 1, 1, 1);

        ConnectionConvention connection = block.getBlockModel().getModelConnectionsConvention();
        int metadata = world.getBlockMetadata(x, y, z);

        ForgeDirection north = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);
        ForgeDirection south = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[north.ordinal()]];
        ForgeDirection up = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);

        boolean[] directions = new boolean[4];

        for (int i = 0; i < 4; i++) {
            Block connect = world.getBlock(x + north.offsetX, y + north.offsetY, z + north.offsetZ);
            int connectMeta = world.getBlockMetadata(x + north.offsetX, y + north.offsetY, z + north.offsetZ);

           directions[i] = connection.testConnection(block, metadata, connect, connectMeta);

            if (!directions[i])
                trimDirection(block, south, getTrimAmount());

            north = north.getRotation(up);
            south = south.getRotation(up);
        }

        if ((directions[0] == directions[2] && directions[1] == directions[3] && directions[0] != directions[1]) && (directions[0] || directions[1] || directions[2] || directions[3])) {
            trimDirection(block, ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[up.ordinal()]], getVerticalTrimAmount());

            if (!directions[1]) {
                south = south.getRotation(up);
                north = north.getRotation(up);
            }
            trimDirection(block, south, getNoPostTrimAmount());
            trimDirection(block, north, getNoPostTrimAmount());
        }
    }
}
