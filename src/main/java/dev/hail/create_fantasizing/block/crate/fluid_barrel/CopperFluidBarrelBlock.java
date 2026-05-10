package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.block.IBE;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CopperFluidBarrelBlock extends AbstractFluidBarrelBlock implements IBE<CopperFluidBarrelEntity> {
    public CopperFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CopperFluidBarrelEntity> getBlockEntityClass() {
        return CopperFluidBarrelEntity.class;
    }

    @Override
    public BlockEntityType<? extends CopperFluidBarrelEntity> getBlockEntityType() {
        return CFABlocks.COPPER_FLUID_BARREL_ENTITY.get();
    }
}
