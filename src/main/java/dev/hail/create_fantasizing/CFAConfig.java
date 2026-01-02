package dev.hail.create_fantasizing;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FantasizingMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CFAConfig {
    private static final ModConfigSpec.Builder BUILDER_C = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder BUILDER_S = new ModConfigSpec.Builder();

    //private static final ModConfigSpec.BooleanValue FOLD_INTERFACE = BUILDER_C
    //        .define("fold_interface", true);

    private static final ModConfigSpec.DoubleValue HYDRAULIC_ENGINE_STRESS_PROVIDE = BUILDER_S
            .defineInRange("hydraulic_engine_stress_provide", 8192, 0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue WIND_ENGINE_STRESS_PROVIDE = BUILDER_S
            .defineInRange("wind_engine_stress_provide", 8192, 0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue BLOCK_PLACER_POWER = BUILDER_S
            .defineInRange("block_placer_power", 50, 0, Double.MAX_VALUE);
    static final ModConfigSpec SPEC_C = BUILDER_C.build();
    static final ModConfigSpec SPEC_S = BUILDER_S.build();

    //public static boolean foldInterface;

    public static double hydraulicEngineStressProvide;
    public static double windEngineStressProvide;
    public static double blockPlacerPower;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event)
    {
        /*if (event.getConfig().getSpec() == SPEC_C) {
            foldInterface = FOLD_INTERFACE.get();
        }*/
        if (event.getConfig().getSpec() == SPEC_S) {
            hydraulicEngineStressProvide = HYDRAULIC_ENGINE_STRESS_PROVIDE.get();
            windEngineStressProvide = WIND_ENGINE_STRESS_PROVIDE.get();
            blockPlacerPower = BLOCK_PLACER_POWER.get();
        }
    }
}
