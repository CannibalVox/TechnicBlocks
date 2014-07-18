package net.technic.technicblocks.blocks.selection;

public class WallSelection extends FenceSelection {
    @Override
    protected float getTrimAmount() {
        return 0.25f;
    }

    @Override
    protected float getVerticalTrimAmount() { return 0.1875f; }

    @Override
    protected float getNoPostTrimAmount() { return 0.0625f; }
}
