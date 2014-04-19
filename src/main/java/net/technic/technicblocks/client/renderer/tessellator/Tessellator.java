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
import net.technic.technicblocks.client.renderer.tessellator.facehandlers.FaceHandler;
import net.technic.technicblocks.client.renderer.tessellator.preposthandlers.IPrePostFaceHandler;

public class Tessellator {
    private IPrePostFaceHandler prePostHandler;
    private FaceHandler faceHandler;

    public Tessellator(IPrePostFaceHandler prePostHandler, FaceHandler faceHandler) {
        this.prePostHandler = prePostHandler;
        this.faceHandler = faceHandler;
    }

    public TessellatorInstance getInstance(RenderBlocks renderer) {
        return new TessellatorInstance(this, net.minecraft.client.renderer.Tessellator.instance, renderer);
    }

    public IPrePostFaceHandler getPrePostHandler() {
        return prePostHandler;
    }
    public FaceHandler getFaceHandler() { return faceHandler; }
}
