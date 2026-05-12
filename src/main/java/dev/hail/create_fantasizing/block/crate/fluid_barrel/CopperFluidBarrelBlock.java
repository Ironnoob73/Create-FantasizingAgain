package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CopperFluidBarrelBlock extends AbstractFluidBarrelBlock {
    public CopperFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends CopperFluidBarrelEntity> getBlockEntityType() {
        return CFABlocks.COPPER_FLUID_BARREL_ENTITY.get();
    }
}
