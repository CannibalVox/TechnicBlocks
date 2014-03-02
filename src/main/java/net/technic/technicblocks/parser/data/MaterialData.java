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

package net.technic.technicblocks.parser.data;

public class MaterialData {
    public enum PistonBehavior {
        shove,
        destroy,
        block
    }

    private String name;
    private int mapColorIndex;
    private boolean isLiquid;
    private boolean isSolid;
    private boolean blocksGrass;
    private boolean blocksMovement;
    private boolean isFlammable;
    private boolean isReplaceable;
    private boolean isOpaque;
    private boolean requiresTool;
    private PistonBehavior pistonBehavior;
    private boolean adventureModeExempt;

    public MaterialData() {}

    public String getMaterialName() { return name; }
    public int getMapColorIndex() { return mapColorIndex; }
    public boolean isLiquid() { return isLiquid; }
    public boolean isSolid() { return isSolid; }
    public boolean doesBlockGrass() { return blocksGrass; }
    public boolean doesBlockMovement() { return blocksMovement; }
    public boolean isFlammable() { return isFlammable; }
    public boolean isReplaceable() { return isReplaceable; }
    public boolean isOpaque() { return isOpaque; }
    public boolean doesRequireTool() { return requiresTool; }
    public PistonBehavior getPistonBehavior() { return pistonBehavior; }
    public boolean isAdventureModeExempt() { return adventureModeExempt; }
}
