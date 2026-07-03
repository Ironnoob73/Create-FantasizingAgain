package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class DiamondFluidBarrelBlock extends AbstractFluidBarrelBlock {
    public DiamondFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends DiamondFluidBarrelEntity> getBlockEntityType() {
        return CFABlocks.DIAMOND_FLUID_BARREL_ENTITY.get();
    }
}