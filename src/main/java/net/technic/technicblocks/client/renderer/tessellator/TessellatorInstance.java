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

package net.technic.technicblocks.client.renderer.tessellator;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.util.vector.Vector3f;

import javax.vecmath.Vector2f;
import java.util.ArrayList;
import java.util.List;

public class TessellatorInstance {
    private final Tessellator tessellator;
    private final net.minecraft.client.renderer.Tessellator minecraftTessellator;
    private final RenderBlocks renderer;

    public TessellatorInstance(Tessellator tessellator, net.minecraft.client.renderer.Tessellator minecraftTessellator, RenderBlocks renderer) {
        this.tessellator = tessellator;
        this.minecraftTessellator = minecraftTessellator;
        this.renderer = renderer;
    }

    public void renderFace(ForgeDirection face, float startX, float startY, float endX, float endY, float depth, BlockTextureScheme textureScheme, IRenderContext posContext, ForgeDirection upDir) {
        IIcon icon = posContext.getTexture(face);
        Vector3f topLeft = posContext.getTopLeft(face);

        ForgeDirection xDir = textureScheme.getAxisSide(face, 1, 0);
        ForgeDirection yDir = textureScheme.getAxisSide(face, 0, 1);
        Vector3f xVec = new Vector3f(xDir.offsetX, xDir.offsetY, xDir.offsetZ);
        Vector3f yVec = new Vector3f(yDir.offsetX, yDir.offsetY, yDir.offsetZ);

        ForgeDirection intoDir = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[face.ordinal()]];
        Vector3f zVec = new Vector3f(intoDir.offsetX, intoDir.offsetY, intoDir.offsetZ);

        List<Vector2f> uvList = new ArrayList<Vector2f>(4);
        uvList.add(new Vector2f(icon.getInterpolatedU(16.0f * startX), icon.getInterpolatedV(16.0f * startY)));
        uvList.add(new Vector2f(icon.getInterpolatedU(16.0f * startX), icon.getInterpolatedV(16.0f * endY)));
        uvList.add(new Vector2f(icon.getInterpolatedU(16.0f * endX), icon.getInterpolatedV(16.0f * endY)));
        uvList.add(new Vector2f(icon.getInterpolatedU(16.0f * endX), icon.getInterpolatedV(16.0f * startY)));

        ForgeDirection realUpDir = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[yDir.ordinal()]];

        while (realUpDir != upDir) {
            rotateUvs(uvList);
            realUpDir = realUpDir.getRotation(face);
        }

        tessellator.getPrePostHandler().preDrawFace(posContext, face, depth > 0.0001f, startX, startY, endX, endY, renderer, minecraftTessellator);
        tesselateFace(icon, topLeft, xVec, yVec, zVec, startX, startY, endX, endY, depth, uvList);
        tessellator.getPrePostHandler().postDrawFace(renderer, minecraftTessellator);
    }

    private void rotateUvs(List<Vector2f> uvList) {
        Vector2f temp = uvList.get(0);
        uvList.set(0, uvList.get(3));
        uvList.set(3, uvList.get(2));
        uvList.set(2, uvList.get(1));
        uvList.set(1, temp);
    }

    private void tesselateFace(IIcon icon, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float startX, float startY, float endX, float endY, float depth, List<Vector2f> uvList) {
        float[] redWeights = { renderer.colorRedTopLeft, renderer.colorRedBottomLeft, renderer.colorRedBottomRight, renderer.colorRedTopRight };
        float[] greenWeights = { renderer.colorGreenTopLeft, renderer.colorGreenBottomLeft, renderer.colorGreenBottomRight, renderer.colorGreenTopRight };
        float[] blueWeights = { renderer.colorBlueTopLeft, renderer.colorBlueBottomLeft, renderer.colorBlueBottomRight, renderer.colorBlueTopRight };
        int[] brightnesses = { renderer.brightnessTopLeft, renderer.brightnessBottomLeft, renderer.brightnessBottomRight, renderer.brightnessTopRight };

        tessellator.getFaceHandler().renderFace(minecraftTessellator, icon, topLeft, xVec, yVec, zVec, startX, startY, endX, endY, depth, uvList, renderer.enableAO, redWeights, greenWeights, blueWeights, brightnesses);
    }
}
