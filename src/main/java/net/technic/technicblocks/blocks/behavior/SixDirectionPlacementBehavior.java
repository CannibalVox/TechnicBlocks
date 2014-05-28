package net.technic.technicblocks.blocks.behavior;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.behavior.functions.IBlockPlacementBehavior;

public class SixDirectionPlacementBehavior extends BlockBehavior implements IBlockPlacementBehavior {

    public SixDirectionPlacementBehavior(String[] args) {
        super(args);
    }

    @Override
    public boolean isMetadataReserved() {
        return true;
    }

    @Override
    public int getMetadataBitSize() {
        return 3;
    }

    @Override
    public ForgeDirection transformBlockFacing(int metadata, ForgeDirection currentFacing) {
        int value = getMetadataValue(metadata);

        if (value == 0)
            return currentFacing;

        if ((value & 4) != 0) {
            int topRotations = (value & 3);

            while (topRotations > 0) {
                currentFacing = currentFacing.getRotation(ForgeDirection.DOWN);
                topRotations--;
            }
        }

        currentFacing = currentFacing.getRotation(ForgeDirection.WEST);

        if (value == 1) {
            currentFacing = currentFacing.getRotation(ForgeDirection.WEST);
        }

        return currentFacing;
    }

    @Override
    public ForgeDirection reverseTransformBlockFacing(int metadata, ForgeDirection currentFacing) {
        int value = getMetadataValue(metadata);

        if (value == 0)
            return currentFacing;

        currentFacing = currentFacing.getRotation(ForgeDirection.WEST);

        if ((value & 4) == 0) {
            return currentFacing.getRotation(ForgeDirection.WEST);
        }

        int topRotations = (value & 3);

        while (topRotations > 0) {
            currentFacing = currentFacing.getRotation(ForgeDirection.UP);
            topRotations--;
        }

        return currentFacing;
    }

    @Override
    public int transformPlacementMetadata(DataDrivenBlock block, World world, int x, int y, int z, ForgeDirection side, float hitX, float hitY, float hitZ, int metadata) {
        int value;

        switch(side) {
            case SOUTH:
                value = 4;
                break;
            case WEST:
                value = 5;
                break;
            case NORTH:
                value = 6;
                break;
            case EAST:
                value = 7;
                break;
            case DOWN:
                value = 1;
                break;
            default:
                value = 0;
        }

        return setMetadataValue(metadata, value);
    }

    @Override
    public void triggerBlockPlacement(DataDrivenBlock block, World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {
    }
}
