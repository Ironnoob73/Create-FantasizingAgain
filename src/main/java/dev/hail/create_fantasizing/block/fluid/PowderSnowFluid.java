package dev.hail.create_fantasizing.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PowderSnowFluid extends BaseFlowingFluid {
    protected PowderSnowFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return fluidState.isSource();
    }
    @Override
    public int getAmount(FluidState fluidState) {
        return fluidState.getValue(LEVEL);
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (!level.isClientSide){
            if(state.isSource())
                level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 11);
            else
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        }
    }

    public static class Source extends PowderSnowFluid {
        public Source(Properties properties) {
            super(properties);
        }
        public int getAmount(FluidState state) {
            return 8;
        }
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
