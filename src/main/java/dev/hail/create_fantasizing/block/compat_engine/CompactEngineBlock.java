package dev.hail.create_fantasizing.block.compat_engine;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class CompactEngineBlock extends DirectionalKineticBlock implements IBE<CompactEngineEntity> {
    public CompactEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }
    @Override
    public boolean hideStressImpact() {
        return true;
    }
    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }
    @Override
    public Class<CompactEngineEntity> getBlockEntityClass() {
        return CompactEngineEntity.class;
    }

    public static class CompactHydraulicEngineGenerator extends SpecialBlockStateGen {

        @Override
        protected int getXRotation(BlockState state) {
            return state.getValue(CompactEngineBlock.FACING) == Direction.DOWN ? 180 : 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            return state.getValue(CompactEngineBlock.FACING)
                    .getAxis()
                    .isVertical() ? 0 : horizontalAngle(state.getValue(CompactEngineBlock.FACING));
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            return state.getValue(CompactEngineBlock.FACING)
                    .getAxis()
                    .isVertical() ? AssetLookup.partialBaseModel(ctx, prov, "vertical")
                    : AssetLookup.partialBaseModel(ctx, prov);
        }

    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getBlockEntityType().create(blockPos, blockState);
    }
}
