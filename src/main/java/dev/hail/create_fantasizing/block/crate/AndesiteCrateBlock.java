package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AndesiteCrateBlock extends AbstractCrateBlock {
    public AndesiteCrateBlock(Properties properties) {super(properties);}

    @Override
    public BlockEntityType<? extends AndesiteCrateEntity> getBlockEntityType() {
        return CFABlocks.ANDESITE_CRATE_ENTITY.get();
    }
}
