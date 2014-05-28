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
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BlockTextureScheme {
    private static Map<String, ForgeDirection[]> directionAliases;

    static {
        directionAliases = new HashMap<String, ForgeDirection[]>();

        directionAliases.put("north", new ForgeDirection[] {ForgeDirection.NORTH});
        directionAliases.put("front", new ForgeDirection[] {ForgeDirection.NORTH});

        directionAliases.put("south", new ForgeDirection[] {ForgeDirection.SOUTH});
        directionAliases.put("rear", new ForgeDirection[] {ForgeDirection.SOUTH});

        directionAliases.put("east", new ForgeDirection[] {ForgeDirection.EAST});
        directionAliases.put("left", new ForgeDirection[] {ForgeDirection.EAST});

        directionAliases.put("west", new ForgeDirection[] {ForgeDirection.WEST});
        directionAliases.put("right", new ForgeDirection[] {ForgeDirection.WEST});

        directionAliases.put("top", new ForgeDirection[] {ForgeDirection.UP});

        directionAliases.put("bottom", new ForgeDirection[] {ForgeDirection.DOWN});

        directionAliases.put("topandbottom", new ForgeDirection[] {ForgeDirection.UP, ForgeDirection.DOWN});

        directionAliases.put("2sides", new ForgeDirection[] {ForgeDirection.WEST, ForgeDirection.EAST});
        directionAliases.put("3sides", new ForgeDirection[] {ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.SOUTH});
        directionAliases.put("4sides", new ForgeDirection[] {ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH});
        directionAliases.put("all", new ForgeDirection[] {ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH, ForgeDirection.UP, ForgeDirection.DOWN});
    }

    private ConnectionConvention connectionConvention;
    private Map<ForgeDirection, TextureSelector> textureSelectorMap = new HashMap<ForgeDirection, TextureSelector>();
    private Map<String, TextureSelector> decoratorMap = new HashMap<String, TextureSelector>();
    private Map<String, IIcon> registeredIcons = new HashMap<String, IIcon>();

    public BlockTextureScheme(ConnectionConvention connectionConvention) {
        this.connectionConvention = connectionConvention;
    }

    public ConnectionConvention getConnectionConvention() { return connectionConvention; }

    public void addDecorator(String key, TextureSelector selector) {
        decoratorMap.put(key, selector);
    }

    public void addTextureSelector(String key, TextureSelector selector) {
        if (directionAliases.containsKey(key.toLowerCase())) {
            for(ForgeDirection dir : directionAliases.get(key.toLowerCase()))
                textureSelectorMap.put(dir, selector);
        } else
            throw TechnicBlocks.getProxy().createParseException("Side descriptor \"" + key + "\" isn't a real side descriptor.");
    }

    public void checkSelectorCoverage() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!textureSelectorMap.containsKey(dir)) {
                throw TechnicBlocks.getProxy().createParseException("No texture selector was indicated for \""+dir.name()+"\" side.");
            }
        }
    }

    public void registerIcon(IIconRegister register, String iconPath) {
        if (decoratorMap.containsKey(iconPath) || registeredIcons.containsKey(iconPath))
            return;

        registeredIcons.put(iconPath, register.registerIcon(iconPath));
    }

    public void registerIcons(IIconRegister register) {
        LinkedList<TextureSelector> registeredSelectors = new LinkedList<TextureSelector>();

        for (TextureSelector selector : textureSelectorMap.values()) {
            if (!registeredSelectors.contains(selector)) {
                selector.registerIcons(this, register);
                registeredSelectors.add(selector);
            }
        }

        for (String texture : decoratorMap.keySet()) {
            TextureSelector decorator = decoratorMap.get(texture);

            if (!registeredSelectors.contains(decorator)) {
                decorator.registerIcons(this, register, texture);
                registeredSelectors.add(decorator);
            }
        }
    }

    public IIcon getTextureForSide(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection physicalSide, ForgeDirection virtualSide, int rotations) {
        TextureSelector selector = textureSelectorMap.get(virtualSide);

        String resourcePath = selector.selectTexture(block, this, world, x, y, z, physicalSide, connectionConvention, rotations);

        while (decoratorMap.containsKey(resourcePath)) {
            selector = decoratorMap.get(resourcePath);
            resourcePath = selector.selectTexture(block, this, world, x, y, z, physicalSide, connectionConvention, rotations);
        }

        return registeredIcons.get(resourcePath);
    }

    public IIcon getTextureForSide(ForgeDirection side) {
        TextureSelector selector = textureSelectorMap.get(side);

        String resourcePath = selector.selectDefaultTexture();

        while (decoratorMap.containsKey(resourcePath)) {
            selector = decoratorMap.get(resourcePath);
            resourcePath = selector.selectDefaultTexture();
        }

        return registeredIcons.get(resourcePath);
    }

    static final ForgeDirection[] posXDirs = new ForgeDirection[] { ForgeDirection.EAST, ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH };
    static final ForgeDirection[] posYDirs = new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.DOWN, ForgeDirection.DOWN, ForgeDirection.DOWN, ForgeDirection.DOWN };
    public ForgeDirection getAxisSide(ForgeDirection side, int relX, int relY) {
        if (relX == 0 && relY == 0)
            return side;
        else if (relX != 0) {
            side = posXDirs[side.ordinal()];

            if (relX < 0)
                side = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[side.ordinal()]];
        } else {
            side = posYDirs[side.ordinal()];

            if (relY < 0)
                side = ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[side.ordinal()]];
        }

        return side;
    }

    public Vector3f getTopLeft(ForgeDirection side) {
        switch (side) {
            case UP:
                return new Vector3f(0, 1.0f, 0);
            case DOWN:
                return new Vector3f(0, 0, 1.0f);
            case NORTH:
                return new Vector3f(1.0f, 1.0f, 0);
            case SOUTH:
                return new Vector3f(0, 1.0f, 1.0f);
            case EAST:
                return new Vector3f(1.0f, 1.0f, 1.0f);
            default:
                return new Vector3f(0, 1.0f, 0);
        }
    }
}
