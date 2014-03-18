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

package net.technic.technicblocks.blocks.behavior;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.behavior.functions.IItemBlockTargetBehavior;
import net.technic.technicblocks.items.DataDrivenItemBlock;

public class CompleteSlabBehavior extends BlockBehavior implements IItemBlockTargetBehavior {
    private String targetBlock;

    public CompleteSlabBehavior(String[] args) {
        super(args);

        if (args.length < 1) {
            throw TechnicBlocks.getProxy().createParseException("'completeSlab' behaviors should have at least one parameter: the name of the block to use as a doubleslab.");
        }

        targetBlock = args[0];
    }

    @Override
    public boolean isMetadataReserved() {
        return false;
    }

    @Override
    public int getMetadataBitSize() {
        return 0;
    }

    @Override
    public boolean transformShouldPlaceBlock(EntityPlayer player, DataDrivenBlock block, DataDrivenItemBlock item, ItemStack stack, World world, int x, int y, int z, ForgeDirection face, float hitX, float hitY, float hitZ, boolean shouldPlaceBlock) {
        if (item.field_150939_a != block)
            return shouldPlaceBlock;

        int metadata = world.getBlockMetadata(x-face.offsetX,y-face.offsetY,z-face.offsetZ);
        boolean isOnFloor = block.isOnFloor(metadata);

        if ((isOnFloor && face == ForgeDirection.UP) || (!isOnFloor && face == ForgeDirection.DOWN))
            return false;

        return shouldPlaceBlock;
    }

    @Override
    public boolean itemUsedOnBlock(EntityPlayer player, DataDrivenBlock block, DataDrivenItemBlock item, ItemStack stack, World world, int x, int y, int z, ForgeDirection face, float hitX, float hitY, float hitZ) {
        if (item.field_150939_a != block)
            return false;

        int metadata = world.getBlockMetadata(x-face.offsetX,y-face.offsetY,z-face.offsetZ);
        boolean isOnFloor = block.isOnFloor(metadata);

        if ((isOnFloor && face == ForgeDirection.UP) || (!isOnFloor && face == ForgeDirection.DOWN)) {
            int newMetadata = (block.getSubBlockMask() & metadata);
            Block newBlock = (Block)Block.blockRegistry.getObject(targetBlock);

            world.setBlock(x, y - face.offsetY, z, newBlock, newMetadata, 3);
            return true;
        }

        return false;
    }
}
