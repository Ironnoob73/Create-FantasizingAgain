package dev.hail.create_fantasizing.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.hail.create_fantasizing.block.CFAPartialModels;
import dev.hail.create_fantasizing.block.sturdy_girder.CFABezierGirder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * By Deepseek V4.1 Flash 1017:
 * Uses the Sturdy Girder segment models when the track renderer draws a curve
 * that was built with Sturdy Girders in the off hand.
 */
@Mixin(TrackRenderer.class)
public abstract class TrackRendererMixin {

    @Unique
    private static final ThreadLocal<BezierConnection> cfa$currentConnection = new ThreadLocal<>();

    @Inject(method = "renderGirder", at = @At("HEAD"))
    private static void cfa$beginGirder(Level level, BezierConnection connection, PoseStack ms,
                                        VertexConsumer vb, BlockPos tePosition, CallbackInfo ci) {
        cfa$currentConnection.set(connection);
    }

    @Inject(method = "renderGirder", at = @At("RETURN"))
    private static void cfa$endGirder(Level level, BezierConnection connection, PoseStack ms,
                                      VertexConsumer vb, BlockPos tePosition, CallbackInfo ci) {
        cfa$currentConnection.remove();
    }

    @ModifyArg(
            method = "renderGirder",
            at = @At(value = "INVOKE",
                    target = "Lnet/createmod/catnip/render/CachedBuffers;partial(Ldev/engine_room/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/createmod/catnip/render/SuperByteBuffer;"),
            index = 0
    )
    private static PartialModel cfa$sturdyGirderModel(PartialModel model) {
        BezierConnection connection = cfa$currentConnection.get();
        if (!(connection instanceof CFABezierGirder girder) || !girder.isSturdyGirder())
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
