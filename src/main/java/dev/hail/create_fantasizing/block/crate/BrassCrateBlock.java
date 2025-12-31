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
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrassCrateBlock extends AbstractCrateBlock implements IBE<BrassCrateEntity> {
    public BrassCrateBlock(Properties properties) {super(properties);}
    @Override
    public Class<BrassCrateEntity> getBlockEntityClass() {
        return BrassCrateEntity.class;
    }
    @Override
    public BlockEntityType<? extends BrassCrateEntity> getBlockEntityType() {
        return CFABlocks.BRASS_CRATE_ENTITY.get();
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                                    BlockHitResult hit) {
        super.useItemOn(stack,state,worldIn,pos,player,handIn,hit);
        withBlockEntityDo(worldIn, pos, crate -> player.openMenu((MenuProvider) crate.getMainCrate(), crate.getMainCrate()::sendToMenu));
        return ItemInteractionResult.SUCCESS;
    }
}
