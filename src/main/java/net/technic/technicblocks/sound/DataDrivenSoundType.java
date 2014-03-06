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

public class DataDrivenSoundType extends Block.SoundType {
    private SoundData soundData;

    public DataDrivenSoundType(SoundData soundData) {
        super("", 1.0f, 1.0f);
        this.soundData = soundData;
    }

    @Override
    public float getVolume() {
        return soundData.getVolume();
    }

    @Override
    public float getPitch() {
        return soundData.getPitch();
    }

    @Override
    public String getBreakSound() {
        return soundData.getBreakSound();
    }

    @Override
    public String getStepResourcePath() {
        return soundData.getStepSound();
    }

    @Override
    public String func_150496_b()
    {
        return soundData.getPlaceSound();
    }
}
