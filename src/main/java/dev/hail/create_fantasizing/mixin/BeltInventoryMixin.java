package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ChromaticTunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltInventory.class)
public class BeltInventoryMixin {
    @Mutable
    @Final
    @Shadow
    BeltBlockEntity belt;

    @Inject(
            method = "getBeltProcessingAtSegment(I)Lcom/simibubi/create/content/kinetics/belt/behaviour/BeltProcessingBehaviour;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(int segment, CallbackInfoReturnable<BeltProcessingBehaviour> cir) {
        if (belt.getLevel() != null) {
            BlockEntity blockEntity = belt.getLevel().getBlockEntity(BeltHelper.getPositionForOffset(belt, segment).above());
            if (blockEntity instanceof ChromaticTunnelBlockEntity){
                BeltProcessingBehaviour tunnelProcessBehavior = BlockEntityBehaviour.get(belt.getLevel(), BeltHelper.getPositionForOffset(belt, segment)
                        .above(), BeltProcessingBehaviour.TYPE);
                cir.setReturnValue(tunnelProcessBehavior);
            }
        }
    }

    @WrapOperation(
            method = "handleBeltProcessingAndCheckIfRemoved(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;FZ)Z",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/belt/behaviour/BeltProcessingBehaviour;isBlocked(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean wrap(BlockGetter world, BlockPos processingSpace, Operation<Boolean> original,
                         @Local(name = "segment") int segment,
                         @Local(name = "processingBehaviour") BeltProcessingBehaviour processingBehaviour) {
        if (belt.getLevel() != null) {
            BlockEntity blockEntity = belt.getLevel().getBlockEntity(BeltHelper.getPositionForOffset(belt, segment).above());
            if (blockEntity instanceof ChromaticTunnelBlockEntity && processingBehaviour.blockEntity instanceof ChromaticTunnelBlockEntity){
                return false;
            }
        }
        return original.call(world, processingSpace);
    }
}
