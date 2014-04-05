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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.behavior.functions.IBlockPlacementBehavior;
import net.technic.technicblocks.blocks.behavior.functions.IBlockTickBehavior;
import net.technic.technicblocks.blocks.behavior.functions.INeighborUpdateBehavior;

import java.util.Random;

public class RedstoneDeactivationBehavior extends BlockBehavior implements IBlockPlacementBehavior, IBlockTickBehavior, INeighborUpdateBehavior {

    private String targetBlock;

    public RedstoneDeactivationBehavior(String[] args) {
        super(args);

        if (args.length < 1) {
            throw TechnicBlocks.getProxy().createParseException("'redstoneOff' behaviors should have at least one parameter: the name of the block to transform to.");
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
    public int transformPlacementMetadata(DataDrivenBlock block, World world, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int metadata) {
        return metadata;
    }

    @Override
    public void triggerBlockPlacement(DataDrivenBlock block, World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
        if (!world.isRemote)
        {
            if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, block, 4);
            }
        }
    }

    @Override
    public boolean shouldTickRandomly() {
        return false;
    }

    @Override
    public void blockUpdateTick(DataDrivenBlock block, World world, int x, int y, int z, Random random) {
        if (!world.isRemote && !world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            Block newBlock = (Block)Block.blockRegistry.getObject(targetBlock);
            int metadata = world.getBlockMetadata(x, y, z);
            world.setBlock(x, y, z, newBlock, metadata, 2);
        }
    }

    @Override
    public void neighborUpdated(DataDrivenBlock block, World world, int x, int y, int z, Block neighbor) {
        if (!world.isRemote)
        {
            if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, block, 4);
            }
        }
    }
}
