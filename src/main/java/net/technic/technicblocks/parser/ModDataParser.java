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
import net.technic.technicblocks.TechnicBlocksCommonProxy;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenBlockRegistry;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.blocks.behavior.BlockBehavior;
import net.technic.technicblocks.blocks.behavior.BlockBehaviorFactory;
import net.technic.technicblocks.blocks.collision.BlockCollision;
import net.technic.technicblocks.blocks.collision.BlockCollisionFactory;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.blocks.connections.ConnectionConventionFactory;
import net.technic.technicblocks.blocks.selection.BlockSelection;
import net.technic.technicblocks.blocks.selection.BlockSelectionFactory;
import net.technic.technicblocks.client.BlockModel;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityConvention;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityFactory;
import net.technic.technicblocks.client.renderer.DataDrivenRenderer;
import net.technic.technicblocks.client.renderer.RendererFactory;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import net.technic.technicblocks.client.texturing.StaticTextureSelector;
import net.technic.technicblocks.client.texturing.TextureSelector;
import net.technic.technicblocks.client.texturing.TextureSelectorFactory;
import net.technic.technicblocks.creativetabs.CreativeTabFactory;
import net.technic.technicblocks.items.DataDrivenItemBlock;
import net.technic.technicblocks.materials.MaterialFactory;
import net.technic.technicblocks.parser.data.*;
import net.technic.technicblocks.sound.SoundTypeFactory;
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
    private TechnicBlocksCommonProxy proxy;

    public ModDataParser(InputStream stream, TechnicBlocksCommonProxy proxy) {
        this.proxy = proxy;

        if (stream == null) {
            throw proxy.createParseException("Mod Data json file could not be found.");
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
                throw proxy.createParseException("Mod Data json file parsed to a null value.");
            }
        } catch (IOException ex) {
            throw proxy.createParseException("Error loading Mod Data json file.", ex);
        } catch (JsonSyntaxException ex) {
            throw proxy.createParseException("Error parsing the Mod Data json file.", ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {}
        }
    }

    public String getModId() { return data.getModId(); }

    public void RegisterAllBlocks(CreativeTabFactory creativeTabsFactory, MaterialFactory materialFactory, SoundTypeFactory soundFactory, ConnectionConventionFactory connectionConventionFactory, RendererFactory rendererFactory, FaceVisibilityFactory faceVisibilityFactory, BlockCollisionFactory collisionFactory, BlockSelectionFactory selectionFactory, BlockBehaviorFactory behaviorFactory, TextureSelectorFactory textureSelectorFactory) {
        for (CreativeTabData tab : data.getCreativeTabs()) {

            if (tab == null)
                continue;

            creativeTabsFactory.addCreativeTab(tab);
        }

        for (MaterialData material : data.getCustomMaterials()) {

            if (material == null)
                continue;

            materialFactory.addMaterial(material);
        }

        for (SoundData sound : data.getCustomSounds()) {

            if (sound == null)
                continue;

            soundFactory.addSoundType(sound);
        }

        for (BlockData block : data.getBlocks()) {
            if (block == null)
                continue;

            //Pull material & creative tab before registering block
            Material material = materialFactory.getMaterialByName(block.getMaterialName());

            if (material == null) {
                throw proxy.createParseException("'"+block.getMaterialName()+"' is not a valid material name.");
            }

            CreativeTabs tab = creativeTabsFactory.getCreativeTabByName(block.getCreativeTabName());

            if (tab == null) {
                throw proxy.createParseException("'" + block.getCreativeTabName() + "' is not a valid creative tab name.");
            }

            Block.SoundType sound = soundFactory.getSoundByName(block.getSoundName());

            if (sound == null) {
                throw proxy.createParseException("'" + block.getSoundName() + "' is not a valid sound name.");
            }

            setupAndRegisterBlock(block, material, tab, sound, connectionConventionFactory, rendererFactory, faceVisibilityFactory, collisionFactory, selectionFactory, behaviorFactory, textureSelectorFactory);
        }
    }

    private void setupAndRegisterBlock(BlockData block, Material material, CreativeTabs creativeTab, Block.SoundType sound, ConnectionConventionFactory conventionFactory, RendererFactory rendererFactory, FaceVisibilityFactory faceVisibilityFactory, BlockCollisionFactory collisionFactory, BlockSelectionFactory selectionFactory, BlockBehaviorFactory behaviorFactory, TextureSelectorFactory textureSelectorFactory) {
        //Build blockmodel
        ConnectionConvention modelConvention = createConvention(conventionFactory, block.getModelConnections());
        FaceVisibilityConvention faceVisibility = faceVisibilityFactory.getConvention(block.getFaceVisibilityType());
        DataDrivenRenderer renderer = rendererFactory.getRenderer(block.getModelType());

        String collisionType = block.getCollisionType();
        String selectionType = block.getSelectionType();

        if (collisionType == null)
            collisionType = renderer.getDefaultCollisionType();
        if (selectionType == null)
            selectionType = renderer.getDefaultSelectionType();

        BlockCollision collision = collisionFactory.getCollision(collisionType);
        BlockSelection selection = selectionFactory.getSelection(selectionType);

        BlockModel model = new BlockModel(renderer, modelConvention, faceVisibility, collision, selection);

        //Get block behaviors & get the bit index where subblock data starts
        List<BlockBehavior> behaviors = new LinkedList<BlockBehavior>();
        int maxReservedBit = buildBlockBehaviorList(block.getBehaviors(), behaviorFactory, block.getBlockName(), behaviors);

        //Build subblock list
        ConnectionConvention textureConvention = createConvention(conventionFactory, block.getTextureConnections());
        List<DataDrivenSubBlock> subBlocks = buildSubBlocks(block.getSubBlocks(), textureSelectorFactory, textureConvention, maxReservedBit, block.getBlockName());

        //Build out basic minecraft block data
        DataDrivenBlock blockObj = new DataDrivenBlock(material, model, block.getBlockTags(), behaviors, subBlocks);
        blockObj.setHardness(block.getHardness());
        blockObj.setCreativeTab(creativeTab);
        blockObj.setStepSound(sound);
        blockObj.setLightLevel(block.getLightLevel());
        blockObj.setResistance(block.getResistance());

        if (block.isIndestructible())
            blockObj.setBlockUnbreakable();

        if (block.getHarvestLevel() != null) {
            blockObj.setHarvestLevel(block.getHarvestLevel().getTool(), block.getHarvestLevel().getHarvestLevel());
        }
        //Register block
        GameRegistry.registerBlock(blockObj, DataDrivenItemBlock.class, block.getBlockName(), data.getModId());
        DataDrivenBlockRegistry.registerBlock(blockObj);
    }

    private int buildBlockBehaviorList(Collection<BehaviorData> behaviorData, BlockBehaviorFactory factory, String blockName, List<BlockBehavior> outList) {
        for (BehaviorData data : behaviorData) {

            if (data == null)
                continue;

            BlockBehavior behavior = factory.createBehavior(data.getName(), data.getArgs());
            outList.add(behavior);
        }

        int maxUsedRootBit = 4;

        //Assign reserved metadatas
        for (BlockBehavior behavior : outList) {
            int bitSize = behavior.getMetadataBitSize();
            if (behavior.isMetadataReserved() && bitSize > 0) {
                if (bitSize > maxUsedRootBit) {
                    throw proxy.createParseException("Block '"+blockName+"' has too many metadata-using behaviors.  There are not enough metadata bits to handle it all!");
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
                    throw proxy.createParseException("Block '"+blockName+"' has too many metadata-using behaviors.  There are not enough metadata bits to handle it all!");
                }

                maxUsedRootBit -= bitSize;
                behavior.setMetadataBitRoot(maxUsedRootBit);
            }
        }

        return maxReservedRootBit;
    }

    private List<DataDrivenSubBlock> buildSubBlocks(Collection<SubBlockData> subBlockData, TextureSelectorFactory textureSelectorFactory, ConnectionConvention textureConvention, int maxReservedBit, String blockName) {
        int availableSubBlocks = 1;
        while (maxReservedBit > 0) {
            availableSubBlocks = (availableSubBlocks << 1) | 1;
            maxReservedBit--;
        }

        List<DataDrivenSubBlock> subBlocks = new LinkedList<DataDrivenSubBlock>();

        for (SubBlockData data : subBlockData) {
            if (data == null)
                continue;

            if (data.getMetadata() >= 16)
                throw proxy.createParseException("Block '"+blockName+"' has a subblock with invalid metadata- metadata ID's have to be less than 16!");
            else if (data.getMetadata() >= availableSubBlocks)
                throw proxy.createParseException("Block '"+blockName+"' cannot use metadata "+data.getMetadata()+" because some of those bits are being occupied by a behavior.");

            BlockTextureScheme textureScheme = new BlockTextureScheme(textureConvention);
            populateTextureScheme(textureScheme, textureSelectorFactory, data.getTextureScheme());

            DataDrivenSubBlock subBlock = new DataDrivenSubBlock(data.getMetadata(), "tile."+this.data.getModId()+"."+data.getDisplayName(), data.isInCreativeMenu(), data.getVariantTag(), textureScheme);
            subBlocks.add(subBlock);
        }

        return subBlocks;
    }

    private ConnectionConvention createConvention(ConnectionConventionFactory factory, ConnectionConventionData data) {
        String[] argsArray = new String[data.getArgs().size()];
        data.getArgs().toArray(argsArray);
        ConnectionConvention convention = factory.createConvention(data.getName(),argsArray);

        for (ConnectionConventionData subConventionData : data.getSubConventions()) {
            if (subConventionData == null)
                continue;

            ConnectionConvention subConvention = createConvention(factory, subConventionData);
            convention.addSubConvention(subConvention);
        }

        return convention;
    }

    private void populateTextureScheme(BlockTextureScheme textureScheme, TextureSelectorFactory textureSelectorFactory, TextureSchemeData schemeData) {
        for(TextureFaceData faceData : schemeData.getFaces()) {
            if (faceData == null)
                continue;

            TextureSelector selector = textureSelectorFactory.createSelector(faceData.getSelector(), faceData.getArgs());
            textureScheme.addTextureSelector(faceData.getFace(), selector);
        }

        for (DecorationData decorator : schemeData.getDecorators()) {
            if (decorator == null)
                continue;

            TextureSelector selector = textureSelectorFactory.createSelector(decorator.getSelector(), decorator.getArgs());
            textureScheme.addDecorator(decorator.getTextureResource(), selector);
        }

        textureScheme.checkSelectorCoverage();
    }
}
