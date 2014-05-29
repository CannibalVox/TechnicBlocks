package net.technic.technicblocks.blocks.connections;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;

public class SameOrientationConvention extends ConnectionConvention {
    public SameOrientationConvention(String[] args) {
        super(args);
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, Block otherBlock, int otherMetadata) {
        return false;
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, DataDrivenBlock otherBlock, int otherMetadata) {
        return thisBlock.reverseTransformBlockFacing(thisMetadata, ForgeDirection.UP) == otherBlock.reverseTransformBlockFacing(otherMetadata, ForgeDirection.UP) &&
                thisBlock.reverseTransformBlockFacing(thisMetadata, ForgeDirection.NORTH) == otherBlock.reverseTransformBlockFacing(otherMetadata, ForgeDirection.NORTH);
    }
}
