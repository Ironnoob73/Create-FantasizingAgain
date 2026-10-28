package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import dev.hail.create_fantasizing.data.CFATriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ChromaticTunnelBlock extends BeltTunnelBlock {

    public ChromaticTunnelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!isValidPositionForPlacement(state, worldIn, pos))
            return false;
        if (!AllBlocks.DEPOT.has(blockState))
            return super.canSurvive(state, worldIn, pos);
        return true;
    }

    @Override
    public boolean isValidPositionForPlacement(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.below());
        if (!AllBlocks.DEPOT.has(blockState))
            return super.isValidPositionForPlacement(state, worldIn, pos);
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (AllBlocks.DEPOT.has(context.getLevel().getBlockState(context.getClickedPos().below())))
            for (Direction direction : context.getNearestLookingDirections()) {
                if (direction.getAxis().isVertical()) continue;
                BlockState blockstate = defaultBlockState().setValue(BeltTunnelBlock.HORIZONTAL_AXIS, direction.getAxis());
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    if (!context.getLevel().isClientSide && context.getPlayer() instanceof ServerPlayer serverPlayer)
                        CFATriggers.CHROMATIC_TUNNEL_PUT_ON_DEPOT.get().trigger(serverPlayer);
                    return blockstate;
                }
            }
        return super.getStateForPlacement(context);
    }
}
