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

package net.technic.technicblocks.client.renderer.tessellator.facehandlers;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import org.lwjgl.util.vector.Vector3f;

import javax.vecmath.Vector2f;
import java.util.List;

public abstract class FaceHandler {
    public abstract void renderFace(Tessellator tessellator, IIcon icon, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float startX, float startY, float endX, float endY, float depth, List<Vector2f> uvList, boolean useAO, float[] redWeights, float[] greenWeights, float[] blueWeights, int[] brightnesses);
    public abstract boolean isOpaqueCube();

    protected void addVertex(Tessellator minecraftTessellator, IIcon icon, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float x, float y, float z, float u, float v, boolean useAO, float red, float green, float blue, int brightness) {
        if (useAO) {
            minecraftTessellator.setColorOpaque_F(red, green, blue);
            minecraftTessellator.setBrightness(brightness);
        }

        double vertX = topLeft.x + (xVec.x * x) + (yVec.x * y) + (zVec.x * z);
        double vertY = topLeft.y + (xVec.y * x) + (yVec.y * y) + (zVec.y * z);
        double vertZ = topLeft.z + (xVec.z * x) + (yVec.z * y) + (zVec.z * z);

        minecraftTessellator.addVertexWithUV(vertX, vertY, vertZ, u, v);
    }
}
