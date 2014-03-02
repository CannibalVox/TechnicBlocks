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

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.technic.technicblocks.api.IFileProcessor;
import net.technic.technicblocks.api.TechnicBlocksApi;
import net.technic.technicblocks.blocks.behavior.BlockBehaviorFactory;
import net.technic.technicblocks.blocks.connections.ConnectionConventionFactory;
import net.technic.technicblocks.blocks.connections.NoConnectionConvention;
import net.technic.technicblocks.client.facevisibility.FaceVisibilityFactory;
import net.technic.technicblocks.client.facevisibility.OpaqueBlockVisibilityConvention;
import net.technic.technicblocks.client.renderer.CubeRenderer;
import net.technic.technicblocks.client.renderer.DataDrivenRenderer;
import net.technic.technicblocks.client.renderer.RendererFactory;
import net.technic.technicblocks.creativetabs.CreativeTabFactory;
import net.technic.technicblocks.materials.MaterialFactory;
import net.technic.technicblocks.parser.ModDataParser;
import net.technic.technicblocks.parser.ParseException;

@Mod(modid = TechnicBlocks.MODID, version = TechnicBlocks.VERSION)
public class TechnicBlocks implements IFileProcessor {
    public static final String MODID = "technicblocks";
    public static final String VERSION = "1.0";

    private CreativeTabFactory creativeTabFactory = new CreativeTabFactory();
    private MaterialFactory materialFactory = new MaterialFactory();
    private ConnectionConventionFactory conventionFactory = new ConnectionConventionFactory();
    private RendererFactory rendererFactory = new RendererFactory();
    private FaceVisibilityFactory faceVisibilityFactory = new FaceVisibilityFactory();
    private BlockBehaviorFactory blockBehaviorFactory = new BlockBehaviorFactory();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //Register connection conventions
        conventionFactory.addConvention("none", NoConnectionConvention.class);

        //Create renderers
        DataDrivenRenderer cube = new CubeRenderer(RenderingRegistry.getNextAvailableRenderId());

        //Register renderers
        RenderingRegistry.registerBlockHandler(cube);

        //Set up renderer factory
        rendererFactory.addRenderer("cube", cube);

        //Register face visibility factory
        faceVisibilityFactory.addConvention("normal", new OpaqueBlockVisibilityConvention());

        //Register behaviors

        //Register to receive data
        TechnicBlocksApi.registerFileProcessor(this);
    }

    public void processFile(String filePath) {
        try {
            ModDataParser parser = new ModDataParser(TechnicBlocks.class.getResourceAsStream(filePath));
            parser.RegisterAllBlocks(creativeTabFactory, materialFactory, conventionFactory, rendererFactory, faceVisibilityFactory, blockBehaviorFactory);
            creativeTabFactory.verifyCreativeTabs();
        } catch (ParseException ex) {
            FMLLog.getLogger().fatal("Error loading json file '"+filePath+"'.", ex);
            System.exit(-1);
        }
    }
}
