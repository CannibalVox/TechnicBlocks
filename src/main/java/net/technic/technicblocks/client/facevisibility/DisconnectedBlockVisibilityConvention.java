package net.technic.technicblocks.client.facevisibility;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.technic.technicblocks.blocks.DataDrivenBlock;
import net.technic.technicblocks.blocks.connections.ConnectionConvention;
import net.technic.technicblocks.client.renderer.context.IRenderContext;

public class DisconnectedBlockVisibilityConvention extends OpaqueBlockVisibilityConvention {
    @Override
    protected boolean isSideVisible(IBlockAccess world, int x, int y, int z, ForgeDirection direction, DataDrivenBlock thisBlock, DataDrivenBlock otherBlock, IRenderContext connections) {
        if (!super.isSideVisible(world, x, y, z, direction, thisBlock, otherBlock, connections))
            return false;

        ConnectionConvention connection = thisBlock.getBlockModel().getModelConnectionsConvention();
        if (connection.testConnection(thisBlock, world.getBlockMetadata(x, y, z), otherBlock, world.getBlockMetadata(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ)))
            return false;

        return true;
    }
}
