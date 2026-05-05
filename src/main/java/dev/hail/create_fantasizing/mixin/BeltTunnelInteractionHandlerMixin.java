package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.BeltTunnelInteractionHandler;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ChromaticTunnelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltTunnelInteractionHandler.class)
public class BeltTunnelInteractionHandlerMixin {
    @Inject(method = "flapTunnelsAndCheckIfStuck(Lcom/simibubi/create/content/kinetics/belt/transport/BeltInventory;Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;F)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I",
                    shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/transport/BeltTunnelInteractionHandler;getTunnelOnSegment(Lcom/simibubi/create/content/kinetics/belt/transport/BeltInventory;I)Lcom/simibubi/create/content/logistics/tunnel/BeltTunnelBlockEntity;"),
                    to = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/tunnel/BrassTunnelBlockEntity;hasDistributionBehaviour()Z")
            )
    )
    private static void injected(BeltInventory beltInventory, TransportedItemStack current, float nextOffset, CallbackInfoReturnable<Boolean> cir,
                                 @Local(name = "nextTunnel") BeltTunnelBlockEntity nextTunnel) {
        if (nextTunnel instanceof ChromaticTunnelBlockEntity chromaticTunnel) {
            chromaticTunnel.setStackToProcess(current, false);
        }
    }
}
