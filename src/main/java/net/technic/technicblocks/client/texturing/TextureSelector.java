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
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
public abstract class TextureSelector {

    public void registerIcons(BlockTextureScheme textureScheme, IIconRegister register) { registerIcons(textureScheme, register, null); }
    public abstract void registerIcons(BlockTextureScheme textureScheme, IIconRegister register, String decoratorTexture);

    public abstract String selectTexture(DataDrivenBlock block, BlockTextureScheme textureScheme, IBlockAccess world, int x, int y, int z, ForgeDirection side, ConnectionConvention connections, int rotations);
    public abstract String selectDefaultTexture();
}
