package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.trains.track.TrackBlockItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.sturdy_girder.CFAGirderTypeTracker;
import dev.hail.create_fantasizing.data.CFATriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * By Deepseek V4.1 Flash 1017:
 * Lets holding a Sturdy Girder in the off hand build curved track supports,
 * mirroring Create's Metal Girder behaviour.
 */
@Mixin(TrackBlockItem.class)
public abstract class TrackBlockItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"))
    private void cfa$rememberGirderType(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = pContext.getPlayer();
        CFAGirderTypeTracker.set(player != null && CFABlocks.STURDY_GIRDER.isIn(player.getOffhandItem()));
    }

    @Inject(method = "useOn", at = @At("RETURN"))
    private void cfa$clearGirderType(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir) {
        CFAGirderTypeTracker.clear();
    }

    @Redirect(
            method = "useOn",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z")
    )
    private boolean cfa$acceptSturdyGirder(BlockEntry<?> entry, ItemStack stack, UseOnContext pContext) {
        if (entry.isIn(stack) || CFABlocks.STURDY_GIRDER.isIn(stack))
            if (!pContext.getLevel().isClientSide && pContext.getPlayer() instanceof ServerPlayer serverPlayer)
                CFATriggers.STURDY_GIRDER_TRACK.get().trigger(serverPlayer);
        return entry.isIn(stack) || CFABlocks.STURDY_GIRDER.isIn(stack);
    }
}
