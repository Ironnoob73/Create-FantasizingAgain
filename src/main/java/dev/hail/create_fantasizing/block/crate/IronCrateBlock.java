package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.block.IBE;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IronCrateBlock extends AbstractCrateBlock implements IBE<IronCrateEntity> {
    public IronCrateBlock(Properties properties) {super(properties);}
    @Override
    public Class<IronCrateEntity> getBlockEntityClass() {
        return IronCrateEntity.class;
    }
    @Override
    public BlockEntityType<? extends IronCrateEntity> getBlockEntityType() {
        return CFABlocks.IRON_CRATE_ENTITY.get();
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
}