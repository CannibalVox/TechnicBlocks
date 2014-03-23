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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.behavior.BlockBehavior;
import net.technic.technicblocks.blocks.behavior.functions.IBlockDropBehavior;
import net.technic.technicblocks.blocks.behavior.functions.IBlockPlacementBehavior;
import net.technic.technicblocks.blocks.behavior.functions.ICreativePickerBehavior;
import net.technic.technicblocks.blocks.behavior.functions.IItemBlockTargetBehavior;
import net.technic.technicblocks.client.BlockModel;
import net.technic.technicblocks.items.DataDrivenItemBlock;

import java.util.*;

public class DataDrivenBlock extends Block {
    private BlockModel blockModel;
    private Collection<String> blockTags;
    private List<Integer> subBlockMetadatas = new ArrayList<Integer>();
    private Map<Integer, DataDrivenSubBlock> subBlocks = new HashMap<Integer, DataDrivenSubBlock>();

    private byte subblockMask;

    private List<BlockBehavior> behaviors;
    private List<IBlockPlacementBehavior> blockPlacementBehaviors = new ArrayList<IBlockPlacementBehavior>();
    private List<IItemBlockTargetBehavior> itemBlockTargetBehaviors = new ArrayList<IItemBlockTargetBehavior>();
    private List<IBlockDropBehavior> blockDropBehaviors = new ArrayList<IBlockDropBehavior>();
    private List<ICreativePickerBehavior> creativePickerBehaviors = new ArrayList<ICreativePickerBehavior>();

    public DataDrivenBlock(Material material, BlockModel blockModel, Collection<String> blockTags, List<BlockBehavior> behaviors, List<DataDrivenSubBlock> dataDrivenSubBlocks) {
        super(material);

        this.blockModel = blockModel;
        this.blockTags = blockTags;
        this.behaviors = behaviors;

        interfaceifyBehaviors();

        for (DataDrivenSubBlock subBlock : dataDrivenSubBlocks) {
            subBlockMetadatas.add(subBlock.getMetadata());
            subBlocks.put(subBlock.getMetadata(), subBlock);
        }

        subblockMask = 0x0F;

        byte nonSubblockMask = 0;
        for(BlockBehavior convention : behaviors) {
            if (convention.isMetadataReserved())
                nonSubblockMask |= convention.getMetadataMask();
        }

        subblockMask = (byte)(subblockMask & ~nonSubblockMask);

        this.opaque = this.isOpaqueCube();
        this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
    }

    private void interfaceifyBehaviors() {
        for(BlockBehavior behavior : behaviors) {
            if (behavior instanceof IBlockPlacementBehavior)
                blockPlacementBehaviors.add((IBlockPlacementBehavior)behavior);
            if (behavior instanceof IItemBlockTargetBehavior)
                itemBlockTargetBehaviors.add((IItemBlockTargetBehavior)behavior);
            if (behavior instanceof IBlockDropBehavior)
                blockDropBehaviors.add((IBlockDropBehavior)behavior);
            if (behavior instanceof ICreativePickerBehavior)
                creativePickerBehaviors.add((ICreativePickerBehavior)behavior);
        }
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
        ForgeDirection dir = transformBlockFacing(meta, ForgeDirection.VALID_DIRECTIONS[side]);
        return getSubBlock(meta).getTextureScheme().getTextureForSide(dir);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        int meta = world.getBlockMetadata(x,y,z);
        ForgeDirection physicalSide = ForgeDirection.VALID_DIRECTIONS[side];
        ForgeDirection virtualSide = transformBlockFacing(meta, physicalSide);

        return getSubBlock(meta).getTextureScheme().getTextureForSide(this, world, x, y, z, physicalSide, virtualSide);
    }

    public BlockModel getBlockModel() {
        return blockModel;
    }

    public boolean hasBlockTag(String tag) {
        return blockTags.contains(tag);
    }

    public ForgeDirection transformBlockFacing(int metadata, ForgeDirection direction) {

        for (BlockBehavior convention : behaviors) {
            direction = convention.transformBlockFacing(metadata, direction);
        }

        return direction;
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

    public int getSubBlockMask() {
        return subblockMask;
    }

    public int getSubBlockCount() {
        return subBlocks.size();
    }

    public int getSubBlockMetadataByIndex(int index) {
        return subBlockMetadatas.get(index);
    }

    @Override
    public int getRenderType() { return getBlockModel().getRendererId(); }

    @Override
    public boolean isOpaqueCube() {
        if (getBlockModel() == null)
            return true;
        return getBlockModel().isOpaqueCube();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return getBlockModel().isSideSolid(this, world, x, y, z, side);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        ForgeDirection face = ForgeDirection.VALID_DIRECTIONS[side];

        for (IBlockPlacementBehavior behavior : blockPlacementBehaviors)
            metadata = behavior.transformPlacementMetadata(this, world, x, y, z, face, hitX, hitY, hitZ, metadata);

        return metadata;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item)
    {
        for (IBlockPlacementBehavior behavior : blockPlacementBehaviors)
            behavior.triggerBlockPlacement(this, world, x, y, z, player, item);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        boolean dropDefaults = true;

        for(IBlockDropBehavior behavior : blockDropBehaviors) {
            dropDefaults = behavior.doesDropDefault(world, x, y, z, metadata, fortune);

            if (!dropDefaults)
                break;
        }

        ArrayList<ItemStack> returnValue;

        if (dropDefaults)
            returnValue = super.getDrops(world, x, y, z, metadata, fortune);
        else
            returnValue = new ArrayList<ItemStack>();

        for (IBlockDropBehavior behavior : blockDropBehaviors) {
            behavior.addExtraDrops(returnValue, world, x, y, z, metadata, fortune);
        }

        return returnValue;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        ItemStack result = super.getPickBlock(target, world, x, y, z);

        for(ICreativePickerBehavior pickerBehavior : creativePickerBehaviors) {
            result = pickerBehavior.transformPickResult(target, world, x, y, z, result);
        }

        return result;
    }

    public boolean shouldPlaceBlock(EntityPlayer player, DataDrivenItemBlock item, ItemStack itemStack, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        boolean shouldPlaceBlock = true;
        ForgeDirection face = ForgeDirection.VALID_DIRECTIONS[side];

        for (IItemBlockTargetBehavior behavior : itemBlockTargetBehaviors) {
            shouldPlaceBlock = behavior.transformShouldPlaceBlock(player, this, item, itemStack, world, x, y, z, face, hitX, hitY, hitZ, shouldPlaceBlock);
        }

        return shouldPlaceBlock;
    }

    public boolean itemUsedOnBlock(EntityPlayer player, DataDrivenItemBlock item, ItemStack itemStack, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        boolean shouldConsumeItem = false;
        ForgeDirection face = ForgeDirection.VALID_DIRECTIONS[side];

        for (IItemBlockTargetBehavior behavior : itemBlockTargetBehaviors) {
            shouldConsumeItem = behavior.itemUsedOnBlock(player, this, item, itemStack, world, x, y, z, face, hitX, hitY, hitZ) || shouldConsumeItem;
        }

        return shouldConsumeItem;
    }
}
