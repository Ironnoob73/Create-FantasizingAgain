package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.trains.track.TrackPaver;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * By Deepseek V4.1 Flash 1017:
 * Makes the track paver treat Sturdy Girders like Metal Girders when paving a
 * curved track, so holding a Sturdy Girder in the off hand consumes it for the
 * curved support just as Create does for its own girder.
 */
@Mixin(TrackPaver.class)
public abstract class TrackPaverMixin {

    @Inject(method = "isWallLike", at = @At("HEAD"), cancellable = true)
    private static void cfa$sturdyGirderIsWallLike(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (CFABlocks.STURDY_GIRDER.has(state))
            cir.setReturnValue(true);
    }

    @Redirect(
            method = "paveCurve",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private static boolean cfa$countSturdyGirderPavement(BlockEntry<?> entry, BlockState state) {
        return entry.has(state) || CFABlocks.STURDY_GIRDER.has(state);
    }
}
