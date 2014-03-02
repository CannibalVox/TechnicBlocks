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

package net.technic.technicblocks.materials;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.technic.technicblocks.parser.data.MaterialData;

public class DataDrivenMaterial extends Material {

    private MaterialData materialData;

    public DataDrivenMaterial(MaterialData data) {
        super(MapColor.mapColorArray[data.getMapColorIndex()]);

        this.materialData = data;
    }

    @Override
    public boolean isLiquid() {
        return materialData.isLiquid();
    }

    @Override
    public boolean isSolid() {
        return materialData.isSolid();
    }

    @Override
    public boolean getCanBlockGrass() {
        return materialData.doesBlockGrass();
    }

    @Override
    public boolean blocksMovement() {
        return materialData.doesBlockMovement();
    }

    @Override
    public boolean getCanBurn() {
        return materialData.isFlammable();
    }

    @Override
    public boolean isReplaceable() {
        return materialData.isReplaceable();
    }

    @Override
    public boolean isOpaque() {
        return materialData.isOpaque();
    }

    @Override
    public boolean isToolNotRequired() {
        return !materialData.doesRequireTool();
    }

    @Override
    public int getMaterialMobility() {
        switch (materialData.getPistonBehavior()) {
            case shove:
                return 0;
            case destroy:
                return 1;
            case block:
                return 2;
        }

        return 0;
    }

    @Override
    public boolean isAdventureModeExempt() {
        return materialData.isAdventureModeExempt();
    }
}
