package dev.hail.create_fantasizing.block.fluid;

import com.simibubi.create.AllFluids;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFAFluids {

    public static final FluidEntry<BaseFlowingFluid.Flowing> POWDER_SNOW =
            REGISTRATE.fluid("powder_snow",
                            ResourceLocation.withDefaultNamespace("block/powder_snow"),
                            ResourceLocation.withDefaultNamespace("block/powder_snow"),
                            SolidRenderedPlaceableFluidType.create(0xFFFFF,
                                    () -> 1f / 8f * AllConfigs.client().honeyTransparencyMultiplier.getF()))
                    .properties(b -> b.viscosity(2000)
                            .density(1400))
                    .fluidProperties(p -> p.levelDecreasePerBlock(7)
                            .tickRate(1)
                            .slopeFindDistance(0)
                            .explosionResistance(100f))
                    .source(PowderSnowFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.SNOW))
                    .build()
                    .noBucket()
                    .register();

    public static class SolidRenderedPlaceableFluidType extends AllFluids.TintedFluidType {

        private Vector3f fogColor;
        private Supplier<Float> fogDistance;

        public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                SolidRenderedPlaceableFluidType fluidType = new SolidRenderedPlaceableFluidType(p, s, f);
                fluidType.fogColor = new Color(fogColor, false).asVectorF();
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        private SolidRenderedPlaceableFluidType(Properties properties, ResourceLocation stillTexture,
                                                ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 0x00ffffff;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return fogColor;
        }

        @Override
        protected float getFogDistanceModifier() {
            return fogDistance.get();
        }

    }

    public static void init() {}
}
