package dev.hail.create_fantasizing.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.sturdy_girder.CFABezierGirder;
import dev.hail.create_fantasizing.block.sturdy_girder.CFAGirderTypeTracker;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * By Deepseek V4.1 Flash 1017:
 * Stores which girder item a curved track connection was built with, so that
 * Sturdy Girders can act as track supports just like Metal Girders do.
 */
@Mixin(BezierConnection.class)
public abstract class BezierConnectionMixin implements CFABezierGirder {

    @Unique
    private boolean cfa$sturdyGirder;

    @Override
    public boolean isSturdyGirder() {
        return cfa$sturdyGirder;
    }

    @Override
    public void setSturdyGirder(boolean sturdy) {
        cfa$sturdyGirder = sturdy;
    }

    @Inject(
            method = "<init>(Lnet/createmod/catnip/data/Couple;Lnet/createmod/catnip/data/Couple;Lnet/createmod/catnip/data/Couple;Lnet/createmod/catnip/data/Couple;ZZLcom/simibubi/create/content/trains/track/TrackMaterial;)V",
            at = @At("TAIL")
    )
    private void cfa$initFromPlacement(Couple<BlockPos> positions, Couple<Vec3> starts, Couple<Vec3> axes,
                                       Couple<Vec3> normals, boolean primary, boolean girder,
                                       TrackMaterial material, CallbackInfo ci) {
        if (girder && CFAGirderTypeTracker.isSturdy())
            cfa$sturdyGirder = true;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    private void cfa$readFromTag(CompoundTag tag, BlockPos localTo, CallbackInfo ci) {
        cfa$sturdyGirder = tag.getBoolean("SturdyGirder");
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void cfa$readFromBuffer(FriendlyByteBuf buffer, CallbackInfo ci) {
        cfa$sturdyGirder = buffer.readBoolean();
    }

    @Inject(method = "secondary", at = @At("RETURN"))
    private void cfa$copyToSecondary(CallbackInfoReturnable<BezierConnection> cir) {
        if (cir.getReturnValue() instanceof CFABezierGirder other)
            other.setSturdyGirder(cfa$sturdyGirder);
    }

    @Inject(
            method = "clone()Lcom/simibubi/create/content/trains/track/BezierConnection;",
            at = @At("RETURN")
    )
    private void cfa$copyToClone(CallbackInfoReturnable<BezierConnection> cir) {
        if (cir.getReturnValue() instanceof CFABezierGirder other)
            other.setSturdyGirder(cfa$sturdyGirder);
    }

    @Inject(
            method = "write(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("RETURN")
    )
    private void cfa$writeToTag(BlockPos localTo, CallbackInfoReturnable<CompoundTag> cir) {
        if (cfa$sturdyGirder)
            cir.getReturnValue().putBoolean("SturdyGirder", true);
    }

    @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void cfa$writeToBuffer(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(cfa$sturdyGirder);
    }

    @Redirect(
            method = "addItemsToPlayer",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack(I)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack cfa$refundGirders(BlockEntry<?> entry, int count) {
        return (cfa$sturdyGirder ? CFABlocks.STURDY_GIRDER : AllBlocks.METAL_GIRDER).asStack(count);
    }

    @Redirect(
            method = "spawnItems",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;asStack()Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack cfa$dropGirder(BlockEntry<?> entry) {
        return (cfa$sturdyGirder ? CFABlocks.STURDY_GIRDER : AllBlocks.METAL_GIRDER).asStack();
    }

    @Redirect(
            method = "spawnDestroyParticles",
            at = @At(value = "INVOKE",
                    target = "Lcom/tterrag/registrate/util/entry/BlockEntry;getDefaultState()Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState cfa$girderParticleState(BlockEntry<?> entry) {
        return cfa$sturdyGirder ? CFABlocks.STURDY_GIRDER.getDefaultState() : entry.getDefaultState();
    }
}
