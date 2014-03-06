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

package net.technic.technicblocks.sound;

import net.minecraft.block.Block;
import net.technic.technicblocks.parser.data.SoundData;

import java.util.HashMap;
import java.util.Map;

public class SoundTypeFactory {
    Map<String, Block.SoundType> sounds = new HashMap<String, Block.SoundType>();

    public SoundTypeFactory() {
        sounds.put("stone", Block.soundTypeStone);
        sounds.put("metal", Block.soundTypeMetal);
        sounds.put("wood", Block.soundTypeWood);
        sounds.put("gravel", Block.soundTypeGravel);
        sounds.put("grass", Block.soundTypeGrass);
        sounds.put("piston", Block.soundTypePiston);
        sounds.put("glass", Block.soundTypeGlass);
        sounds.put("cloth", Block.soundTypeCloth);
        sounds.put("sand", Block.soundTypeSand);
        sounds.put("snow", Block.soundTypeSnow);
        sounds.put("ladder", Block.soundTypeLadder);
        sounds.put("anvil", Block.soundTypeAnvil);
    }

    public void addSoundType(SoundData sound) {
        sounds.put(sound.getName(), new DataDrivenSoundType(sound));
    }

    public Block.SoundType getSoundByName(String name) {
        if (sounds.containsKey(name))
            return sounds.get(name);
        else
            return null;
    }
}
