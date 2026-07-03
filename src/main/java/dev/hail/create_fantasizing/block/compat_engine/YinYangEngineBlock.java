package dev.hail.create_fantasizing.block.compat_engine;

import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class YinYangEngineBlock extends CreativeMotorBlock {

    public YinYangEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null) {
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            if (nearestLookingDirection.getAxis() != Direction.Axis.Y)
                return defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer()
                        .isShiftKeyDown() ? nearestLookingDirection.getClockWise() : nearestLookingDirection.getOpposite().getClockWise());
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public BlockEntityType<YinYangEngineEntity> getBlockEntityType() {
        return CFABlocks.YIN_YANG_ENGINE_ENTITY.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(FACING).getAxis();
    }
}
