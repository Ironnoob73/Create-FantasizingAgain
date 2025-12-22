package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.content.logistics.crate.CrateBlock;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class AbstractCrateBlock extends CrateBlock {

    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public AbstractCrateBlock(Properties p_i48415_1_) {
        super(p_i48415_1_);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP)
                .setValue(DOUBLE, false));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                  BlockPos currentPos, BlockPos facingPos) {

        boolean isDouble = stateIn.getValue(DOUBLE);
        Direction blockFacing = stateIn.getValue(FACING);
        boolean isFacingOther = facingState.getBlock() == this && facingState.getValue(DOUBLE)
                && facingState.getValue(FACING) == facing.getOpposite();

        if (!isDouble) {
            if (!isFacingOther)
                return stateIn;
            return stateIn.setValue(DOUBLE, true)
                    .setValue(FACING, facing);
        }

        if (facing != blockFacing)
            return stateIn;
        if (!isFacingOther)
            return stateIn.setValue(DOUBLE, false);

        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();

        if (context.getPlayer() == null || !context.getPlayer()
                .isShiftKeyDown()) {
            for (Direction d : Iterate.directions) {
                BlockState state = world.getBlockState(pos.relative(d));
                if (state.getBlock() == this && !state.getValue(DOUBLE))
                    return defaultBlockState().setValue(FACING, d)
                            .setValue(DOUBLE, true);
            }
        }

        Direction placedOnFace = context.getClickedFace()
                .getOpposite();
        BlockState state = world.getBlockState(pos.relative(placedOnFace));
        if (state.getBlock() == this && !state.getValue(DOUBLE))
            return defaultBlockState().setValue(FACING, placedOnFace)
                    .setValue(DOUBLE, true);
        return defaultBlockState();
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return originalState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(DOUBLE));
    }

}
