package dev.hail.create_fantasizing.block.compat_engine.water;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CompactHydraulicEngineBlock extends DirectionalKineticBlock implements IBE<CompactHydraulicEngineEntity> {
    public CompactHydraulicEngineBlock(Properties properties) {
        super(properties);
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
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
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
    @Override
    public Class<CompactHydraulicEngineEntity> getBlockEntityClass() {
        return CompactHydraulicEngineEntity.class;
    }
    @Override
    public BlockEntityType<? extends CompactHydraulicEngineEntity> getBlockEntityType() {
        return CFABlocks.COMPACT_HYDRAULIC_ENGINE_ENTITY.get();
    }

    public static class CompactHydraulicEngineGenerator extends SpecialBlockStateGen {

        @Override
        protected int getXRotation(BlockState state) {
            return state.getValue(CompactHydraulicEngineBlock.FACING) == Direction.DOWN ? 180 : 0;
        }

        @Override
        protected int getYRotation(BlockState state) {
            return state.getValue(CompactHydraulicEngineBlock.FACING)
                    .getAxis()
                    .isVertical() ? 0 : horizontalAngle(state.getValue(CompactHydraulicEngineBlock.FACING));
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            return state.getValue(CompactHydraulicEngineBlock.FACING)
                    .getAxis()
                    .isVertical() ? AssetLookup.partialBaseModel(ctx, prov, "vertical")
                    : AssetLookup.partialBaseModel(ctx, prov);
        }

    }
}
