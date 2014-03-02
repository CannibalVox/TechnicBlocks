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

import net.minecraft.block.material.Material;
import net.technic.technicblocks.parser.data.MaterialData;

import java.util.HashMap;

public class MaterialFactory {
    private HashMap<String, Material> materials = new HashMap<String, Material>();

    public MaterialFactory() {
        materials.put("air",Material.air);
        materials.put("grass",Material.grass);
        materials.put("ground",Material.ground);
        materials.put("wood",Material.wood);
        materials.put("rock",Material.rock);
        materials.put("iron",Material.iron);
        materials.put("anvil",Material.anvil);
        materials.put("water",Material.water);
        materials.put("lava",Material.lava);
        materials.put("leaves",Material.leaves);
        materials.put("plants",Material.plants);
        materials.put("vine",Material.vine);
        materials.put("sponge",Material.sponge);
        materials.put("cloth",Material.cloth);
        materials.put("fire",Material.fire);
        materials.put("sand",Material.sand);
        materials.put("circuits",Material.circuits);
        materials.put("carpet",Material.carpet);
        materials.put("glass",Material.glass);
        materials.put("redstoneLight", Material.redstoneLight);
        materials.put("tnt",Material.tnt);
        materials.put("coral",Material.coral);
        materials.put("ice",Material.ice);
        materials.put("packedIce",Material.packedIce);
        materials.put("snow",Material.snow);
        materials.put("craftedSnow", Material.craftedSnow);
        materials.put("cactus", Material.cactus);
        materials.put("clay", Material.clay);
        materials.put("gourd", Material.gourd);
        materials.put("dragonEgg", Material.dragonEgg);
        materials.put("portal", Material.portal);
        materials.put("cake", Material.cake);
        materials.put("web", Material.web);
    }

    public void addMaterial(MaterialData data) {
        materials.put(data.getMaterialName(), new DataDrivenMaterial(data));
    }

    public Material getMaterialByName(String name) {
        if (materials.containsKey(name))
            return materials.get(name);
        else
            return null;
    }
}
