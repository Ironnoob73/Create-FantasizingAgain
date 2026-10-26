package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DepotBehaviour.class)
public class DepotBehaviorMixin {
    @Inject(method = "tick()V", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    private void injected(CallbackInfo ci, @Local(name = "world") Level world, @Local(name = "pos") BlockPos pos) {
        BeltProcessingBehaviour processingBehaviour = BlockEntityBehaviour.get(world, pos.above(), BeltProcessingBehaviour.TYPE);
        if (processingBehaviour == null)
            ci.cancel();
    }
}
