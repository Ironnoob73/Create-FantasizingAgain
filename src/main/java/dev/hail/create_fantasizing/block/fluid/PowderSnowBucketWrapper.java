package dev.hail.create_fantasizing.block.fluid;

import net.minecraft.world.item.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;

public class PowderSnowBucketWrapper extends FluidBucketWrapper {
    public PowderSnowBucketWrapper(ItemStack container) {
        super(container);
    }
    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid.getFluid() != CFAFluids.POWDER_SNOW.get()) {
            return !fluid.getFluid().getFluidType().getBucket(fluid).isEmpty();
        } else {
            return true;
        }
    }

    @Override
    public @NotNull FluidStack getFluid() {
        Item item = this.container.getItem();
        if (item == Items.POWDER_SNOW_BUCKET)
            return new  FluidStack(CFAFluids.POWDER_SNOW.get(), 1000);
        return FluidStack.EMPTY;
    }
}
