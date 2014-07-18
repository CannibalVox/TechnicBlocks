package net.technic.technicblocks.client.renderer;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.DataDrivenSubBlock;
import net.technic.technicblocks.client.renderer.context.IRenderContext;
import net.technic.technicblocks.client.renderer.context.InventoryRenderContext;
import net.technic.technicblocks.client.renderer.tessellator.TessellatorInstance;
import net.technic.technicblocks.client.texturing.BlockTextureScheme;
import org.lwjgl.opengl.GL11;

public class WallRenderer extends DataDrivenRenderer {

    public WallRenderer(int renderId) {
        super(renderId);
    }

    @Override
    public String getDefaultCollisionType() {
        return "fence";
    }

    @Override
    public String getDefaultSelectionType() {
        return "wall";
    }

    @Override
    protected boolean tesselate(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        if (connectionContext instanceof InventoryRenderContext)
            return tesselateInventory(block, metadata, tessellatorInstance, connectionContext);

        ForgeDirection up = block.reverseTransformBlockFacing(metadata, ForgeDirection.UP);
        ForgeDirection down = block.reverseTransformBlockFacing(metadata, ForgeDirection.DOWN);
        ForgeDirection front = block.reverseTransformBlockFacing(metadata, ForgeDirection.NORTH);

        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);
        ForgeDirection north = front;
        int evenConnections = 0;
        int oddConnections = 0;

        for (int i = 0; i < 4; i++) {
            if (connectionContext.isModelConnected(north)) {
                renderFencing(north, up, down, subBlock.getTextureScheme(), connectionContext, tessellatorInstance);

                if (i % 2 == 0)
                    evenConnections++;
                else
                    oddConnections++;
            }

            north = north.getRotation(up);
        }

        if (evenConnections < 2 && oddConnections < 2 || evenConnections == 1 || oddConnections == 1)
            renderPost(subBlock.getTextureScheme(), connectionContext, tessellatorInstance, up, down, front);

        return true;
    }

    private boolean tesselateInventory(DataDrivenBlock block, int metadata, TessellatorInstance tessellatorInstance, IRenderContext connectionContext) {
        DataDrivenSubBlock subBlock = block.getSubBlock(metadata);

        renderPost(subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH);

        GL11.glTranslatef(-0.375f, 0, 0);
        renderFace(ForgeDirection.NORTH, 0.375f, 0.1875f, 0.625f, 1.0f, 0.3125f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP);
        renderFace(ForgeDirection.WEST, 0.3125f, 0.1875f, 0.6875f, 1.0f, 0.375f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP);
        renderFace(ForgeDirection.UP, 0.375f, 0.3125f, 0.625f, 0.6875f, 0.1875f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.NORTH);

        GL11.glTranslatef(0.75f, 0, 0);
        renderFace(ForgeDirection.NORTH, 0.375f, 0.1875f, 0.625f, 1.0f, 0.3125f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.UP);
        renderFace(ForgeDirection.UP, 0.375f, 0.3125f, 0.625f, 0.6875f, 0.1875f, subBlock.getTextureScheme(), connectionContext, tessellatorInstance, ForgeDirection.NORTH);

        GL11.glTranslatef(-0.375f, 0, 0);
        return true;
    }

    private void renderPost(BlockTextureScheme textureScheme, IRenderContext renderContext, TessellatorInstance tessellatorInstance, ForgeDirection up, ForgeDirection down, ForgeDirection front) {
        renderFaceIfVisible(up, 0.25f, 0.25f, 0.75f, 0.75f, textureScheme, renderContext, tessellatorInstance, front);
        renderFaceIfVisible(down, 0.25f ,0.25f, 0.75f, 0.75f, textureScheme, renderContext, tessellatorInstance, front);

        for (int i = 0; i < 4; i++) {
            renderFace(front, 0.25f, 0, 0.75f, 1, 0.25f, textureScheme, renderContext, tessellatorInstance, up);
            front = front.getRotation(up);
        }
    }

    private void renderFencing(ForgeDirection runningDirection, ForgeDirection top, ForgeDirection bottom, BlockTextureScheme textureScheme, IRenderContext renderContext, TessellatorInstance tessellatorInstance) {
        renderFaceIfVisible(runningDirection, 0.3125f, 0.1875f, 0.6875f, 1.0f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(top), 0.5f, 0.1875f, 1.0f, 1.0f, 0.3125f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(runningDirection.getRotation(bottom), 0, 0.1875f, 0.5f, 1.0f, 0.3125f, textureScheme, renderContext, tessellatorInstance, top);
        renderFace(top, 0.3125f, 0, 0.6875f, 0.5f, 0.1875f, textureScheme, renderContext, tessellatorInstance, runningDirection);
        renderFaceIfVisible(bottom, 0.3125f, 0, 0.6875f, 0.5f, textureScheme, renderContext, tessellatorInstance, runningDirection);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(DataDrivenBlock block, IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return false;
    }
}
