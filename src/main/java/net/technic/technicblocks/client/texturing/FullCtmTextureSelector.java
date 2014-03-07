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

public class FullCtmTextureSelector extends TextureSelector {
    private String iconPath;
    private IIcon[] allIcons = new IIcon[48];

    public FullCtmTextureSelector(String[] args) {
        if(args.length < 1)
            throw TechnicBlocks.getProxy().createParseException("Texture selectors of type 'fullCtm' require at least one argument: a resource path containing the full CTM icon list.");

        this.iconPath = args[0];
    }

    @Override
    public void registerIcons(IIconRegister register) {
        for (int i = 0; i < 48; i++) {
            allIcons[i] = register.registerIcon(iconPath+Integer.toString(i));
        }
    }

    @Override
    public IIcon selectTexture(DataDrivenBlock block, BlockTextureScheme textureScheme, IBlockAccess world, int x, int y, int z, ForgeDirection side, ConnectionConvention connections) {
        int thisBlockMetadata = world.getBlockMetadata(x,y,z);

        ForgeDirection east = textureScheme.getAxisSide(side, 1, 0);
        ForgeDirection south = textureScheme.getAxisSide(side, 0, 1);

        short direction = 0;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x+east.offsetX, y+east.offsetY, z+east.offsetZ), world.getBlockMetadata(x+east.offsetX, y+east.offsetY, z+east.offsetZ)))
            direction |= 1;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x+south.offsetX, y+south.offsetY, z+south.offsetZ), world.getBlockMetadata(x+south.offsetX, y+south.offsetY, z+south.offsetZ)))
            direction |= 2;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x-east.offsetX, y-east.offsetY, z-east.offsetZ), world.getBlockMetadata(x-east.offsetX, y-east.offsetY, z-east.offsetZ)))
            direction |= 4;
        if (connections.testConnection(block, thisBlockMetadata, world.getBlock(x-south.offsetX, y-south.offsetY, z-south.offsetZ), world.getBlockMetadata(x-south.offsetX, y-south.offsetY, z-south.offsetZ)))
            direction |= 8;

        if ((direction & 0x03) == 0x03 && connections.testConnection(block, thisBlockMetadata, world.getBlock(x+east.offsetX+south.offsetX, y+east.offsetY+south.offsetY, z+east.offsetZ+south.offsetZ), world.getBlockMetadata(x+east.offsetX+south.offsetX, y+east.offsetY+south.offsetY, z+east.offsetZ+south.offsetZ)))
            direction |= 0x10;
        if ((direction & 0x06) == 0x06 && connections.testConnection(block, thisBlockMetadata, world.getBlock(x-east.offsetX+south.offsetX, y-east.offsetY+south.offsetY, z-east.offsetZ+south.offsetZ), world.getBlockMetadata(x-east.offsetX+south.offsetX, y-east.offsetY+south.offsetY, z-east.offsetZ+south.offsetZ)))
            direction |= 0x20;
        if ((direction & 0x09) == 0x09 && connections.testConnection(block, thisBlockMetadata, world.getBlock(x+east.offsetX-south.offsetX, y+east.offsetY-south.offsetY, z+east.offsetZ-south.offsetZ), world.getBlockMetadata(x+east.offsetX-south.offsetX, y+east.offsetY-south.offsetY, z+east.offsetZ-south.offsetZ)))
            direction |= 0x40;
        if ((direction & 0x0C) == 0x0C && connections.testConnection(block, thisBlockMetadata, world.getBlock(x-east.offsetX-south.offsetX, y-east.offsetY-south.offsetY, z-east.offsetZ-south.offsetZ), world.getBlockMetadata(x-east.offsetX-south.offsetX, y-east.offsetY-south.offsetY, z-east.offsetZ-south.offsetZ)))
            direction |= 0x80;

        return allIcons[getIndexFromDirection(direction)];
    }

    protected int getIndexFromDirection(short direction) {
        switch (direction)
        {
            case 0x00:
                return 0;
            case 0x01:
                return 1;
            case 0x05:
                return 2;
            case 0x04:
                return 3;
            case 0x03:
                return 4;
            case 0x06:
                return 5;
            case 0x0B:
                return 6;
            case 0x07:
                return 7;
            case 0x4F:
                return 8;
            case 0x1F:
                return 9;
            case 0xAF:
                return 10;
            case 0xCF:
                return 11;
            case 0x02:
                return 12;
            case 0x13:
                return 13;
            case 0x37:
                return 14;
            case 0x26:
                return 15;
            case 0x09:
                return 16;
            case 0x0C:
                return 17;
            case 0x0D:
                return 18;
            case 0x0E:
                return 19;
            case 0x8F:
                return 20;
            case 0x2F:
                return 21;
            case 0x3F:
                return 22;
            case 0x5F:
                return 23;
            case 0x0A:
                return 24;
            case 0x5B:
                return 25;
            case 0xFF:
                return 26;
            case 0xAE:
                return 27;
            case 0x1B:
                return 28;
            case 0x27:
                return 29;
            case 0x4B:
                return 30;
            case 0x17:
                return 31;
            case 0xEF:
                return 32;
            case 0xDF:
                return 33;
            case 0x6F:
                return 34;
            case 0x9F:
                return 35;
            case 0x08:
                return 36;
            case 0x49:
                return 37;
            case 0xCD:
                return 38;
            case 0x8C:
                return 39;
            case 0x4D:
                return 40;
            case 0x8E:
                return 41;
            case 0x8D:
                return 42;
            case 0x2E:
                return 43;
            case 0xBF:
                return 44;
            case 0x7F:
                return 45;
            case 0x0F:
                return 46;
            default:
                return 47;
        }
    }

    @Override
    public IIcon selectDefaultTexture() {
        return allIcons[0];
    }
}
