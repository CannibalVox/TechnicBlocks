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

import net.minecraft.world.IBlockAccess;
import net.technic.technicblocks.blocks.DataDrivenBlock;

public class SlabSelection extends BlockSelection {
    @Override
    public void setBlockBounds(DataDrivenBlock block, IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x,y,z);
        boolean isOnFloor = block.isOnFloor(metadata);

        if (isOnFloor)
            block.setBlockBounds(0, 0, 0, 1, 0.5f, 1);
        else
            block.setBlockBounds(0, 0.5f, 0, 1, 1, 1);
    }
}
