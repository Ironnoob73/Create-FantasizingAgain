package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.block.IBE;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
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
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                                    BlockHitResult hit) {

        if (player.isCrouching())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (player instanceof FakePlayer)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (worldIn.isClientSide)
            return ItemInteractionResult.SUCCESS;

        withBlockEntityDo(worldIn, pos, crate -> player.openMenu((MenuProvider) crate.getMainCrate(), crate.getMainCrate()::sendToMenu));
        return ItemInteractionResult.SUCCESS;
    }
}
