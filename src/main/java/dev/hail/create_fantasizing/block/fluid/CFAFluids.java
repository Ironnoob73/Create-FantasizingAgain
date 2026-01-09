package dev.hail.create_fantasizing.block.fluid;

import dev.hail.create_fantasizing.FantasizingMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.*;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class CFAFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, FantasizingMod.MOD_ID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, FantasizingMod.MOD_ID);

    public static final Supplier<Fluid> POWDER_SNOW = FLUIDS.register("powder_snow", PowderSnowFluid.Source::new);
    public static final Supplier<Fluid> FLOWING_POWDER_SNOW = FLUIDS.register("flowing_powder_snow", PowderSnowFluid.Flowing::new);
    public static final Holder<FluidType> POWDER_SNOW_TYPE = FLUID_TYPES.register("powder_snow",
            () -> new PowderSnowFluid.PowderSnowFluidType(
                    FluidType.Properties.create().descriptionId("block.minecraft.powder_snow"),
                    ResourceLocation.withDefaultNamespace("block/powder_snow"),
                    ResourceLocation.withDefaultNamespace("block/powder_snow")));
}
