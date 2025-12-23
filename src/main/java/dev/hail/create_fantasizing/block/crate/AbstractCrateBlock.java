package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.content.logistics.crate.CrateBlock;
import com.simibubi.create.foundation.block.IBE;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public abstract class AbstractCrateBlock extends CrateBlock {

    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public AbstractCrateBlock(Properties p_i48415_1_) {
        super(p_i48415_1_);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP)
                .setValue(DOUBLE, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
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
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && state.hasBlockEntity() && state.getValue(DOUBLE)) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (!(blockEntity instanceof AbstractCrateEntity be))
                return;

            AbstractCrateEntity other = be.getOtherCrate();
            if (other == null)
                return;

            if (state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                onMerge(be, other);
            } else {
                onMerge(other, be);
            }

        }
    }

    public void onMerge(AbstractCrateEntity be, AbstractCrateEntity other){
        for (int slot = 0; slot < other.inventory.getSlots()/2; slot++) {
            be.inventory.setStackInSlot(slot + be.invSize/2, other.inventory.getStackInSlot(slot));
            other.inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        be.allowedAmount += other.allowedAmount;
        other.invHandler.invalidate();
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

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, world, pos, newState);
    }
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof AbstractCrateEntity abstractCrateEntity) {
            abstractCrateEntity.onSplit();
            ItemStack itemstack = new ItemStack(blockState.getBlock());
            //CompoundTag compoundTag = abstractCrateEntity.getUpdateTag().copy();
            //compoundTag.remove("Main");
            //abstractCrateEntity.handleUpdateTag(compoundTag);
            abstractCrateEntity.saveToItem(itemstack);
            return Collections.singletonList(itemstack);
        }
        return super.getDrops(blockState, builder);
    }
}
