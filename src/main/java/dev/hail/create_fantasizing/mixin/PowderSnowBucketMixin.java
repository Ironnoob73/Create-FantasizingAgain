package dev.hail.create_fantasizing.mixin;

import dev.hail.create_fantasizing.block.fluid.PowderSnowBucketWrapper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SolidBucketItem.class)
public class PowderSnowBucketMixin implements IForgeItem {
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.nbt.CompoundTag nbt) {
        return new PowderSnowBucketWrapper(stack);
    }
}
