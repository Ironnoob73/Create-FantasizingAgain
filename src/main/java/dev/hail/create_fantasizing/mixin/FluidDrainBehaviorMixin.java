package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import dev.hail.create_fantasizing.block.fluid.CFAFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidDrainingBehaviour.class)
public class FluidDrainBehaviorMixin {
    @Inject(method = "pullNext", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z",
            shift = At.Shift.AFTER))
    private static void injected(BlockPos root, boolean simulate, CallbackInfoReturnable<Boolean> cir,
                                 @Local(name = "blockState") BlockState blockState, @Local(name = "emptied") BlockState emptied, @Local(name = "fluid") Fluid fluid){
        if (blockState.getBlock() == Blocks.POWDER_SNOW) {
            emptied = Blocks.AIR.defaultBlockState();
            fluid = CFAFluids.POWDER_SNOW.get();
        }
    }
}
