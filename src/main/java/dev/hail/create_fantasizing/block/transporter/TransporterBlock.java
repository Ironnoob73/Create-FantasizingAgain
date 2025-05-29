package dev.hail.create_fantasizing.block.transporter;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TransporterBlock extends Block implements IWrenchable, IBE<TransporterEntity> {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public TransporterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(FACING, Direction.DOWN));
    }
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (level.isClientSide)
            return;
        if (!level.getBlockTicks().willTickThisTick(pos, this))
            level.scheduleTick(pos, this, 1);
    }
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        boolean previouslyPowered = state.getValue(POWERED);
        if (previouslyPowered != worldIn.hasNeighborSignal(pos))
            worldIn.setBlock(pos, state.cycle(POWERED), 2);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        for (Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate = state.setValue(FACING, direction.getOpposite());
            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos()))
                return blockstate.setValue(POWERED, state.getValue(POWERED));
        }
        return state;
    }

    @Override
    public Class<TransporterEntity> getBlockEntityClass() {
        return TransporterEntity.class;
    }
    @Override
    public BlockEntityType<? extends TransporterEntity> getBlockEntityType() {
        return CFABlocks.TRANSPORTER_ENTITY.get();
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWERED,FACING));
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isClientSide)
            return;
        ItemStack stack = ItemHelper.fromItemEntity(entityIn);
        if (stack.isEmpty())
            return;
        if (state.getValue(POWERED))
            return;

        Direction direction = state.getValue(FACING);
        Vec3 openPos = VecHelper.getCenterOf(pos)
                .add(Vec3.atLowerCornerOf(direction.getNormal())
                        .scale(entityIn instanceof ItemEntity ? -.25f : -.125f));
        Vec3 diff = entityIn.position()
                .subtract(openPos);
        double projectedDiff = direction.getAxis()
                .choose(diff.x, diff.y, diff.z);
        if (projectedDiff < 0 == (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE))
            return;
        float yOffset = direction == Direction.UP ? 0.25f : direction == Direction.DOWN ? -0.5f : -0.5f;
        if (!PackageEntity.centerPackage(entityIn, openPos.add(0, yOffset, 0)))
            return;

        ItemStack remainder = tryInsert(worldIn, pos, stack, false);
        if (remainder.isEmpty())
            entityIn.discard();
        if (remainder.getCount() < stack.getCount() && entityIn instanceof ItemEntity)
            ((ItemEntity) entityIn).setItem(remainder);
    }
    public static ItemStack tryInsert(Level worldIn, BlockPos pos, ItemStack toInsert, boolean simulate) {
        FilteringBehaviour filter = BlockEntityBehaviour.get(worldIn, pos, FilteringBehaviour.TYPE);
        InvManipulationBehaviour inserter = BlockEntityBehaviour.get(worldIn, pos, InvManipulationBehaviour.TYPE);
        if (inserter == null)
            return toInsert;
        if (filter != null && !filter.test(toInsert))
            return toInsert;
        if (simulate)
            inserter.simulate();
        ItemStack insert = inserter.insert(toInsert);

        if (!simulate && insert.getCount() != toInsert.getCount()) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof FunnelBlockEntity funnelBlockEntity) {
                funnelBlockEntity.onTransfer(toInsert);
                if (funnelBlockEntity.hasFlap())
                    funnelBlockEntity.flap(true);
            }
        }
        return insert;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }
    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

}
