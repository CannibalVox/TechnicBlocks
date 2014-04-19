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

package net.technic.technicblocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.technic.technicblocks.blocks.behavior.*;
import net.technic.technicblocks.blocks.collision.BlockCollisionFactory;
import net.technic.technicblocks.blocks.collision.FenceCollision;
import net.technic.technicblocks.blocks.collision.SelectionVolumeCollision;
import net.technic.technicblocks.blocks.collision.StairsCollision;
import net.technic.technicblocks.blocks.connections.*;
import net.technic.technicblocks.blocks.selection.BlockSelectionFactory;
import net.technic.technicblocks.blocks.selection.CubeSelection;
import net.technic.technicblocks.blocks.selection.FenceSelection;
import net.technic.technicblocks.blocks.selection.SlabSelection;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityFactory;
import net.technic.technicblocks.client.facevisibility.OpaqueBlockVisibilityConvention;
import net.technic.technicblocks.client.renderer.*;
import net.technic.technicblocks.client.renderer.tessellator.Tessellator;
import net.technic.technicblocks.client.renderer.tessellator.TessellatorFactory;
import net.technic.technicblocks.client.renderer.tessellator.preposthandlers.GlowLightingHandler;
import net.technic.technicblocks.client.renderer.tessellator.preposthandlers.WorldLightingHandler;
import net.technic.technicblocks.client.texturing.*;
import net.technic.technicblocks.creativetabs.CreativeTabFactory;
import net.technic.technicblocks.materials.MaterialFactory;
import net.technic.technicblocks.mods.TechnicBlockModContainer;
import net.technic.technicblocks.parser.ModDataParser;
import net.technic.technicblocks.sound.SoundTypeFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Mod(modid = TechnicBlocks.MODID, version = TechnicBlocks.VERSION)
public class TechnicBlocks {
    public static final String MODID = "technicblocks";
    public static final String VERSION = "1.0";
    @Mod.Instance
    public static TechnicBlocks instance;

    @SidedProxy(clientSide = "net.technic.technicblocks.TechnicBlocksClientProxy", serverSide = "net.technic.technicblocks.TechnicBlocksCommonProxy")
    private static TechnicBlocksCommonProxy proxy;

    public static TechnicBlocksCommonProxy getProxy() { return proxy; }

    private CreativeTabFactory creativeTabFactory = new CreativeTabFactory();
    private MaterialFactory materialFactory = new MaterialFactory();
    private SoundTypeFactory soundTypeFactory = new SoundTypeFactory();
    private ConnectionConventionFactory conventionFactory = new ConnectionConventionFactory();
    private RendererFactory rendererFactory = new RendererFactory();
    private FaceVisibilityFactory faceVisibilityFactory = new FaceVisibilityFactory();
    private BlockBehaviorFactory blockBehaviorFactory = new BlockBehaviorFactory();
    private TextureSelectorFactory textureSelectorFactory = new TextureSelectorFactory();
    private BlockCollisionFactory collisionFactory = new BlockCollisionFactory();
    private BlockSelectionFactory selectionFactory = new BlockSelectionFactory();
    private TessellatorFactory tessellatorFactory = new TessellatorFactory();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Register connection conventions
        conventionFactory.addConvention("none", NoConnectionConvention.class);
        conventionFactory.addConvention("allTags", AllTagsConvention.class);
        conventionFactory.addConvention("anyTags", AnyTagsConvention.class);
        conventionFactory.addConvention("or", LogicalOrConvention.class);
        conventionFactory.addConvention("sameVariant", VariantTagConvention.class);

        //Create renderers
        DataDrivenRenderer cube = new CubeRenderer(RenderingRegistry.getNextAvailableRenderId());
        DataDrivenRenderer slab = new SlabRenderer(RenderingRegistry.getNextAvailableRenderId());
        DataDrivenRenderer stairs = new StairRenderer(RenderingRegistry.getNextAvailableRenderId());
        DataDrivenRenderer pillar = new PillarRenderer(RenderingRegistry.getNextAvailableRenderId());
        DataDrivenRenderer fence = new FenceRenderer(RenderingRegistry.getNextAvailableRenderId());

        //Register renderers
        RenderingRegistry.registerBlockHandler(cube);
        RenderingRegistry.registerBlockHandler(slab);
        RenderingRegistry.registerBlockHandler(stairs);
        RenderingRegistry.registerBlockHandler(pillar);
        RenderingRegistry.registerBlockHandler(fence);

        //Set up renderer factory
        rendererFactory.addRenderer("cube", cube);
        rendererFactory.addRenderer("slab", slab);
        rendererFactory.addRenderer("stairs", stairs);
        rendererFactory.addRenderer("pillar", pillar);
        rendererFactory.addRenderer("fence", fence);

        //Register face visibility factory
        faceVisibilityFactory.addConvention("normal", new OpaqueBlockVisibilityConvention());

        //Register texture selectors
        textureSelectorFactory.addSelector("static", StaticTextureSelector.class);
        textureSelectorFactory.addSelector("fullCtm", FullCtmTextureSelector.class);
        textureSelectorFactory.addSelector("horizontalCtm", HorizontalCtmTextureSelector.class);
        textureSelectorFactory.addSelector("verticalCtm", VerticalCtmTextureSelector.class);
        textureSelectorFactory.addSelector("hashRandom", HashRandomTextureSelector.class);

        //Register behaviors
        blockBehaviorFactory.addBehavior("4direction", FourDirectionPlacementBehavior.class);
        blockBehaviorFactory.addBehavior("placeRandomSubBlock", PlaceRandomSubBlockBehavior.class);
        blockBehaviorFactory.addBehavior("verticalPlacement", VerticalPlacementBehavior.class);
        blockBehaviorFactory.addBehavior("completeSlab", CompleteSlabBehavior.class);
        blockBehaviorFactory.addBehavior("replaceDrops", ReplaceDropsBehavior.class);
        blockBehaviorFactory.addBehavior("replacePicker", ReplacePickerBehavior.class);
        blockBehaviorFactory.addBehavior("6direction", SixDirectionPlacementBehavior.class);
        blockBehaviorFactory.addBehavior("redstoneOn", RedstoneActivationBehavior.class);
        blockBehaviorFactory.addBehavior("redstoneOff", RedstoneDeactivationBehavior.class);

        //Register collision
        SelectionVolumeCollision selection = new SelectionVolumeCollision();
        collisionFactory.addCollision("selection", selection);
        collisionFactory.addCollision("stairs", new StairsCollision(selection));
        collisionFactory.addCollision("fence", new FenceCollision());

        //Register selection
        selectionFactory.addSelection("cube", new CubeSelection());
        selectionFactory.addSelection("slab", new SlabSelection());
        selectionFactory.addSelection("fence", new FenceSelection());

        //Register tessellators
        tessellatorFactory.addTessellator("basic", new Tessellator(new WorldLightingHandler()));
        tessellatorFactory.addTessellator("glow", new Tessellator(new GlowLightingHandler()));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        //Locate blox files in other jars
        findBloxFiles();
    }

    private void findBloxFiles() {
        Collection<File> modFiles = new LinkedList<File>();

        //Find blox files in existing mods, and keep track of which files are mods
        for (ModContainer mod : Loader.instance().getModList()) {
            File modFile = mod.getSource();

            examineFile(modFile);
            modFiles.add(modFile);
        }

        Collection<ModContainer> newMods = new LinkedList<ModContainer>();

        //Enumerate all files loaded into the classpath- only check out non-mod files
        for (File file : ((ModClassLoader)Loader.instance().getModClassLoader()).getParentSources()) {
            if (!modFiles.contains(file)) {
                for (String modId : examineFile(file)) {
                    //It's a non-mod file that has cool blox stuff in it!  Better read resources in!
                    ModContainer container = new TechnicBlockModContainer(modId, file);
                    FMLCommonHandler.instance().addModToResourcePack(container);

                    //If we can find a mcmod.info file that corresponds to the mod ID in the blox file,
                    //bind the metadata from mcmod.info and hold onto the container
                    if (tryBindMetadata(container)) {
                        newMods.add(container);
                    }
                }
            }
        }

        if (!newMods.isEmpty()) {
            try {
                //I'm a monster.

                //We're going to use black magicks to inject the mod containers generated above into
                //the list of mods- not the active mods, so nothing should go too wrong, we just want
                //these mods to show up in the mod list.
                Field modsField = Loader.class.getDeclaredField("mods");

                boolean requiresAccessChange = !modsField.isAccessible();

                if (requiresAccessChange) modsField.setAccessible(true);
                List<ModContainer> existingMods = (List<ModContainer>)modsField.get(Loader.instance());
                List<ModContainer> newModList = Lists.newArrayList();
                newModList.addAll(existingMods);
                newModList.addAll(newMods);
                modsField.set(Loader.instance(), ImmutableList.copyOf(newModList));
                if (requiresAccessChange) modsField.setAccessible(false);

            } catch (NoSuchFieldException ex) {
            } catch (IllegalAccessException ex) {
            }
        }

        //Gets the added lang entries where they belong
        proxy.refreshResources();
    }

    private static final Pattern bloxFilePattern = Pattern.compile("assets/([^/]*)/blox/(.*).blox");
    private Collection<String> examineFile(File bloxCandidate) {
        Collection<String> parsedModIds = new LinkedList<String>();
        try {
            //Attempt to locate all the properly placed .blox files in the (ostensibly) jar file
            ZipFile zip = new ZipFile(bloxCandidate);
            for (ZipEntry ze : Collections.list(zip.entries()))
            {
                Matcher matcher = bloxFilePattern.matcher(ze.getName());
                if (matcher.matches())
                {
                    try {
                        ModDataParser parser = new ModDataParser(zip.getInputStream(ze), proxy);
                        parser.RegisterAllBlocks(creativeTabFactory, materialFactory, soundTypeFactory, conventionFactory, rendererFactory, faceVisibilityFactory, collisionFactory, selectionFactory, blockBehaviorFactory, textureSelectorFactory, tessellatorFactory);
                        proxy.verifyCreativeTabs(creativeTabFactory);

                        //If we found a valid blox file, then hold onto the mod ID
                        String modId = parser.getModId();

                        if (!parsedModIds.contains(modId))
                            parsedModIds.add(modId);
                    } catch (RuntimeException ex) {
                        throw proxy.createParseException("An error occurred while parsing blox file '" + ze.getName() + "':", ex);
                    }
                }
            }
        } catch (IOException ex)
        {
            //Just ignore this mod then
        }

        //Return a list of all unique mod id's in all parsed blox files
        return parsedModIds;
    }

    private boolean tryBindMetadata(ModContainer container) {
        JarFile jar = null;
        try
        {
            //Attempt to load & parse mcmod.info from jar file
            //return true if we were successful
            jar = new JarFile(container.getSource());

            ZipEntry modInfo = jar.getEntry("mcmod.info");
            MetadataCollection mc = null;
            if (modInfo == null)
                return false;

            mc = MetadataCollection.from(jar.getInputStream(modInfo), container.getSource().getName());
            container.bindMetadata(mc);
            return (container.getMetadata() != null && !container.getMetadata().autogenerated);
        } catch (Exception ex) {
            return false;
        }
    }
}
