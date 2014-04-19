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

public class DoubleSidedHandler extends SingleSidedHandler {
    @Override
    public void renderFace(Tessellator tessellator, IIcon icon, Vector3f topLeft, Vector3f xVec, Vector3f yVec, Vector3f zVec, float startX, float startY, float endX, float endY, float depth, List<Vector2f> uvList, boolean useAO, float[] redWeights, float[] greenWeights, float[] blueWeights, int[] brightnesses) {
        super.renderFace(tessellator, icon, topLeft, xVec, yVec, zVec, startX, startY, endX, endY, depth, uvList, useAO, redWeights, greenWeights, blueWeights, brightnesses);

        addVertex(tessellator, icon, topLeft, xVec, yVec, zVec, endX, startY, depth, uvList.get(3).x, uvList.get(3).y, useAO, redWeights[3], greenWeights[3], blueWeights[3], brightnesses[3]);
        addVertex(tessellator, icon, topLeft, xVec, yVec, zVec, endX, endY, depth, uvList.get(2).x, uvList.get(2).y, useAO, redWeights[2], greenWeights[2], blueWeights[2], brightnesses[2]);
        addVertex(tessellator, icon, topLeft, xVec, yVec, zVec, startX, endY, depth, uvList.get(1).x, uvList.get(1).y, useAO, redWeights[1], greenWeights[1], blueWeights[1], brightnesses[1]);
        addVertex(tessellator, icon, topLeft, xVec, yVec, zVec, startX, startY, depth, uvList.get(0).x, uvList.get(0).y, useAO, redWeights[0], greenWeights[0], blueWeights[0], brightnesses[0]);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
