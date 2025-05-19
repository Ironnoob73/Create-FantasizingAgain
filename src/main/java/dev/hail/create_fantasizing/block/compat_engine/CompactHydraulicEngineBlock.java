package dev.hail.create_fantasizing.block.compat_engine;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CompactHydraulicEngineBlock extends CompactEngineBlock{
    public CompactHydraulicEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntityType<? extends CompactEngineEntity> getBlockEntityType() {
        return CFABlocks.COMPACT_HYDRAULIC_ENGINE_ENTITY.get();
    }
}
