package dev.hail.create_fantasizing;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FantasizingMod.MOD_ID)
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

    // Block Placer brush sizes
    private static final ModConfigSpec.IntValue BLOCK_PLACER_CUBOID_MAX_SIZE = BUILDER_S
            .defineInRange("block_placer_cuboid_max_size", 32, 1, 64);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_SPHERE_MAX_RADIUS = BUILDER_S
            .defineInRange("block_placer_sphere_max_radius", 10, 1, 20);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_CYLINDER_MAX_RADIUS = BUILDER_S
            .defineInRange("block_placer_cylinder_max_radius", 8, 1, 12);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_CYLINDER_MAX_HEIGHT = BUILDER_S
            .defineInRange("block_placer_cylinder_max_height", 8, 1, 12);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_DYNAMIC_MAX_RADIUS = BUILDER_S
            .defineInRange("block_placer_dynamic_max_radius", 10, 1, 64);

    private static final ModConfigSpec.IntValue BLOCK_PLACER_RANGE = BUILDER_S
            .defineInRange("block_placer_range", 48, 8, 128);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_COOLDOWN = BUILDER_S
            .defineInRange("block_placer_cooldown", 2, 1, 100);
    private static final ModConfigSpec.IntValue BLOCK_PLACER_COOLDOWN_SCALE = BUILDER_S
            .defineInRange("block_placer_cooldown_scale", 100, 1, 10000);

    // Block Placer enchantment effects
    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_INFINITY_ENABLED = BUILDER_S
            .define("block_placer_infinity_enabled", true);
    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_FORTUNE_ENABLED = BUILDER_S
            .define("block_placer_fortune_enabled", true);
    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_SILK_TOUCH_ENABLED = BUILDER_S
            .define("block_placer_silk_touch_enabled", true);

    static final ModConfigSpec SPEC_C = BUILDER_C.build();
    static final ModConfigSpec SPEC_S = BUILDER_S.build();

    //public static boolean foldInterface;

    public static double hydraulicEngineStressProvide;
    public static double windEngineStressProvide;
    public static double blockPlacerPower;

    public static int blockPlacerRange = 48;
    public static int blockPlacerCooldown = 2;
    public static int blockPlacerCooldownScale = 100;

    public static int blockPlacerCuboidMaxSize = 32;
    public static int blockPlacerSphereMaxRadius = 10;
    public static int blockPlacerCylinderMaxRadius = 8;
    public static int blockPlacerCylinderMaxHeight = 8;
    public static int blockPlacerDynamicMaxRadius = 10;

    public static boolean blockPlacerInfinityEnabled = true;
    public static boolean blockPlacerFortuneEnabled = true;
    public static boolean blockPlacerSilkTouchEnabled = true;

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
            blockPlacerRange = BLOCK_PLACER_RANGE.get();
            blockPlacerCooldown = BLOCK_PLACER_COOLDOWN.get();
            blockPlacerCooldownScale = BLOCK_PLACER_COOLDOWN_SCALE.get();
            blockPlacerCuboidMaxSize = BLOCK_PLACER_CUBOID_MAX_SIZE.get();
            blockPlacerSphereMaxRadius = BLOCK_PLACER_SPHERE_MAX_RADIUS.get();
            blockPlacerCylinderMaxRadius = BLOCK_PLACER_CYLINDER_MAX_RADIUS.get();
            blockPlacerCylinderMaxHeight = BLOCK_PLACER_CYLINDER_MAX_HEIGHT.get();
            blockPlacerDynamicMaxRadius = BLOCK_PLACER_DYNAMIC_MAX_RADIUS.get();
            blockPlacerInfinityEnabled = BLOCK_PLACER_INFINITY_ENABLED.get();
            blockPlacerFortuneEnabled = BLOCK_PLACER_FORTUNE_ENABLED.get();
            blockPlacerSilkTouchEnabled = BLOCK_PLACER_SILK_TOUCH_ENABLED.get();
        }
    }
}
