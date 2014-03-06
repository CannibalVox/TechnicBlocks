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
import net.technic.technicblocks.blocks.connections.ConnectionConvention;

public class StaticTextureSelector extends TextureSelector  {

    private IIcon icon;
    private String iconName;

    public StaticTextureSelector(String[] args) {
        if(args.length < 1)
            throw TechnicBlocks.getProxy().createParseException("Texture selectors of type 'static' require at least one argument: a valid icon resource path.");

        this.iconName = args[0];
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.icon = register.registerIcon(iconName);
    }

    @Override
    public IIcon selectTexture(IBlockAccess world, int x, int y, int z, ForgeDirection side, ConnectionConvention connections) {
        return selectDefaultTexture();
    }

    @Override
    public IIcon selectDefaultTexture() {
        return icon;
    }
}
