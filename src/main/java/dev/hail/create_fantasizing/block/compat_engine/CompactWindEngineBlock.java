package dev.hail.create_fantasizing.block.compat_engine;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CompactWindEngineBlock extends CompactEngineBlock{
    public CompactWindEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntityType<? extends CompactEngineEntity> getBlockEntityType() {
        return CFABlocks.COMPACT_WIND_ENGINE_ENTITY.get();
    }
}
