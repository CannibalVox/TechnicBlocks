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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityConvention;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class WorldRenderContext implements IRenderContext {

    private ConnectionConvention modelConvention;
    private FaceVisibilityConvention visibilityConvention;
    private DataDrivenBlock anchorBlock;
    private BlockTextureScheme textureScheme;

    private IBlockAccess world;
    private int anchorX;
    private int anchorY;
    private int anchorZ;

    private Map<ForgeDirection, Boolean> textureConnectionCache = new HashMap<ForgeDirection, Boolean>();

    public WorldRenderContext(DataDrivenBlock block, DataDrivenSubBlock subBlock, IBlockAccess world, int x, int y, int z) {
        this.anchorBlock = block;
        this.modelConvention = block.getBlockModel().getModelConnectionsConvention();
        this.visibilityConvention = block.getBlockModel().getFaceVisibilityConvention();
        this.textureScheme = subBlock.getTextureScheme();
        this.world = world;
        this.anchorX = x;
        this.anchorY = y;
        this.anchorZ = z;
    }

    @Override
    public boolean isModelConnected(ForgeDirection side) {
        return isConnected(modelConvention, side);
    }

    @Override
    public ItemStack getConnectedBlock(ForgeDirection side) {
        if (isModelConnected(side)) {
            Block block = world.getBlock(anchorX + side.offsetX, anchorY + side.offsetY, anchorZ + side.offsetZ);
            int meta = world.getBlockMetadata(anchorX+side.offsetX, anchorY+side.offsetY, anchorZ+side.offsetZ);

            return new ItemStack(block, 1, meta);
        }

        return null;
    }

    @Override
    public boolean isFaceVisible(ForgeDirection side) {
        return visibilityConvention.isSideVisible(world, anchorX, anchorY, anchorZ, side, this);
    }

    @Override
    public IIcon getTexture(ForgeDirection face) {
        return anchorBlock.getIcon(world, anchorX, anchorY, anchorZ, face.ordinal());
    }

    @Override
    public boolean isTextureConnected(ForgeDirection side, int relativeX, int relativeY) {
        ForgeDirection realSide = textureScheme.getAxisSide(side, relativeX, relativeY);

        if (textureConnectionCache.containsKey(realSide))
            return textureConnectionCache.get(realSide);

        boolean result = isConnected(textureScheme.getConnectionConvention(), realSide);

        textureConnectionCache.put(realSide, result);

        return result;
    }

    @Override
    public Vector3f getTopLeft(ForgeDirection side) {
        Vector3f topLeft  = textureScheme.getTopLeft(side);

        topLeft.x += anchorX;
        topLeft.y += anchorY;
        topLeft.z += anchorZ;

        return topLeft;
    }

    @Override
    public BlockTextureScheme getTextureScheme() {
        return textureScheme;
    }

    @Override
    public int getColorMultiplier() {
        return anchorBlock.colorMultiplier(world, anchorX, anchorY, anchorZ);
    }

    @Override
    public int getLightValue() {
        return anchorBlock.getLightValue(world, anchorX, anchorY, anchorZ);
    }

    @Override
    public int getMixedBrightness() {
        return anchorBlock.getMixedBrightnessForBlock(world, anchorX, anchorY, anchorZ);
    }

    private void adjacencyProcess(int[] coords, ForgeDirection[] adjacencies) {
        for (int i = 0; i < adjacencies.length; i++) {
            coords[0] += adjacencies[i].offsetX;
            coords[1] += adjacencies[i].offsetY;
            coords[2] += adjacencies[i].offsetZ;
        }
    }

    @Override
    public int getAdjacentMixedBrightness(ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX, anchorY, anchorZ};
        adjacencyProcess(coords, adjacencies);

        return anchorBlock.getMixedBrightnessForBlock(world, coords[0], coords[1], coords[2]);
    }

    @Override
    public int getAdjacentMixedBrightness(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX+manualOffsetX, anchorY+manualOffsetY, anchorZ+manualOffsetZ};
        adjacencyProcess(coords, adjacencies);

        return anchorBlock.getMixedBrightnessForBlock(world, coords[0], coords[1], coords[2]);
    }

    @Override
    public float getAmbientLightValue() {
        return world.getBlock(anchorX, anchorY, anchorZ).getAmbientOcclusionLightValue();
    }

    @Override
    public float getAdjacentAmbientLightValue(ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX, anchorY, anchorZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).getAmbientOcclusionLightValue();
    }

    @Override
    public float getAdjacentAmbientLightValue(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX+manualOffsetX, anchorY+manualOffsetY, anchorZ+manualOffsetZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).getAmbientOcclusionLightValue();
    }

    @Override
    public boolean canTransmitLight() {
        return anchorBlock.getCanBlockGrass();
    }

    @Override
    public boolean canAdjacentTransmitLight(ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX, anchorY, anchorZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).getCanBlockGrass();
    }

    @Override
    public boolean canAdjacentTransmitLight(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX+manualOffsetX, anchorY+manualOffsetY, anchorZ+manualOffsetZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).getCanBlockGrass();
    }

    @Override
    public boolean isOpaque() {
        return anchorBlock.isOpaqueCube();
    }

    @Override
    public boolean isAdjacentOpaque(ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX, anchorY, anchorZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).isOpaqueCube();
    }

    @Override
    public boolean isAdjacentOpaque(int manualOffsetX, int manualOffsetY, int manualOffsetZ, ForgeDirection... adjacencies) {
        int[] coords = new int[] { anchorX+manualOffsetX, anchorY+manualOffsetY, anchorZ+manualOffsetZ};
        adjacencyProcess(coords, adjacencies);

        return world.getBlock(coords[0], coords[1], coords[2]).isOpaqueCube();
    }

    private boolean isConnected(ConnectionConvention convention, ForgeDirection side) {
        Block adjacentBlock = world.getBlock(anchorX+side.offsetX, anchorY+side.offsetY, anchorZ+side.offsetZ);
        int thisMetadata = world.getBlockMetadata(anchorX, anchorY, anchorZ);
        int otherMetadata = world.getBlockMetadata(anchorX+side.offsetX, anchorY+side.offsetY, anchorZ+side.offsetZ);

        return convention.testConnection(anchorBlock, thisMetadata, adjacentBlock, otherMetadata);
    }
}
