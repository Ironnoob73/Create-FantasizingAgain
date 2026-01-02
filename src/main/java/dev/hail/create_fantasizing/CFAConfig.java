package dev.hail.create_fantasizing;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CFAConfig {
    private static final ForgeConfigSpec.Builder BUILDER_S = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue HYDRAULIC_ENGINE_STRESS_PROVIDE = BUILDER_S
            .defineInRange("hydraulic_engine_stress_provide", 8192, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue WIND_ENGINE_STRESS_PROVIDE = BUILDER_S
            .defineInRange("wind_engine_stress_provide", 8192, 0, Double.MAX_VALUE);
    private static final ForgeConfigSpec.DoubleValue BLOCK_PLACER_POWER = BUILDER_S
            .defineInRange("block_placer_power", 50, 0, Double.MAX_VALUE);
    static final ForgeConfigSpec SPEC_S = BUILDER_S.build();

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
