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

package net.technic.technicblocks.client.texturing;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;

public class HashRandomTextureSelector extends TextureSelector {

    private int iconCount;
    private String iconPath;
    private IIcon[] allIcons;

    public HashRandomTextureSelector(String[] args) {
        if(args.length < 2)
            throw TechnicBlocks.getProxy().createParseException("Texture selectors of type 'random' require at least two arguments: a number indicating how many icons there are, and a resource path containing the full icon list.");

        try {
            iconCount = Integer.parseInt(args[0]);
            allIcons = new IIcon[iconCount];
        } catch (NumberFormatException ex) {
            throw TechnicBlocks.getProxy().createParseException("The first argument of 'random' texture selector was '"+args[0]+"' instead of a valid number of icons.", ex);
        }

        iconPath = args[1];
    }

    @Override
    public void registerIcons(IIconRegister register) {
        for (int i = 0; i < iconCount; i++) {
            allIcons[i] = register.registerIcon(iconPath+Integer.toString(i));
        }
    }

    @Override
    public IIcon selectTexture(DataDrivenBlock block, BlockTextureScheme textureScheme, IBlockAccess world, int x, int y, int z, ForgeDirection side, ConnectionConvention connections) {
        int faceInt = side.ordinal();
        long n = 0x1c3764a30115L * x * (x + 0xbL) + 0x227c1adccd1dL * y * (y + 0xbL) + 0xe0d251c03ba5L * z * (z + 0xbL) + 0xa2fb1377aeb3L * faceInt * (faceInt + 0xbL);
        n = 0x5deece66dL * (n + x + y + z + faceInt) + 0xbL;

        return allIcons[(int)Math.abs(n % iconCount)];
    }

    @Override
    public IIcon selectDefaultTexture() {
        return allIcons[0];
    }
}
