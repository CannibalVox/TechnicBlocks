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

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.TechnicBlocks;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;

public class VerticalCtmTextureSelector extends TextureSelector {
    private String iconPath;
    private String[] allIcons = new String[4];

    public VerticalCtmTextureSelector(String[] args) {
        if(args.length < 1)
            throw TechnicBlocks.getProxy().createParseException("Texture selectors of type 'verticalCtm' require at least one argument: a resource path containing the full CTM icon list.");

        this.iconPath = args[0];
    }

    @Override
    public void registerIcons(BlockTextureScheme textureScheme, IIconRegister register, String decoratorName) {
        for (int i = 0; i < 4; i++) {
            allIcons[i] = iconPath+Integer.toString(i);

            if (decoratorName != null && decoratorName.equalsIgnoreCase(allIcons[i]))
                throw TechnicBlocks.getProxy().createParseException("Illegal decorator: Decorator for texture '"+decoratorName+"' can select that same texture.");

            textureScheme.registerIcon(register, allIcons[i]);
        }
    }

    @Override
    public String selectTexture(DataDrivenBlock block, BlockTextureScheme textureScheme, IBlockAccess world, int x, int y, int z, ForgeDirection side, ConnectionConvention connections) {
        int thisBlockMetadata = world.getBlockMetadata(x,y,z);

        ForgeDirection south = textureScheme.getAxisSide(side, 0, 1);

        short direction = 0;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x+south.offsetX, y+south.offsetY, z+south.offsetZ), world.getBlockMetadata(x+south.offsetX, y+south.offsetY, z+south.offsetZ)))
            direction |= 1;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x-south.offsetX, y-south.offsetY, z-south.offsetZ), world.getBlockMetadata(x-south.offsetX, y-south.offsetY, z-south.offsetZ)))
            direction |= 2;

        return allIcons[getIndexFromDirection(direction)];
    }

    protected int getIndexFromDirection(short direction) {
        switch (direction)
        {
            case 0x00:
                return 3;
            case 0x01:
                return 2;
            case 0x02:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public String selectDefaultTexture() {
        return allIcons[3];
    }
}
