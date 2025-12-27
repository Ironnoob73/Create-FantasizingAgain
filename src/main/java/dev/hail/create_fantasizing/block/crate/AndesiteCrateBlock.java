package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
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

    public AndesiteCrateBlock(Properties properties) {super(properties);}

    @Override
    public Class<AndesiteCrateEntity> getBlockEntityClass() {
        return AndesiteCrateEntity.class;
    }

    @Override
    public BlockEntityType<? extends AndesiteCrateEntity> getBlockEntityType() {
        return CFABlocks.ANDESITE_CRATE_ENTITY.get();
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
                crate -> NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) crate.getMainCrate(), crate.getMainCrate()::sendToMenu));
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
}
