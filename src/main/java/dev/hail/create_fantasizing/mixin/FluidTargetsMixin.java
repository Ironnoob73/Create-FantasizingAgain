package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.fluids.pipes.VanillaFluidTargets;
import dev.hail.create_fantasizing.block.fluid.CFAFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanillaFluidTargets.class)
public class FluidTargetsMixin {
    @Inject(method = "canProvideFluidWithoutCapability", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injected(BlockState state, CallbackInfoReturnable<Boolean> cir){
        if (state.is(Blocks.POWDER_SNOW_CAULDRON))
            cir.setReturnValue(true);
        if (state.is(Blocks.POWDER_SNOW))
            cir.setReturnValue(true);
    }

    @Inject(method = "drainBlock",  at = @At("HEAD"), cancellable = true, remap = false)
    private static void injected(Level level, BlockPos pos, BlockState state, boolean simulate, CallbackInfoReturnable<FluidStack> cir){
        if (state.is(Blocks.POWDER_SNOW_CAULDRON) && state.getBlock() instanceof LayeredCauldronBlock lcb) {
            if (!lcb.isFull(state))
                cir.setReturnValue(FluidStack.EMPTY);
            if (!simulate)
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            cir.setReturnValue(new FluidStack(CFAFluids.POWDER_SNOW.get(), 1000));
        }

        if (state.is(Blocks.POWDER_SNOW)) {
            if (!simulate)
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            cir.setReturnValue(new FluidStack(CFAFluids.POWDER_SNOW.get(), 1000));
        }
    }
}
