package dev.hail.create_fantasizing.block.compat_engine;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SculkEngineBlock extends CompactEngineBlock{
    public SculkEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockEntityType<? extends CompactEngineEntity> getBlockEntityType() {
        return CFABlocks.SCULK_ENGINE_ENTITY.get();
    }
}
