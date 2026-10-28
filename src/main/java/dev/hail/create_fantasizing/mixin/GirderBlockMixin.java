package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.decoration.girder.GirderBlock;
import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Added By Deepseek V4.1 Flash 1017:
@Mixin(GirderBlock.class)
public class GirderBlockMixin {
    // Create only handles metal girders in GirderBlock's extension helper; route sturdy girders through their own helper.
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void cfa$extendWithSturdyGirder(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                            Player player, InteractionHand hand, BlockHitResult hitResult,
                                            CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (player == null || !(stack.getItem() instanceof BlockItem blockItem)
                || !(blockItem.getBlock() instanceof SturdyGirderBlock))
            return;

        IPlacementHelper helper = SturdyGirderBlock.getPlacementHelper();
        if (!helper.matchesItem(stack) || !helper.matchesState(state))
            return;

        cir.setReturnValue(helper.getOffset(player, level, state, pos, hitResult)
                .placeInWorld(level, blockItem, player, hand, hitResult));
    }
}
