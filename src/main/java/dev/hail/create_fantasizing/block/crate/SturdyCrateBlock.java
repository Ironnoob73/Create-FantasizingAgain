package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SturdyCrateBlock extends AbstractCrateBlock {
    public SturdyCrateBlock(Properties properties) {super(properties);}

    @Override
    public BlockEntityType<? extends SturdyCrateEntity> getBlockEntityType() {
        return CFABlocks.STURDY_CRATE_ENTITY.get();
    }
}
