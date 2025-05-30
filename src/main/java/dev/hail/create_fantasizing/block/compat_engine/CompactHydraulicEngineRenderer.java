package dev.hail.create_fantasizing.block.compat_engine;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.hail.create_fantasizing.block.CFAPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class CompactHydraulicEngineRenderer extends KineticBlockEntityRenderer<CompactEngineEntity> {

    public CompactHydraulicEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(CompactEngineEntity be, BlockState state) {
        return CachedBuffers.partialFacing(CFAPartialModels.COMPACT_HYDRAULIC_ENGINE_HEART, state);
    }
}
