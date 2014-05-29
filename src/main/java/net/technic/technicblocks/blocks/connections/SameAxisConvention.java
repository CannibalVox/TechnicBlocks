package net.technic.technicblocks.blocks.connections;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;

public class SameAxisConvention extends ConnectionConvention {
    public SameAxisConvention(String[] args) {
        super(args);
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, Block otherBlock, int otherMetadata) {
        return false;
    }

    @Override
    protected boolean checkConvention(DataDrivenBlock thisBlock, int thisMetadata, DataDrivenBlock otherBlock, int otherMetadata) {
        ForgeDirection thisDir = thisBlock.reverseTransformBlockFacing(thisMetadata, ForgeDirection.UP);
        ForgeDirection otherDir = otherBlock.reverseTransformBlockFacing(otherMetadata, ForgeDirection.UP);

        return thisDir == otherDir || thisDir == ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[otherDir.ordinal()]];
    }
}
