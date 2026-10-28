package dev.hail.create_fantasizing.mixin.client;

import com.simibubi.create.AllPartialModels;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.hail.create_fantasizing.block.CFAPartialModels;
import dev.hail.create_fantasizing.block.sturdy_girder.CFAGirderRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * By Deepseek V4.1 Flash 1017:
 * Flywheel counterpart of {@link TrackRendererMixin}. The visual is a private
 * inner class, so its girder partial models are swapped through the model
 * argument instead of redirecting the surrounding code.
 */
@Mixin(targets = "com.simibubi.create.content.trains.track.TrackVisual$BezierTrackVisual$GirderVisual")
public abstract class TrackVisualGirderVisualMixin {

    @ModifyArg(
            method = "*",
            at = @At(value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/render/SpecialModels;flatChunk(Ldev/engine_room/flywheel/lib/model/baked/PartialModel;)Ldev/engine_room/flywheel/api/model/Model;"),
            index = 0
    )
    private static PartialModel cfa$sturdyGirderModel(PartialModel model) {
        if (!CFAGirderRenderContext.isSturdy())
            return model;

        if (model == AllPartialModels.GIRDER_SEGMENT_MIDDLE)
            return CFAPartialModels.STURDY_GIRDER_SEGMENT_MIDDLE;
        if (model == AllPartialModels.GIRDER_SEGMENT_TOP)
            return CFAPartialModels.STURDY_GIRDER_SEGMENT_TOP;
        if (model == AllPartialModels.GIRDER_SEGMENT_BOTTOM)
            return CFAPartialModels.STURDY_GIRDER_SEGMENT_BOTTOM;
        return model;
    }
}
