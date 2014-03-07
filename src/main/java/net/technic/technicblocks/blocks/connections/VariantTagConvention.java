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

package net.technic.technicblocks.blocks.connections;

import net.minecraft.block.Block;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;

public class VariantTagConvention extends ConnectionConvention {
    public VariantTagConvention(String[] args) {
        super(args);
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, Block otherBlock, int otherMetadata) {
        return false;
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, DataDrivenBlock otherBlock, int otherMetadata) {
        DataDrivenSubBlock thisSubBlock = thisBlock.getSubBlock(thisMetadata);
        DataDrivenSubBlock otherSubBlock = otherBlock.getSubBlock(otherMetadata);

        return (thisSubBlock.getVariantTag() != null && otherSubBlock.getVariantTag() != null && thisSubBlock.getVariantTag().equalsIgnoreCase(otherSubBlock.getVariantTag()));
    }
}
