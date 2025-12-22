package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AndesiteCrateBlock extends AbstractCrateBlock implements IBE<AndesiteCrateEntity> {

    public AndesiteCrateBlock(Properties p_i48415_1_) {super(p_i48415_1_);}

    @Override
    public Class<AndesiteCrateEntity> getBlockEntityClass() {
        return AndesiteCrateEntity.class;
    }

    @Override
    public BlockEntityType<? extends AndesiteCrateEntity> getBlockEntityType() {
        return CFABlocks.ANDESITE_CRATE_ENTITY.get();
    }

    /*@Override
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }
    /*@Override
    public BlockEntity createBlockEntity(BlockState state, IBlockReader world) {
        return AllTileEntities.ADJUSTABLE_CRATE.create();
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && state.hasBlockEntity() && state.getValue(DOUBLE)
                && state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (!(blockEntity instanceof AndesiteCrateEntity be))
                return;

            AndesiteCrateEntity other = be.getOtherCrate();
            if (other == null)
                return;

            for (int slot = 0; slot < other.inventory.getSlots(); slot++) {
                be.inventory.setStackInSlot(slot, other.inventory.getStackInSlot(slot));
                other.inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
            be.allowedAmount = other.allowedAmount;
            other.invHandler.invalidate();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                          BlockHitResult hit) {

        if (player.isCrouching())
            return InteractionResult.PASS;

        if (player instanceof FakePlayer)
            return InteractionResult.PASS;
        if (worldIn.isClientSide)
            return InteractionResult.SUCCESS;

        withBlockEntityDo(worldIn, pos,
                crate -> NetworkHooks.openScreen((ServerPlayer) player, crate, crate::sendToMenu));
        return InteractionResult.SUCCESS;
    }

    public static void splitCrate(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!CFABlocks.ANDESITE_CRATE.has(state))
            return;
        if (!state.getValue(DOUBLE))
            return;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof AndesiteCrateEntity crateBe))
            return;
        crateBe.onSplit();
        world.setBlockAndUpdate(pos, state.setValue(DOUBLE, false));
        world.setBlockAndUpdate(crateBe.getOtherCrate().getBlockPos(), state.setValue(DOUBLE, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean isMoving) {
        if (pState.hasBlockEntity() && (!pNewState.hasBlockEntity() || !(pNewState.getBlock() instanceof AndesiteCrateBlock)))
            pLevel.removeBlockEntity(pPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof AndesiteCrateEntity) {
            AndesiteCrateEntity flexCrateBlockEntity = ((AndesiteCrateEntity) be).getMainCrate();
            return ItemHelper.calcRedstoneFromInventory(flexCrateBlockEntity.inventory);
        }
        return 0;
    }
}
