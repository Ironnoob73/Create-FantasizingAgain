package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShaftBlock.class)
public class ShaftBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void injected(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir){
        if ((!player.isShiftKeyDown() || player.mayBuild()) && stack.getItem() == Items.PHANTOM_MEMBRANE){
            if (level.isClientSide)
                cir.setReturnValue(ItemInteractionResult.SUCCESS);
            CFABlocks.PHANTOM_SHAFT.get().handlePhantom(state, level, pos);

            BlockState newState = level.getBlockState(pos);
            SoundType soundType = newState.getSoundType();
            level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            cir.setReturnValue(ItemInteractionResult.SUCCESS);
        }
    }
}
