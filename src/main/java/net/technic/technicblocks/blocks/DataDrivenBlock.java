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

package net.technic.technicblocks.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.behavior.BlockBehavior;
import net.technic.technicblocks.client.BlockModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDrivenBlock extends Block {
    private BlockModel blockModel;
    private Collection<String> blockTags;
    private List<BlockBehavior> behaviors;
    private Map<Integer, DataDrivenSubBlock> subBlocks = new HashMap<Integer, DataDrivenSubBlock>();

    private byte subblockMask;

    public DataDrivenBlock(Material material, BlockModel blockModel, Collection<String> blockTags, List<BlockBehavior> behaviors, List<DataDrivenSubBlock> dataDrivenSubBlocks) {
        super(material);

        this.blockModel = blockModel;
        this.blockTags = blockTags;
        this.behaviors = behaviors;

        for (DataDrivenSubBlock subBlock : dataDrivenSubBlocks) {
            subBlocks.put(subBlock.getMetadata(), subBlock);
        }

        subblockMask = 0x0F;

        byte nonSubblockMask = 0;
        for(BlockBehavior convention : behaviors) {
            if (convention.isMetadataReserved())
                nonSubblockMask |= convention.getMetadataMask();
        }

        subblockMask = (byte)(subblockMask & ~nonSubblockMask);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for(DataDrivenSubBlock subBlock : subBlocks.values()) {
            if (subBlock.isInCreativeMenu()) {
                list.add(new ItemStack(this, 1, subBlock.getMetadata()));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry)
    {
        for (DataDrivenSubBlock subBlock : subBlocks.values()) {
            subBlock.getTextureScheme().registerIcons(registry);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        return subBlocks.get(meta).getTextureScheme().getTextureForSide(dir);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        int meta = world.getBlockMetadata(x,y,z);
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];

        return subBlocks.get(meta).getTextureScheme().getTextureForSide(world, x, y, z, dir);
    }


    public BlockModel getBlockModel() {
        return blockModel;
    }

    public boolean hasBlockTag(String tag) {
        return blockTags.contains(tag);
    }

    public ForgeDirection getBlockFacing(int metadata) {
        ForgeDirection facing = ForgeDirection.NORTH;

        for (BlockBehavior convention : behaviors) {
            facing = convention.transformBlockFacing(metadata, facing);
        }

        return facing;
    }

    public boolean isOnFloor(int metadata) {
        boolean isOnFloor = true;

        for (BlockBehavior convention : behaviors) {
            isOnFloor = convention.transformIsOnFloor(metadata, isOnFloor);
        }

        return isOnFloor;
    }

    public DataDrivenSubBlock getSubBlock(int metadata) {
        metadata &= subblockMask;

        if (subBlocks.containsKey(metadata))
            return subBlocks.get(metadata);

        return null;
    }

    public DataDrivenSubBlock getSubBlock(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x,y,z);

        return getSubBlock(metadata);
    }

    @Override
    public int getRenderType() { return getBlockModel().getRendererId(); }
}
