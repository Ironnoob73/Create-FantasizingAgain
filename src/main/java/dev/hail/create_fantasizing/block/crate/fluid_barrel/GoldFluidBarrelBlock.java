package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GoldFluidBarrelBlock extends AbstractFluidBarrelBlock {
    public GoldFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends GoldFluidBarrelEntity> getBlockEntityType() {
        return CFABlocks.GOLD_FLUID_BARREL_ENTITY.get();
    }
}
