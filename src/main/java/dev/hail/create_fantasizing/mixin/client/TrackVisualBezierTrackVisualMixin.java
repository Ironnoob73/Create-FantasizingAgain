package dev.hail.create_fantasizing.mixin.client;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackVisual;
import dev.hail.create_fantasizing.block.sturdy_girder.CFABezierGirder;
import dev.hail.create_fantasizing.block.sturdy_girder.CFAGirderRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * By Deepseek V4.1 Flash 1017:
 * Marks the duration in which a curved track's girder visual is being built, so
 * the nested girder visual can pick the matching segment models.
 */
@Mixin(targets = "com.simibubi.create.content.trains.track.TrackVisual$BezierTrackVisual")
public abstract class TrackVisualBezierTrackVisualMixin {

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void cfa$beginGirderVisual(TrackVisual outer, BezierConnection connection, CallbackInfo ci) {
        CFAGirderRenderContext.set(connection instanceof CFABezierGirder girder && girder.isSturdyGirder());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private static void cfa$endGirderVisual(TrackVisual outer, BezierConnection connection, CallbackInfo ci) {
        CFAGirderRenderContext.clear();
    }
}
