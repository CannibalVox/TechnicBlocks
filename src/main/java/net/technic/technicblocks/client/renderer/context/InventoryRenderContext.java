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

package net.technic.technicblocks.client.renderer.context;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.util.vector.Vector3f;

public class InventoryRenderContext implements IRenderContext {

    private DataDrivenBlock block;
    private int metadata;

    public InventoryRenderContext(DataDrivenBlock block, int metadata) {
        this.metadata = metadata;
        this.block = block;
    }

    @Override
    public boolean isModelConnected(ForgeDirection side) {
        return false;
    }

    @Override
    public ItemStack getConnectedBlock(ForgeDirection side) {
        return null;
    }

    @Override
    public boolean isTextureConnected(ForgeDirection side, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public boolean isFaceVisible(ForgeDirection side) {
        return true;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int rotations) {
        return block.getIcon(side.ordinal(), metadata);
    }

    @Override
    public Vector3f getTopLeft(ForgeDirection side) {
        return block.getSubBlock(metadata).getTextureScheme().getTopLeft(side);
    }

    @Override
    public BlockTextureScheme getTextureScheme() {
        return null;
    }

    @Override
    public int getColorMultiplier() {
        return 0xFFFFFF;
    }

    @Override
    public int getLightValue() {
        return block.getLightValue();
    }

    @Override
    public int getMixedBrightness() {
        return 0;
    }

    @Override
    public int getAdjacentMixedBrightness(ForgeDirection... adjacencies) {
        return 0;
    }

    @Override
    public int getAdjacentMixedBrightness(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        return 0;
    }

    @Override
    public float getAmbientLightValue() {
        return block.getAmbientOcclusionLightValue();
    }

    @Override
    public float getAdjacentAmbientLightValue(ForgeDirection... adjacencies) {
        return 0;
    }

    @Override
    public float getAdjacentAmbientLightValue(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        return 0;
    }

    @Override
    public boolean canTransmitLight() {
        return block.getCanBlockGrass();
    }

    @Override
    public boolean canAdjacentTransmitLight(ForgeDirection... adjacencies) {
        return false;
    }

    @Override
    public boolean canAdjacentTransmitLight(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return block.isOpaqueCube();
    }

    @Override
    public boolean isAdjacentOpaque(ForgeDirection... adjacencies) {
        return false;
    }

    @Override
    public boolean isAdjacentOpaque(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        return false;
    }
}
