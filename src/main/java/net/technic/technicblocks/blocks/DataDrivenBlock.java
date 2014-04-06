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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.behavior.BlockBehavior;
import net.technic.technicblocks.blocks.behavior.functions.*;
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
    private List<INeighborUpdateBehavior> neighborUpdateBehaviors = new ArrayList<INeighborUpdateBehavior>();
    private List<IBlockTickBehavior> blockTickBehaviors = new ArrayList<IBlockTickBehavior>();

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

        this.opaque = getBlockModel().isOpaqueCube();
        this.setLightOpacity(getBlockModel().getOpacity());
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
            if (behavior instanceof INeighborUpdateBehavior)
                neighborUpdateBehaviors.add((INeighborUpdateBehavior)behavior);
            if (behavior instanceof IBlockTickBehavior) {
                if (((IBlockTickBehavior)behavior).shouldTickRandomly())
                    this.setTickRandomly(true);
                blockTickBehaviors.add((IBlockTickBehavior)behavior);
            }
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

    public ForgeDirection reverseTransformBlockFacing(int metadata, ForgeDirection direction) {
        for (BlockBehavior convention : behaviors) {
            direction = convention.reverseTransformBlockFacing(metadata, direction);
        }

        return direction;
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
    public boolean renderAsNormalBlock() {
        if (getBlockModel() == null)
            return true;
        return getBlockModel().isOpaqueCube();
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
    {
        return getBlockModel().getMixedBrightnessForBlock(world, x, y, z);
    }

    @Override
    public boolean getUseNeighborBrightness() {
        return getBlockModel().shouldForceUseNeighborBrightness() || super.getUseNeighborBrightness();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return getBlockModel().isSideSolid(this, world, x, y, z, side);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        getBlockModel().setBlockBounds(this, world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return AxisAlignedBB.getAABBPool().getAABB((double)x + this.getBlockBoundsMinX(), (double)y + this.getBlockBoundsMinY(), (double)z + this.getBlockBoundsMinZ(), (double)x + this.getBlockBoundsMaxX(), (double)y + this.getBlockBoundsMaxY(), (double)z + this.getBlockBoundsMaxZ());
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        return getBlockModel().traceCollision(this, world, x, y, z, start, end);
    }

    /**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    public boolean isVecYZContained(Vec3 p_149654_1_)
    {
        return p_149654_1_ == null ? false : p_149654_1_.yCoord >= this.minY && p_149654_1_.yCoord <= this.maxY && p_149654_1_.zCoord >= this.minZ && p_149654_1_.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    public boolean isVecXZContained(Vec3 p_149687_1_)
    {
        return p_149687_1_ == null ? false : p_149687_1_.xCoord >= this.minX && p_149687_1_.xCoord <= this.maxX && p_149687_1_.zCoord >= this.minZ && p_149687_1_.zCoord <= this.maxZ;
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    public boolean isVecXYContained(Vec3 p_149661_1_)
    {
        return p_149661_1_ == null ? false : p_149661_1_.xCoord >= this.minX && p_149661_1_.xCoord <= this.maxX && p_149661_1_.yCoord >= this.minY && p_149661_1_.yCoord <= this.maxY;
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity)
    {
        getBlockModel().collectCollisionBoxes(this, world, x, y, z, mask, list, entity);
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
        ItemStack result = new ItemStack(this, 1, this.getSubBlock(world,x,y,z).getMetadata());

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

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        super.updateTick(world, x, y, z, random);

        for(IBlockTickBehavior behavior : blockTickBehaviors) {
            behavior.blockUpdateTick(this, world, x, y, z, random);
        }
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        for(INeighborUpdateBehavior behavior : neighborUpdateBehaviors) {
            behavior.neighborUpdated(this, world, x, y, z, neighbor);
        }
    }
}
