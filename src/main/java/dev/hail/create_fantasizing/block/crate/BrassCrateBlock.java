package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrassCrateBlock extends AbstractCrateBlock{
    public BrassCrateBlock(Properties properties) {super(properties);}

    @Override
    public BlockEntityType<? extends BrassCrateEntity> getBlockEntityType() {
        return CFABlocks.BRASS_CRATE_ENTITY.get();
    }
}
