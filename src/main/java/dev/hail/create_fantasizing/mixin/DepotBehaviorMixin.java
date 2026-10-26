package dev.hail.create_fantasizing.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DepotBehaviour.class)
public class DepotBehaviorMixin extends BlockEntityBehaviour{
    @Shadow
    TransportedItemStack heldItem;
    @Shadow
    TransportedItemStackHandlerBehaviour transportedHandler;

    public DepotBehaviorMixin(SmartBlockEntity be) {
        super(be);
    }

    @Inject(method = "tick()V", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    private void injected(CallbackInfo ci, @Local(name = "world") Level world, @Local(name = "pos") BlockPos pos) {
        BeltProcessingBehaviour processingBehaviour = BlockEntityBehaviour.get(world, pos.above(), BeltProcessingBehaviour.TYPE);
        if (processingBehaviour == null)
            ci.cancel();
        if (!heldItem.locked && BeltProcessingBehaviour.isBlocked(world, pos))
            ci.cancel();

        ItemStack previousItem = heldItem.stack;
        boolean wasLocked = heldItem.locked;
        BeltProcessingBehaviour.ProcessingResult result = wasLocked ? processingBehaviour.handleHeldItem(heldItem, transportedHandler)
                : processingBehaviour.handleReceivedItem(heldItem, transportedHandler);
        if (heldItem == null || result == BeltProcessingBehaviour.ProcessingResult.REMOVE) {
            heldItem = null;
            blockEntity.sendData();
            ci.cancel();
        }

        heldItem.locked = result == BeltProcessingBehaviour.ProcessingResult.HOLD;
        if (heldItem.locked != wasLocked || !ItemStack.matches(previousItem, heldItem.stack))
            blockEntity.sendData();
    }

    @Override
    public BehaviourType<?> getType() {
        return DepotBehaviour.TYPE;
    }
}
