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

package net.technic.technicblocks.client.renderer.tessellator.preposthandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import org.lwjgl.util.vector.Vector3f;

public class WorldLightingHandler implements IPrePostFaceHandler {

    @Override
    public void preDrawFace(IRenderContext renderContext, ForgeDirection dir, boolean internal, float startX, float startY, float endX, float endY, RenderBlocks blocks, Tessellator tessellator, int rotations) {
        int multiplierValue = renderContext.getColorMultiplier();

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

        if (Minecraft.isAmbientOcclusionEnabled() && renderContext.getLightValue() == 0)
            this.ambientOcclusionPreDraw(renderContext, dir, internal, blocks, tessellator, startX, startY, endX, endY, colorR, colorG, colorB, rotations);
        else
            this.colorMultiplierPreDraw(renderContext, dir, internal, blocks, tessellator, colorR, colorG, colorB);
    }

    @Override
    public void postDrawFace(RenderBlocks blocks, Tessellator tessellator) {
        if (blocks.enableAO)
            blocks.enableAO = false;
    }

    protected void ambientOcclusionPreDraw(IRenderContext renderContext, ForgeDirection face, boolean internal, RenderBlocks renderBlocks, Tessellator tessellator, float startX, float startY, float endX, float endY, float r, float g, float b, int rotations) {
        renderBlocks.enableAO = true;
        int mixedBrightness = renderContext.getMixedBrightness();
        tessellator.setBrightness(983055);

        int adjacentX = 0;
        int adjacentY = 0;
        int adjacentZ = 0;

        if (!internal) {
            adjacentX = face.offsetX;
            adjacentY = face.offsetY;
            adjacentZ = face.offsetZ;
        }

        ForgeDirection xDir = renderContext.getTextureScheme().getAxisSide(face, 1, 0);
        ForgeDirection yDir = renderContext.getTextureScheme().getAxisSide(face, 0, 1);

        ForgeDirection xDirOpposite = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[xDir.ordinal()]];
        ForgeDirection yDirOpposite = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[yDir.ordinal()]];

        int westMixedBrightness = renderContext.getAdjacentMixedBrightness(xDirOpposite);
        int eastMixedBrightness = renderContext.getAdjacentMixedBrightness(xDir);
        int northMixedBrightness = renderContext.getAdjacentMixedBrightness(yDirOpposite);
        int southMixedBrightness = renderContext.getAdjacentMixedBrightness(yDir);

        float westLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDirOpposite);
        float eastLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDir);
        float northLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, yDirOpposite);
        float southLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, yDir);

        boolean westTransmitsLight = renderContext.canAdjacentTransmitLight(adjacentX, adjacentY, adjacentZ, xDirOpposite, face);
        boolean eastTransmitsLight = renderContext.canAdjacentTransmitLight(adjacentX, adjacentY, adjacentZ, xDir, face);
        boolean northTransmitsLight = renderContext.canAdjacentTransmitLight(adjacentX, adjacentY, adjacentZ, yDirOpposite, face);
        boolean southTransmitsLight = renderContext.canAdjacentTransmitLight(adjacentX, adjacentY, adjacentZ, yDir, face);

        int northwestMixedBrightness = westMixedBrightness;
        int southwestMixedBrightness = westMixedBrightness;
        int northeastMixedBrightness = eastMixedBrightness;
        int southeastMixedBrightness = eastMixedBrightness;

        float northwestLightValue = westLightValue;
        float southwestLightValue = westLightValue;
        float northeastLightValue = eastLightValue;
        float southeastLightValue = eastLightValue;

        if (westTransmitsLight || northTransmitsLight) {
            northwestMixedBrightness = renderContext.getAdjacentMixedBrightness(adjacentX, adjacentY, adjacentZ, xDirOpposite, yDirOpposite);
            northwestLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDirOpposite, yDirOpposite);
        }

        if (westTransmitsLight || southTransmitsLight) {
            southwestMixedBrightness = renderContext.getAdjacentMixedBrightness(adjacentX, adjacentY, adjacentZ, xDirOpposite, yDir);
            southwestLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDirOpposite, yDir);
        }

        if (eastTransmitsLight || northTransmitsLight) {
            northeastMixedBrightness = renderContext.getAdjacentMixedBrightness(adjacentX, adjacentY, adjacentZ, xDir, yDirOpposite);
            northeastLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDir, yDirOpposite);
        }

        if (eastTransmitsLight || southTransmitsLight) {
            southeastMixedBrightness = renderContext.getAdjacentMixedBrightness(adjacentX, adjacentY, adjacentZ, xDir, yDir);
            southeastLightValue = renderContext.getAdjacentAmbientLightValue(adjacentX, adjacentY, adjacentZ, xDir, yDir);
        }

        if (!internal || !renderContext.isAdjacentOpaque(face))
        {
            mixedBrightness = renderContext.getAdjacentMixedBrightness(face);
        }

        float adjacentLightValue = renderContext.getAdjacentAmbientLightValue(face);

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

        for (int i = 0; i < (rotations-4); i++) {
            int topLeft = renderBlocks.brightnessTopLeft;
            float redTopLeft = renderBlocks.colorRedTopLeft;
            float greenTopLeft = renderBlocks.colorGreenTopLeft;
            float blueTopLeft = renderBlocks.colorBlueTopLeft;

            renderBlocks.brightnessTopLeft = renderBlocks.brightnessTopRight;
            renderBlocks.colorRedTopLeft = renderBlocks.colorRedTopRight;
            renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenTopRight;
            renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueTopRight;

            renderBlocks.brightnessTopRight = renderBlocks.brightnessBottomRight;
            renderBlocks.colorRedTopRight = renderBlocks.colorRedBottomRight;
            renderBlocks.colorGreenTopRight = renderBlocks.colorGreenBottomRight;
            renderBlocks.colorBlueTopRight = renderBlocks.colorBlueBottomRight;

            renderBlocks.brightnessBottomRight = renderBlocks.brightnessBottomLeft;
            renderBlocks.colorRedBottomRight = renderBlocks.colorRedBottomLeft;
            renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenBottomLeft;
            renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueBottomLeft;

            renderBlocks.brightnessBottomLeft = topLeft;
            renderBlocks.colorRedBottomLeft = redTopLeft;
            renderBlocks.colorGreenBottomLeft = greenTopLeft;
            renderBlocks.colorBlueBottomLeft = blueTopLeft;
        }
    }

    protected void colorMultiplierPreDraw(IRenderContext renderContext, ForgeDirection dir, boolean internal, RenderBlocks renderBlocks, Tessellator tessellator, float r, float g, float b) {
        renderBlocks.enableAO = false;

        int brightness = 0;
        if (internal) {
            brightness = renderContext.getMixedBrightness();
        } else {
            brightness = renderContext.getAdjacentMixedBrightness(dir);
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
}
