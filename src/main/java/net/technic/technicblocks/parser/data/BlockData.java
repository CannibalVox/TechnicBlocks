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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlockData {
    private String name;
    private String creativeTabName;
    private String materialName;
    private String soundName;
    private float hardness;
    private float resistance;
    private float lightLevel;
    private boolean isIndestructible;
    private HarvestLevelData harvestLevel;
    private Collection<String> blockTags = new ArrayList<String>();
    private ConnectionConventionData modelConnections;
    private ConnectionConventionData textureConnections;
    private String modelType;
    private String faceVisibilityType;
    private String collisionType;
    private String selectionType;
    private List<BehaviorData> behaviors = new ArrayList<BehaviorData>();
    private List<SubBlockData> subBlocks = new ArrayList<SubBlockData>();

    public String getBlockName() { return name; }
    public String getCreativeTabName() { return creativeTabName; }
    public String getMaterialName() { return materialName; }
    public String getSoundName() { return soundName; }
    public float getHardness() { return hardness; }
    public float getResistance() { return resistance; }
    public boolean isIndestructible() { return isIndestructible; }
    public float getLightLevel() { return lightLevel; }
    public HarvestLevelData getHarvestLevel() { return harvestLevel; }
    public Collection<String> getBlockTags() { return blockTags; }
    public ConnectionConventionData getModelConnections() { return modelConnections; }
    public ConnectionConventionData getTextureConnections() { return textureConnections; }
    public String getModelType() { return modelType; }
    public String getFaceVisibilityType() { return faceVisibilityType; }
    public String getCollisionType() { return collisionType; }
    public String getSelectionType() { return selectionType; }
    public List<BehaviorData> getBehaviors() { return behaviors; }
    public List<SubBlockData> getSubBlocks() { return subBlocks; }
}
