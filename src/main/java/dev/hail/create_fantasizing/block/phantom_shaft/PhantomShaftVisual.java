package dev.hail.create_fantasizing.block.phantom_shaft;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.hail.create_fantasizing.block.CFAPartialModels;

public class PhantomShaftVisual extends BracketedKineticBlockEntityVisual {

    public static BlockEntityVisual<BracketedKineticBlockEntity> create(VisualizationContext context, BracketedKineticBlockEntity blockEntity, float partialTick) {
        //if (ICogWheel.isLargeCog(blockEntity.getBlockState())) {
        //    return new LargeCogVisual(context, blockEntity, partialTick);
        //} else {
            Model model;
            //if (AllBlocks.COGWHEEL.is(blockEntity.getBlockState().getBlock())) {
            //    model = Models.partial(AllPartialModels.COGWHEEL);
            //} else {
                model = Models.partial(CFAPartialModels.PHANTOM_SHAFT);
            //}
            return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, model);
        //}
    }
}
