package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IronCrateBlock extends AbstractCrateBlock{
    public IronCrateBlock(Properties properties) {super(properties);}

    @Override
    public BlockEntityType<? extends IronCrateEntity> getBlockEntityType() {
        return CFABlocks.IRON_CRATE_ENTITY.get();
    }
}