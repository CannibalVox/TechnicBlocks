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

    private boolean isConnected(ConnectionConvention convention, ForgeDirection side) {
        Block adjacentBlock = world.getBlock(anchorX+side.offsetX, anchorY+side.offsetY, anchorZ+side.offsetZ);
        int thisMetadata = world.getBlockMetadata(anchorX, anchorY, anchorZ);
        int otherMetadata = world.getBlockMetadata(anchorX+side.offsetX, anchorY+side.offsetY, anchorZ+side.offsetZ);

        return convention.testConnection(anchorBlock, thisMetadata, adjacentBlock, otherMetadata);
    }

    @Override
    public void preDrawFace(ForgeDirection dir, boolean internal, float startX, float startY, float endX, float endY, RenderBlocks renderer, Tessellator tessellator) {
        int multiplierValue = anchorBlock.colorMultiplier(world, anchorX, anchorY, anchorZ);

        float colorR = (float)(multiplierValue >> 16 & 255) / 255.0F;
        float colorG = (float)(multiplierValue >> 8 & 255) / 255.0F;
        float colorB = (float)(multiplierValue & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float tempR = (colorR * 30.0F + colorG * 59.0F + colorB * 11.0F) / 100.0F;
            float tempG = (colorR * 30.0F + colorG * 70.0F) / 100.0F;
            float tempB = (colorR * 30.0F + colorB * 70.0F) / 100.0F;
            colorR = tempR;
            colorG = tempG;
            colorB = tempB;
        }

        if (Minecraft.isAmbientOcclusionEnabled() && anchorBlock.getLightValue() == 0)
            this.ambientOcclusionPreDraw(dir, internal, renderer, tessellator, startX, startY, endX, endY, colorR, colorG, colorB);
        else
            this.colorMultiplierPreDraw(dir, internal, renderer, tessellator, colorR, colorG, colorB);
    }

    private void ambientOcclusionPreDraw(ForgeDirection face, boolean internal, RenderBlocks renderBlocks, Tessellator tessellator, float startX, float startY, float endX, float endY, float r, float g, float b) {
        renderBlocks.enableAO = true;
        int mixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX, anchorY, anchorZ);
        tessellator.setBrightness(983055);

        int adjacentX = 0;
        int adjacentY = 0;
        int adjacentZ = 0;

        if (!internal) {
            adjacentX = face.offsetX;
            adjacentY = face.offsetY;
            adjacentZ = face.offsetZ;
        }

        ForgeDirection xDir = textureScheme.getAxisSide(face, 1, 0);
        ForgeDirection yDir = textureScheme.getAxisSide(face, 0, 1);

        int westMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX - xDir.offsetX + adjacentX, anchorY - xDir.offsetY + adjacentY, anchorZ - xDir.offsetZ + adjacentZ);
        int eastMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX + xDir.offsetX + adjacentX, anchorY + xDir.offsetY + adjacentY, anchorZ + xDir.offsetZ + adjacentZ);
        int northMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX - yDir.offsetX + adjacentX, anchorY - yDir.offsetY + adjacentY, anchorZ - yDir.offsetZ + adjacentZ);
        int southMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX + yDir.offsetX + adjacentX, anchorY + yDir.offsetY + adjacentY, anchorZ + yDir.offsetZ + adjacentZ);

        float westLightValue = world.getBlock(anchorX - xDir.offsetX + adjacentX, anchorY - xDir.offsetY + adjacentY, anchorZ - xDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        float eastLightValue = world.getBlock(anchorX + xDir.offsetX + adjacentX, anchorY + xDir.offsetY + adjacentY, anchorZ + xDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        float northLightValue = world.getBlock(anchorX - yDir.offsetX + adjacentX, anchorY - yDir.offsetY + adjacentY, anchorZ - yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        float southLightValue = world.getBlock(anchorX + yDir.offsetX + adjacentX, anchorY + yDir.offsetY + adjacentY, anchorZ + yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();

        boolean westTransmitsLight = world.getBlock(anchorX - xDir.offsetX + face.offsetX + adjacentX, anchorY - xDir.offsetY + face.offsetY + adjacentY, anchorZ - xDir.offsetZ + face.offsetZ + adjacentZ).getCanBlockGrass();
        boolean eastTransmitsLight = world.getBlock(anchorX + xDir.offsetX + face.offsetX + adjacentX, anchorY + xDir.offsetY + face.offsetY + adjacentY, anchorZ + xDir.offsetZ + face.offsetZ + adjacentZ).getCanBlockGrass();
        boolean northTransmitsLight = world.getBlock(anchorX - yDir.offsetX + face.offsetX + adjacentX, anchorY - yDir.offsetY + face.offsetY + adjacentY, anchorZ - yDir.offsetZ + face.offsetZ + adjacentZ).getCanBlockGrass();
        boolean southTransmitsLight = world.getBlock(anchorX + yDir.offsetX + face.offsetX + adjacentX, anchorY + yDir.offsetY + face.offsetY + adjacentY, anchorZ + yDir.offsetZ + face.offsetZ + adjacentZ).getCanBlockGrass();

        int northwestMixedBrightness = westMixedBrightness;
        int southwestMixedBrightness = westMixedBrightness;
        int northeastMixedBrightness = eastMixedBrightness;
        int southeastMixedBrightness = eastMixedBrightness;

        float northwestLightValue = westLightValue;
        float southwestLightValue = westLightValue;
        float northeastLightValue = eastLightValue;
        float southeastLightValue = eastLightValue;

        if (westTransmitsLight || northTransmitsLight) {
            northwestMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX - xDir.offsetX - yDir.offsetX + adjacentX, anchorY - xDir.offsetY - yDir.offsetY + adjacentY, anchorZ - xDir.offsetZ - yDir.offsetZ + adjacentZ);
            northwestLightValue = world.getBlock(anchorX - xDir.offsetX - yDir.offsetX + adjacentX, anchorY - xDir.offsetY - yDir.offsetY + adjacentY, anchorZ - xDir.offsetZ - yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        }

        if (westTransmitsLight || southTransmitsLight) {
            southwestMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX - xDir.offsetX + yDir.offsetX + adjacentX, anchorY - xDir.offsetY + yDir.offsetY + adjacentY, anchorZ - xDir.offsetZ + yDir.offsetZ + adjacentZ);
            southwestLightValue = world.getBlock(anchorX - xDir.offsetX + yDir.offsetX + adjacentX, anchorY - xDir.offsetY + yDir.offsetY + adjacentY, anchorZ - xDir.offsetZ + yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        }

        if (eastTransmitsLight || northTransmitsLight) {
            northeastMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX + xDir.offsetX - yDir.offsetX + adjacentX, anchorY + xDir.offsetY - yDir.offsetY + adjacentY, anchorZ + xDir.offsetZ - yDir.offsetZ + adjacentZ);
            northeastLightValue = world.getBlock(anchorX + xDir.offsetX - yDir.offsetX + adjacentX, anchorY + xDir.offsetY - yDir.offsetY + adjacentY, anchorZ + xDir.offsetZ - yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        }

        if (eastTransmitsLight || southTransmitsLight) {
            southeastMixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX + xDir.offsetX + yDir.offsetX + adjacentX, anchorY + xDir.offsetY + yDir.offsetY + adjacentY, anchorZ + xDir.offsetZ + yDir.offsetZ + adjacentZ);
            southeastLightValue = world.getBlock(anchorX + xDir.offsetX + yDir.offsetX + adjacentX, anchorY + xDir.offsetY + yDir.offsetY + adjacentY, anchorZ + xDir.offsetZ + yDir.offsetZ + adjacentZ).getAmbientOcclusionLightValue();
        }

        if (!internal || !world.getBlock(anchorX + face.offsetX, anchorY + face.offsetY,anchorZ + face.offsetZ).isOpaqueCube())
        {
            mixedBrightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX + face.offsetX, anchorY + face.offsetY,anchorZ + face.offsetZ);
        }

        float adjacentLightValue = world.getBlock(anchorX + face.offsetX, anchorY + face.offsetY, anchorZ + face.offsetZ).getAmbientOcclusionLightValue();

        float southwestLightAverage = (southLightValue + westLightValue + southwestLightValue + adjacentLightValue) / 4.0f;
        float northwestLightAverage = (northLightValue + westLightValue + northwestLightValue + adjacentLightValue) / 4.0f;
        float southeastLightAverage = (southLightValue + eastLightValue + southeastLightValue + adjacentLightValue) / 4.0f;
        float northeastLightAverage = (northLightValue + eastLightValue + northeastLightValue + adjacentLightValue) / 4.0f;

        float mixedSouthwestLight = southwestLightAverage;
        float mixedNorthwestLight = northwestLightAverage;
        float mixedSoutheastLight = southeastLightAverage;
        float mixedNortheastLight = northeastLightAverage;

        if (face != ForgeDirection.DOWN && face != ForgeDirection.UP) {
            mixedSouthwestLight = (northwestLightAverage * (1.0f - startX) * (1.0f - endY)) + (northeastLightAverage * startX * (1.0f - endY)) + (southwestLightAverage * (1.0f - startX) * endY) + (southeastLightAverage * startX * endY);
            mixedNorthwestLight = (northwestLightAverage * (1.0f - startX) * (1.0f - startY)) + (northeastLightAverage * startX * (1.0f - startY)) + (southwestLightAverage * (1.0f - startX) * startY) + (southeastLightAverage * startX * startY);
            mixedSoutheastLight = (northwestLightAverage * (1.0f - endX) * (1.0f - endY)) + (northeastLightAverage * endX * (1.0f - endY)) + (southwestLightAverage * (1.0f - endX) * endY) + (southeastLightAverage * endX  * endY);
            mixedNortheastLight = (northwestLightAverage * (1.0f - endX) * (1.0f - startY)) + (northeastLightAverage * endX * (1.0f - startY)) + (southwestLightAverage * (1.0f - endX) * startY) + (southeastLightAverage * endX * startY);
        }

        int brightnessBottomLeft = renderBlocks.getAoBrightness(southwestMixedBrightness, westMixedBrightness, southMixedBrightness, mixedBrightness);
        int brightnessTopLeft = renderBlocks.getAoBrightness(westMixedBrightness, northwestMixedBrightness, northMixedBrightness, mixedBrightness);
        int brightnessTopRight = renderBlocks.getAoBrightness(northMixedBrightness, eastMixedBrightness, northeastMixedBrightness, mixedBrightness);
        int brightnessBottomRight = renderBlocks.getAoBrightness(southMixedBrightness, southeastMixedBrightness, eastMixedBrightness, mixedBrightness);
        renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight, (1.0f - startX) * (1.0f - startY), startX * (1.0f - startY), (1.0f - startX) * startY, startX * startY);
        renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight, (1.0f - endX) * (1.0f - startY), endX * (1.0f - startY), (1.0f - endX) * startY, endX * startY);
        renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight, (1.0f - startX) * (1.0f - endY), startX * (1.0f - endY), (1.0f - startX) * endY, startX * endY);
        renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight, (1.0f - endX) * (1.0f - endY), endX * (1.0f - endY), (1.0f - endX) * endY, endX * endY);

        Vector3f color = getBaseColorBySide(face, r, g, b);
        renderBlocks.colorRedTopLeft = mixedNorthwestLight * color.x;
        renderBlocks.colorBlueTopLeft = mixedNorthwestLight * color.y;
        renderBlocks.colorGreenTopLeft = mixedNorthwestLight * color.z;
        renderBlocks.colorRedTopRight = mixedNortheastLight * color.x;
        renderBlocks.colorGreenTopRight = mixedNortheastLight * color.y;
        renderBlocks.colorBlueTopRight = mixedNortheastLight * color.z;
        renderBlocks.colorRedBottomLeft = mixedSouthwestLight * color.x;
        renderBlocks.colorGreenBottomLeft = mixedSouthwestLight * color.y;
        renderBlocks.colorBlueBottomLeft = mixedSouthwestLight * color.z;
        renderBlocks.colorRedBottomRight = mixedSoutheastLight * color.x;
        renderBlocks.colorGreenBottomRight = mixedSoutheastLight * color.y;
        renderBlocks.colorBlueBottomRight = mixedSoutheastLight * color.z;
    }

    private void colorMultiplierPreDraw(ForgeDirection dir, boolean internal, RenderBlocks renderBlocks, Tessellator tessellator, float r, float g, float b) {
        renderBlocks.enableAO = false;

        int brightness = 0;
        if (internal) {
            brightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX, anchorY, anchorZ);
        } else {
            brightness = anchorBlock.getMixedBrightnessForBlock(world, anchorX+dir.offsetX, anchorY+dir.offsetY, anchorZ+dir.offsetZ);
        }
        tessellator.setBrightness(brightness);

        Vector3f color = getBaseColorBySide(dir, r, g, b);

        tessellator.setColorOpaque_F(color.x, color.y, color.z);
    }


    private Vector3f getBaseColorBySide(ForgeDirection side, float r, float g, float b) {
        switch(side) {
            case UP:
                return new Vector3f(r, g, b);
            case DOWN:
                return new Vector3f(r*0.5f, g*0.5f, b*0.5f);
            case NORTH:
            case SOUTH:
                return new Vector3f(r*0.8f, g*0.8f, b*0.8f);
            default:
                return new Vector3f(r*0.6f, g*0.6f, b*0.6f);
        }
    }

    @Override
    public void postDrawFace(RenderBlocks renderer, Tessellator tessellator) {
        if (renderer.enableAO)
            renderer.enableAO = false;
    }
}
