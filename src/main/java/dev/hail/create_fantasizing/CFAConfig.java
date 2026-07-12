package dev.hail.create_fantasizing;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FantasizingMod.MOD_ID)
public class CFAConfig {
    private static final ModConfigSpec.Builder BUILDER_U = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue REGISTER_POWDER_SNOW_LIQUID_CAPABILITIES;

    private static final ModConfigSpec.Builder BUILDER_C = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue CHROMATIC_TUNNEL_SILENT_PROCESSING;

    private static final ModConfigSpec.Builder BUILDER_S = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue HYDRAULIC_ENGINE_STRESS_PROVIDE;
    private static final ModConfigSpec.DoubleValue WIND_ENGINE_STRESS_PROVIDE;
    private static final ModConfigSpec.DoubleValue SCULK_ENGINE_STRESS_PROVIDE;
    private static final ModConfigSpec.BooleanValue SCULK_ENGINE_FRAME_CATCH_WARDEN;
    private static final ModConfigSpec.DoubleValue YIN_YANG_ENGINE_STRESS_PROVIDE;

    private static final ModConfigSpec.DoubleValue BLOCK_PLACER_POWER;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_RANGE;

    private static final ModConfigSpec.IntValue BLOCK_PLACER_CUBOID_MAX_SIZE;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_SPHERE_MAX_RADIUS;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_CYLINDER_MAX_RADIUS;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_CYLINDER_MAX_HEIGHT;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_DYNAMIC_MAX_RADIUS;

    private static final ModConfigSpec.IntValue BLOCK_PLACER_COOLDOWN;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_COOLDOWN_SCALE;
    private static final ModConfigSpec.IntValue BLOCK_PLACER_BATCH_SIZE;

    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_INFINITY_ENABLED;
    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_FORTUNE_ENABLED;
    private static final ModConfigSpec.BooleanValue BLOCK_PLACER_SILK_TOUCH_ENABLED;

    static final ModConfigSpec SPEC_U;
    static final ModConfigSpec SPEC_C;
    static final ModConfigSpec SPEC_S;

    static {
        REGISTER_POWDER_SNOW_LIQUID_CAPABILITIES = BUILDER_U
                .comment("Register the Powder Snow Bucket and the Powder Snow Cauldron capabilities that have the Snow Powder liquid.\n* The Snow Powder liquid will always be registered.\n* You need to turn it off when another mod also adds the powder snow liquid.\n* Need to restart the game!")
                .translation("create_fantasizing.configuration.register_powder_snow_liquid_capabilities")
                .define("register_powder_snow_liquid_capabilities", true);

        CHROMATIC_TUNNEL_SILENT_PROCESSING = BUILDER_C
                .comment("When processing, the Refined Radiance Tunnel and Shadow Steel Tunnel do not play particles and sound.")
                .translation("create_fantasizing.configuration.chromatic_tunnel_silent_processing")
                .define("silent_chromatic_tunnel", false);

        BUILDER_S.push("engines");
        HYDRAULIC_ENGINE_STRESS_PROVIDE = BUILDER_S
                .comment("Stress units provided by the Hydraulic Engine")
                .translation("create_fantasizing.configuration.engines.hydraulic_stress_provide")
                .defineInRange("hydraulic_stress_provide", 8192, 0, Double.MAX_VALUE);
        WIND_ENGINE_STRESS_PROVIDE = BUILDER_S
                .comment("Stress units provided by the Wind Engine")
                .translation("create_fantasizing.configuration.engines.wind_stress_provide")
                .defineInRange("wind_stress_provide", 8192, 0, Double.MAX_VALUE);
        SCULK_ENGINE_STRESS_PROVIDE = BUILDER_S
                .comment("Stress units provided by the Sculk Engine")
                .translation("create_fantasizing.configuration.engines.sculk_stress_provide")
                .defineInRange("sculk_stress_provide", 8192, 0, Double.MAX_VALUE);
        SCULK_ENGINE_FRAME_CATCH_WARDEN = BUILDER_S
                .comment("Can Sculk Engine Frame catch the Warden and turn into Sculk Engine?")
                .translation("create_fantasizing.configuration.engines.sculk_engine_frame_catch_warden")
                .define("sculk_engine_frame_catch_warden", true);
        YIN_YANG_ENGINE_STRESS_PROVIDE = BUILDER_S
                .comment("Stress units provided by the Yin Yang Engine")
                .translation("create_fantasizing.configuration.engines.yin_yang_stress_provide")
                .defineInRange("yin_yang_stress_provide", 32768, 0, Double.MAX_VALUE);
        BUILDER_S.pop();

        BUILDER_S.push("block_placer");

        BLOCK_PLACER_POWER = BUILDER_S
                .comment("Maximum hardness of blocks the Block Placer can break/replace. Blocks with higher hardness are ignored.")
                .translation("create_fantasizing.configuration.block_placer.power")
                .defineInRange("power", 50, 0, Double.MAX_VALUE);
        BLOCK_PLACER_RANGE = BUILDER_S
                .comment("Maximum reach distance of the Block Placer in blocks")
                .translation("create_fantasizing.configuration.block_placer.range")
                .defineInRange("range", 48, 8, 128);

        BUILDER_S.push("brush");
        BLOCK_PLACER_CUBOID_MAX_SIZE = BUILDER_S
                .comment("Maximum size per axis for the Cuboid brush (each axis is independent)")
                .translation("create_fantasizing.configuration.block_placer.brush.cuboid_max_size")
                .defineInRange("cuboid_max_size", 32, 1, 64);
        BLOCK_PLACER_SPHERE_MAX_RADIUS = BUILDER_S
                .comment("Maximum radius for the Sphere brush")
                .translation("create_fantasizing.configuration.block_placer.brush.sphere_max_radius")
                .defineInRange("sphere_max_radius", 10, 1, 20);
        BLOCK_PLACER_CYLINDER_MAX_RADIUS = BUILDER_S
                .comment("Maximum radius for the Cylinder brush")
                .translation("create_fantasizing.configuration.block_placer.brush.cylinder_max_radius")
                .defineInRange("cylinder_max_radius", 8, 1, 12);
        BLOCK_PLACER_CYLINDER_MAX_HEIGHT = BUILDER_S
                .comment("Maximum height for the Cylinder brush")
                .translation("create_fantasizing.configuration.block_placer.brush.cylinder_max_height")
                .defineInRange("cylinder_max_height", 8, 1, 12);
        BLOCK_PLACER_DYNAMIC_MAX_RADIUS = BUILDER_S
                .comment("Maximum radius for the Surface and Cluster brushes")
                .translation("create_fantasizing.configuration.block_placer.brush.dynamic_max_radius")
                .defineInRange("dynamic_max_radius", 10, 1, 64);
        BUILDER_S.pop();

        BUILDER_S.push("cooldown");
        BLOCK_PLACER_COOLDOWN = BUILDER_S
                .comment("Base cooldown in ticks added to every activation.",
                         "Final cooldown = base + affected_blocks / scale")
                .translation("create_fantasizing.configuration.block_placer.cooldown.base")
                .defineInRange("base", 2, 1, 100);
        BLOCK_PLACER_COOLDOWN_SCALE = BUILDER_S
                .comment("Divisor for the block-count part of the cooldown formula: affected_blocks / scale.",
                         "Lower values = longer cooldowns for large operations.")
                .translation("create_fantasizing.configuration.block_placer.cooldown.scale")
                .defineInRange("scale", 20, 1, 10000);
        BLOCK_PLACER_BATCH_SIZE = BUILDER_S
                .comment("Number of blocks the Block Placer places per server tick.",
                         "Lower values = smoother server, slower visual completion. Higher = faster but more load per tick.")
                .translation("create_fantasizing.configuration.block_placer.cooldown.batch_size")
                .defineInRange("batch_size", 256, 1, 4096);
        BUILDER_S.pop();

        BUILDER_S.push("enchantments");
        BLOCK_PLACER_INFINITY_ENABLED = BUILDER_S
                .comment("When true, Infinity enchantment allows placing renewable blocks (e.g. Cobblestone) without consuming them")
                .translation("create_fantasizing.configuration.block_placer.enchantments.infinity")
                .define("infinity", true);
        BLOCK_PLACER_FORTUNE_ENABLED = BUILDER_S
                .comment("When true, Fortune enchantment affects block drops when breaking blocks")
                .translation("create_fantasizing.configuration.block_placer.enchantments.fortune")
                .define("fortune", true);
        BLOCK_PLACER_SILK_TOUCH_ENABLED = BUILDER_S
                .comment("When true, Silk Touch enchantment affects block drops when breaking blocks")
                .translation("create_fantasizing.configuration.block_placer.enchantments.silk_touch")
                .define("silk_touch", true);
        BUILDER_S.pop();

        BUILDER_S.pop(); // block_placer

        SPEC_U = BUILDER_U.build();
        SPEC_C = BUILDER_C.build();
        SPEC_S = BUILDER_S.build();
    }

    public static boolean registerPowderSnowLiquid = true;

    public static boolean chromaticTunnelSilentProcessing = false;

    public static double hydraulicEngineStressProvide;
    public static double windEngineStressProvide;
    public static double sculkEngineStressProvide;
    public static boolean sculkEngineCatchWarden = true;
    public static double yinYangStressProvide;

    public static double blockPlacerPower;
    public static int blockPlacerRange = 48;

    public static int blockPlacerCuboidMaxSize = 32;
    public static int blockPlacerSphereMaxRadius = 10;
    public static int blockPlacerCylinderMaxRadius = 8;
    public static int blockPlacerCylinderMaxHeight = 8;
    public static int blockPlacerDynamicMaxRadius = 10;

    public static int blockPlacerCooldown = 1;
    public static int blockPlacerCooldownScale = 256;
    public static int blockPlacerBatchSize = 256;

    public static boolean blockPlacerInfinityEnabled = true;
    public static boolean blockPlacerFortuneEnabled = true;
    public static boolean blockPlacerSilkTouchEnabled = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        appConfig(event);
    }

    @SubscribeEvent
    static void onFileChange(final ModConfigEvent.Reloading event) {
        appConfig(event);
    }

    protected static void appConfig(ModConfigEvent event){
        if (event.getConfig().getSpec() == SPEC_U) {
            registerPowderSnowLiquid = REGISTER_POWDER_SNOW_LIQUID_CAPABILITIES.getAsBoolean();
        }
        if (event.getConfig().getSpec() == SPEC_C) {
            chromaticTunnelSilentProcessing = CHROMATIC_TUNNEL_SILENT_PROCESSING.getAsBoolean();
        }
        else if (event.getConfig().getSpec() == SPEC_S) {
            hydraulicEngineStressProvide = HYDRAULIC_ENGINE_STRESS_PROVIDE.getAsDouble();
            windEngineStressProvide = WIND_ENGINE_STRESS_PROVIDE.getAsDouble();
            sculkEngineStressProvide = SCULK_ENGINE_STRESS_PROVIDE.getAsDouble();
            sculkEngineCatchWarden = SCULK_ENGINE_FRAME_CATCH_WARDEN.getAsBoolean();
            yinYangStressProvide = YIN_YANG_ENGINE_STRESS_PROVIDE.getAsDouble();

            blockPlacerPower = BLOCK_PLACER_POWER.getAsDouble();
            blockPlacerRange = BLOCK_PLACER_RANGE.getAsInt();

            blockPlacerCuboidMaxSize = BLOCK_PLACER_CUBOID_MAX_SIZE.getAsInt();
            blockPlacerSphereMaxRadius = BLOCK_PLACER_SPHERE_MAX_RADIUS.getAsInt();
            blockPlacerCylinderMaxRadius = BLOCK_PLACER_CYLINDER_MAX_RADIUS.getAsInt();
            blockPlacerCylinderMaxHeight = BLOCK_PLACER_CYLINDER_MAX_HEIGHT.getAsInt();
            blockPlacerDynamicMaxRadius = BLOCK_PLACER_DYNAMIC_MAX_RADIUS.getAsInt();

            blockPlacerCooldown = BLOCK_PLACER_COOLDOWN.getAsInt();
            blockPlacerCooldownScale = BLOCK_PLACER_COOLDOWN_SCALE.getAsInt();
            blockPlacerBatchSize = BLOCK_PLACER_BATCH_SIZE.getAsInt();

            blockPlacerInfinityEnabled = BLOCK_PLACER_INFINITY_ENABLED.getAsBoolean();
            blockPlacerFortuneEnabled = BLOCK_PLACER_FORTUNE_ENABLED.getAsBoolean();
            blockPlacerSilkTouchEnabled = BLOCK_PLACER_SILK_TOUCH_ENABLED.getAsBoolean();
        }
    }
}
