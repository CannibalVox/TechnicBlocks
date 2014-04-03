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

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockBehavior {
    private int metadataBitRoot;

    public BlockBehavior(String[] args) {
    }

    public abstract boolean isMetadataReserved();
    public abstract int getMetadataBitSize();
    public void setMetadataBitRoot(int bitRoot) { metadataBitRoot = bitRoot; }
    public int getMetadataBitRoot() { return metadataBitRoot; }

    public int getMetadataMask() {
        int size = getMetadataBitSize();
        int root = getMetadataBitRoot();

        if (size == 0) return 0;

        int mask = 0;
        for (int i = 0; i < size; i++) {
            mask = (mask << 1) | 1;
        }

        mask <<= root;

        return mask;
    }

    protected int getMetadataValue(int metadata) {
        return getMetadataValueWithMask(metadata, getMetadataMask());
    }

    protected int getMetadataValueWithMask(int metadata, int mask) {
        if (mask == 0) return 0;

        int masked = (metadata & mask);

        return masked >> getMetadataBitRoot();
    }

    protected int setMetadataValue(int metadata, int value) {
        return setMetadataValueWithMask(metadata, value, getMetadataMask());
    }

    protected int setMetadataValueWithMask(int metadata, int value, int mask) {
        if (mask == 0) return metadata;

        int inverseMask = ~mask;
        int maskedValue = (metadata & inverseMask);
        int shiftedValue = value << getMetadataBitRoot();

        return (maskedValue | (shiftedValue & mask));
    }

    public ForgeDirection transformBlockFacing(int metadata, ForgeDirection currentFacing) {
        return currentFacing;
    }

    public ForgeDirection reverseTransformBlockFacing(int metadata, ForgeDirection currentFacing) {
        return currentFacing;
    }

    public boolean transformIsOnFloor(int metadata, boolean isOnFloor) {
        return isOnFloor;
    }
}
