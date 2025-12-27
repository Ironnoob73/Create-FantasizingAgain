package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.Contraption;
import dev.hail.create_fantasizing.block.crate.AbstractCrateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Queue;
import java.util.Set;

@Mixin(Contraption.class)
public class ContraptionMixin {
    @Inject(method = "moveBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;",
            shift = At.Shift.AFTER))
    private void injected(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited,
                          CallbackInfoReturnable<Boolean> cir, @Local(name = "pos") BlockPos pos, @Local(name = "state") BlockState state) {
        if (state.hasProperty(AbstractCrateBlock.DOUBLE) && state.hasProperty(AbstractCrateBlock.FACING)
                && state.getValue(AbstractCrateBlock.DOUBLE)) {
            Direction offset = state.getValue(AbstractCrateBlock.FACING);
            BlockPos attached = pos.relative(offset);
            if (!visited.contains(attached))
                frontier.add(attached);
        }
    }
}
