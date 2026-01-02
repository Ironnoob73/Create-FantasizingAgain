package dev.hail.create_fantasizing.block.rose_quartz_blub;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RoseQuartzBulb extends DirectionalBlock implements SimpleWaterloggedBlock{
    public static final MapCodec<RoseQuartzBulb> CODEC = simpleCodec(RoseQuartzBulb::new);
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
    public RoseQuartzBulb(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape shape = Block.box(5, 0, 5, 11, 8, 11);
        switch (blockState.getValue(FACING)) {
            case Direction.DOWN -> shape = Block.box(5, 8, 5, 11, 16, 11);
            case Direction.NORTH -> shape = Block.box(5, 2, 13, 11, 14, 16);
            case Direction.SOUTH -> shape = Block.box(5, 2, 0, 11, 14, 3);
            case Direction.EAST -> shape = Block.box(0, 2, 5, 3, 14, 11);
            case Direction.WEST -> shape = Block.box(13, 2, 5, 16, 14, 11);
        }
        return shape;
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, WATERLOGGED));
    }
    @Override
    protected BlockState rotate(BlockState p_154354_, Rotation p_154355_) {
        return p_154354_.setValue(FACING, p_154355_.rotate(p_154354_.getValue(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState p_154351_, Mirror p_154352_) {
        return p_154351_.setValue(FACING, p_154352_.mirror(p_154351_.getValue(FACING)));
    }
}
