package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ChromaticTunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeltBlockEntity.class)
public class BeltBlockEntityMixin extends KineticBlockEntity {
    @Shadow
    public int index;

    @Shadow
    public Direction getMovementFacing() {
        return null;
    }

    public BeltBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "tryInsertingFromSide(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void injected(TransportedItemStack transportedStack, Direction side, boolean simulate, CallbackInfoReturnable<ItemStack> cir) {
        if (level != null) {
            BlockEntity tunnelEntity = level.getBlockEntity(worldPosition.above());
            if (tunnelEntity instanceof ChromaticTunnelBlockEntity chromaticTunnel) {
                BlockPos offset = worldPosition.relative(getMovementFacing());
                DirectBeltInputBehaviour sideOutput = BlockEntityBehaviour.get(level, offset, DirectBeltInputBehaviour.TYPE);
                if (sideOutput != null && sideOutput.canInsertFromSide(getMovementFacing())){
                    chromaticTunnel.setStackToProcess(transportedStack, true);
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}
