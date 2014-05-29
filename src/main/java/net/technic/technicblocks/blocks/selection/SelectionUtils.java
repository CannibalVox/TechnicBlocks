package net.technic.technicblocks.blocks.selection;

import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;

public class SelectionUtils {

    protected void trimDirection(DataDrivenBlock block, ForgeDirection direction) {
        trimDirection(block, direction, 0.5f);
    }

    protected void trimDirection(DataDrivenBlock block, ForgeDirection direction, float trimAmount) {
        float startX = (float)block.getBlockBoundsMinX();
        float startY = (float)block.getBlockBoundsMinY();
        float startZ = (float)block.getBlockBoundsMinZ();
        float endX = (float)block.getBlockBoundsMaxX();
        float endY = (float)block.getBlockBoundsMaxY();
        float endZ = (float)block.getBlockBoundsMaxZ();

        float modX = direction.offsetX * trimAmount;
        float modY = direction.offsetY * trimAmount;
        float modZ = direction.offsetZ * trimAmount;

        if (modX > 0)
            startX += modX;
        else
            endX += modX;

        if (modY > 0)
            startY += modY;
        else
            endY += modY;

        if (modZ > 0)
            startZ += modZ;
        else
            endZ += modZ;

        block.setBlockBounds(startX, startY, startZ, endX, endY, endZ);
    }
}
