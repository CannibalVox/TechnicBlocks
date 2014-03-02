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

package net.technic.technicblocks.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenBlockRegistry;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.blocks.behavior.BlockBehavior;
import net.technic.technicblocks.blocks.behavior.BlockBehaviorFactory;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.blocks.connections.ConnectionConventionFactory;
import net.technic.technicblocks.client.BlockModel;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityConvention;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityFactory;
import net.technic.technicblocks.client.renderer.DataDrivenRenderer;
import net.technic.technicblocks.client.renderer.RendererFactory;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import net.technic.technicblocks.client.texturing.StaticTextureSelector;
import net.technic.technicblocks.creativetabs.CreativeTabFactory;
import net.technic.technicblocks.items.DataDrivenItemBlock;
import net.technic.technicblocks.materials.MaterialFactory;
import net.technic.technicblocks.parser.data.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ModDataParser {

    private ModData data;
    private Gson gson;

    public ModDataParser(InputStream stream) throws ParseException {

        if (stream == null) {
            throw new ParseException("Mod Data json file could not be found.");
        }

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer);
            String dataText = writer.toString();
            data = gson.fromJson(dataText, ModData.class);

            if (data == null) {
                throw new ParseException("Mod Data json file parsed to a null value.");
            }
        } catch (IOException ex) {
            throw new ParseException("Error loading Mod Data json file.", ex);
        } catch (JsonSyntaxException ex) {
            throw new ParseException("Error parsing the Mod Data json file.", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {}
        }
    }

    public void RegisterAllBlocks(CreativeTabFactory creativeTabsFactory, MaterialFactory materialFactory, ConnectionConventionFactory connectionConventionFactory, RendererFactory rendererFactory, FaceVisibilityFactory faceVisibilityFactory, BlockBehaviorFactory behaviorFactory) throws ParseException {
        for (CreativeTabData tab : data.getCreativeTabs()) {
            creativeTabsFactory.addCreativeTab(tab);
        }

        for (MaterialData material : data.getCustomMaterials()) {
            materialFactory.addMaterial(material);
        }

        for (BlockData block : data.getBlocks()) {
            //Pull material & creative tab before registering block
            Material material = materialFactory.getMaterialByName(block.getMaterialName());

            if (material == null) {
                throw new ParseException("'"+block.getMaterialName()+"' is not a valid material name.");
            }

            CreativeTabs tab = creativeTabsFactory.getCreativeTabByName(block.getCreativeTabName());

            if (tab == null) {
                throw new ParseException("'" + block.getCreativeTabName() + "' is not a valid creative tab name.");
            }

            setupAndRegisterBlock(block, material, tab, connectionConventionFactory, rendererFactory, faceVisibilityFactory, behaviorFactory);
        }
    }

    private void setupAndRegisterBlock(BlockData block, Material material, CreativeTabs creativeTab, ConnectionConventionFactory conventionFactory, RendererFactory rendererFactory, FaceVisibilityFactory faceVisibilityFactory, BlockBehaviorFactory behaviorFactory) throws ParseException {
        //Build blockmodel
        ConnectionConvention modelConvention = createConvention(conventionFactory, block.getModelConnections());
        FaceVisibilityConvention faceVisibility = faceVisibilityFactory.getConvention(block.getFaceVisibilityType());
        DataDrivenRenderer renderer = rendererFactory.getRenderer(block.getModelType());
        BlockModel model = new BlockModel(renderer, modelConvention, faceVisibility);

        //Get block behaviors & get the bit index where subblock data starts
        List<BlockBehavior> behaviors = new LinkedList<BlockBehavior>();
        int maxReservedBit = buildBlockBehaviorList(block.getBehaviors(), behaviorFactory, block.getBlockName(), behaviors);

        //Build subblock list
        ConnectionConvention textureConvention = createConvention(conventionFactory, block.getTextureConnections());
        List<DataDrivenSubBlock> subBlocks = buildSubBlocks(block.getSubBlocks(), textureConvention, maxReservedBit, block.getBlockName());

        //Build out basic minecraft block data
        DataDrivenBlock blockObj = new DataDrivenBlock(material, model, block.getBlockTags(), behaviors, subBlocks);
        blockObj.setHardness(block.getHardness());
        blockObj.setCreativeTab(creativeTab);
        blockObj.setLightLevel(block.getLightLevel());
        blockObj.setResistance(block.getResistance());

        if (block.isIndestructible())
            blockObj.setBlockUnbreakable();

        for (HarvestLevelData harvestLevel : block.getHarvestLevel()) {
            blockObj.setHarvestLevel(harvestLevel.getTool(), harvestLevel.getHarvestLevel());
        }

        blockObj.setStepSound(new Block.SoundType(block.getSound().getName(), block.getSound().getVolume(), block.getSound().getPitch()));

        //Register block
        GameRegistry.registerBlock(blockObj, DataDrivenItemBlock.class, block.getBlockName(), data.getModId());
        DataDrivenBlockRegistry.registerBlock(blockObj);
    }

    private int buildBlockBehaviorList(Collection<BehaviorData> behaviorData, BlockBehaviorFactory factory, String blockName, List<BlockBehavior> outList) throws ParseException {
        for (BehaviorData data : behaviorData) {
            BlockBehavior behavior = factory.createBehavior(data.getName(), data.getArgs());
            outList.add(behavior);
        }

        int maxUsedRootBit = 4;

        //Assign reserved metadatas
        for (BlockBehavior behavior : outList) {
            int bitSize = behavior.getMetadataBitSize();
            if (behavior.isMetadataReserved() && bitSize > 0) {
                if (bitSize > maxUsedRootBit) {
                    throw new ParseException("Block '"+blockName+"' has too many metadata-using behaviors.  There are not enough metadata bits to handle it all!");
                }

                maxUsedRootBit -= bitSize;
                behavior.setMetadataBitRoot(maxUsedRootBit);
            }
        }

        int maxReservedRootBit = maxUsedRootBit;

        //Assign unreserved metadatas
        for (BlockBehavior behavior : outList) {
            int bitSize = behavior.getMetadataBitSize();
            if (!behavior.isMetadataReserved() && bitSize > 0) {
                if (bitSize > maxUsedRootBit) {
                    throw new ParseException("Block '"+blockName+"' has too many metadata-using behaviors.  There are not enough metadata bits to handle it all!");
                }

                maxUsedRootBit -= bitSize;
                behavior.setMetadataBitRoot(maxUsedRootBit);
            }
        }

        return maxReservedRootBit;
    }

    private List<DataDrivenSubBlock> buildSubBlocks(Collection<SubBlockData> subBlockData, ConnectionConvention textureConvention, int maxReservedBit, String blockName) throws ParseException {
        int availableSubBlocks = 1;
        while (maxReservedBit > 0) {
            availableSubBlocks = (availableSubBlocks << 1) | 1;
            maxReservedBit--;
        }

        List<DataDrivenSubBlock> subBlocks = new LinkedList<DataDrivenSubBlock>();

        for (SubBlockData data : subBlockData) {
            if (data.getMetadata() >= 16)
                throw new ParseException("Block '"+blockName+"' has a subblock with invalid metadata- metadata ID's have to be less than 16!");
            else if (data.getMetadata() >= availableSubBlocks)
                throw new ParseException("Block '"+blockName+"' cannot use metadata "+data.getMetadata()+" because some of those bits are being occupied by a behavior.");

            BlockTextureScheme textureScheme = new BlockTextureScheme(textureConvention);

            //HACK
            textureScheme.addTextureSelector("all", new StaticTextureSelector("test"));

            DataDrivenSubBlock subBlock = new DataDrivenSubBlock(data.getMetadata(), "tile."+this.data.getModId()+"."+data.getDisplayName(), data.isInCreativeMenu(), textureScheme);
            subBlocks.add(subBlock);
        }

        return subBlocks;
    }

    private ConnectionConvention createConvention(ConnectionConventionFactory factory, ConnectionConventionData data) throws ParseException {
        String[] argsArray = new String[data.getArgs().size()];
        data.getArgs().toArray(argsArray);
        ConnectionConvention convention = factory.createConvention(data.getName(),argsArray);

        for (ConnectionConventionData subConventionData : data.getSubConventions()) {
            ConnectionConvention subConvention = createConvention(factory, subConventionData);
            convention.addSubConvention(subConvention);
        }

        return convention;
    }
}
