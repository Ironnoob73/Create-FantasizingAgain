package dev.hail.create_fantasizing.block;

import com.simibubi.create.AllDisplaySources;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.content.kinetics.simpleRelays.*;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelRenderer;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelVisual;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.infrastructure.config.CStress;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.chromatic_tunnel.RefinedRadianceTunnelBlock;
import dev.hail.create_fantasizing.block.chromatic_tunnel.RefinedRadianceTunnelBlockEntity;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ShadowSteelTunnelBlock;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ShadowSteelTunnelBlockEntity;
import dev.hail.create_fantasizing.block.compat_engine.*;
import dev.hail.create_fantasizing.block.crate.*;
import dev.hail.create_fantasizing.block.crate.fluid_barrel.*;
import dev.hail.create_fantasizing.block.phantom_shaft.PhantomCogwheel;
import dev.hail.create_fantasizing.block.phantom_shaft.PhantomShaft;
import dev.hail.create_fantasizing.block.phantom_shaft.PhantomShaftVisual;
import dev.hail.create_fantasizing.block.transporter.TransporterBlock;
import dev.hail.create_fantasizing.block.transporter.TransporterEntity;
import dev.hail.create_fantasizing.block.transporter.TransporterRenderer;
import dev.hail.create_fantasizing.block.sturdy_girder.ConnectedSturdyGirderModel;
import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderBlock;
import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderEncasedShaftBlock;
import dev.hail.create_fantasizing.item.ChromaticTunnelItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static dev.hail.create_fantasizing.CFAConfig.*;
import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFABlocks {
    public static final BlockEntry<CompactHydraulicEngineBlock> COMPACT_HYDRAULIC_ENGINE =
            REGISTRATE.block("compact_hydraulic_engine", CompactHydraulicEngineBlock::new)
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> hydraulicEngineStressProvide))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .simpleItem()
                    .register();
    public static final BlockEntityEntry<CompactEngineEntity> COMPACT_HYDRAULIC_ENGINE_ENTITY = REGISTRATE
            .blockEntity("compact_hydraulic_engine", CompactEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.COMPACT_HYDRAULIC_ENGINE_HEART), true)
            .validBlock(COMPACT_HYDRAULIC_ENGINE)
            .renderer(() -> CompactHydraulicEngineRenderer::new)
            .register();
    public static final BlockEntry<CompactWindEngineBlock> COMPACT_WIND_ENGINE =
            REGISTRATE.block("compact_wind_engine", CompactWindEngineBlock::new)
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> windEngineStressProvide))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .simpleItem()
                    .register();
    public static final BlockEntityEntry<CompactEngineEntity> COMPACT_WIND_ENGINE_ENTITY = REGISTRATE
            .blockEntity("compact_wind_engine", CompactEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.COMPACT_WIND_ENGINE_CORE), true)
            .validBlock(COMPACT_WIND_ENGINE)
            .renderer(() -> CompactWindEngineRenderer::new)
            .register();
    public static final BlockEntry<SculkEngineBlock> SCULK_ENGINE =
            REGISTRATE.block("sculk_engine", SculkEngineBlock::new)
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> sculkEngineStressProvide))
                    .initialProperties(() -> Blocks.SCULK_CATALYST)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .simpleItem()
                    .register();
    public static final BlockEntityEntry<CompactEngineEntity> SCULK_ENGINE_ENTITY = REGISTRATE
            .blockEntity("sculk_hydraulic_engine", CompactEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.SCULK_ENGINE_CORE), true)
            .validBlock(SCULK_ENGINE)
            .renderer(() -> SculkEngineRenderer::new)
            .register();


    public static final BlockEntry<SturdyGirderBlock> STURDY_GIRDER = REGISTRATE.block("sturdy_girder", SturdyGirderBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
            .onRegister(CreateRegistrate.blockModel(() -> ConnectedSturdyGirderModel::new))
            .simpleItem()
            .register();
    public static final BlockEntry<SturdyGirderEncasedShaftBlock> STURDY_GIRDER_ENCASED_SHAFT =
            REGISTRATE.block("sturdy_girder_encased_shaft", SturdyGirderEncasedShaftBlock::new)
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
                    .onRegister(CreateRegistrate.blockModel(() -> ConnectedSturdyGirderModel::new))
                    .register();
    public static final BlockEntityEntry<KineticBlockEntity> STURDY_GIRDER_ENCASED_SHAFT_ENTITY = REGISTRATE
            .blockEntity("encased_shaft", KineticBlockEntity::new)
            .visual(() -> SingleAxisRotatingVisual::shaft, true)
            .validBlocks(STURDY_GIRDER_ENCASED_SHAFT)
            .renderer(() -> ShaftRenderer::new)
            .register();

    public static final BlockEntry<TransporterBlock> TRANSPORTER = REGISTRATE.block("transporter", TransporterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .isRedstoneConductor((level, pos, state) -> false))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<TransporterEntity> TRANSPORTER_ENTITY = REGISTRATE
            .blockEntity("transporter", TransporterEntity::new)
            .validBlocks(TRANSPORTER)
            .renderer(() -> TransporterRenderer::new)
            .register();

    public static final BlockEntry<AndesiteCrateBlock> ANDESITE_CRATE = REGISTRATE.block("andesite_crate", AndesiteCrateBlock::new)
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.mapColor(MapColor.PODZOL).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<AndesiteCrateEntity> ANDESITE_CRATE_ENTITY = REGISTRATE
            .blockEntity("andesite_crate", AndesiteCrateEntity::new)
            .validBlocks(ANDESITE_CRATE)
            .register();
    public static final BlockEntry<IronCrateBlock> IRON_CRATE = REGISTRATE.block("iron_crate", IronCrateBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<IronCrateEntity> IRON_CRATE_ENTITY = REGISTRATE
            .blockEntity("iron_crate", IronCrateEntity::new)
            .validBlocks(IRON_CRATE)
            .register();
    public static final BlockEntry<BrassCrateBlock> BRASS_CRATE = REGISTRATE.block("brass_crate", BrassCrateBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BROWN).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<BrassCrateEntity> BRASS_CRATE_ENTITY = REGISTRATE
            .blockEntity("brass_crate", BrassCrateEntity::new)
            .validBlocks(BRASS_CRATE)
            .register();
    public static final BlockEntry<SturdyCrateBlock> STURDY_CRATE = REGISTRATE.block("sturdy_crate", SturdyCrateBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<SturdyCrateEntity> STURDY_CRATE_ENTITY = REGISTRATE
            .blockEntity("sturdy_crate", SturdyCrateEntity::new)
            .validBlocks(STURDY_CRATE)
            .register();

    public static final BlockEntry<CopperFluidBarrelBlock> COPPER_FLUID_BARREL = REGISTRATE.block("copper_fluid_barrel", CopperFluidBarrelBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_ORANGE).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<CopperFluidBarrelEntity> COPPER_FLUID_BARREL_ENTITY = REGISTRATE
            .blockEntity("copper_fluid_barrel", CopperFluidBarrelEntity::new)
            .validBlocks(COPPER_FLUID_BARREL)
            .register();
    public static final BlockEntry<ZincFluidBarrelBlock> ZINC_FLUID_BARREL = REGISTRATE.block("zinc_fluid_barrel", ZincFluidBarrelBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<ZincFluidBarrelEntity> ZINC_FLUID_BARREL_ENTITY = REGISTRATE
            .blockEntity("zinc_fluid_barrel", ZincFluidBarrelEntity::new)
            .validBlocks(ZINC_FLUID_BARREL)
            .register();
    public static final BlockEntry<GoldFluidBarrelBlock> GOLD_FLUID_BARREL = REGISTRATE.block("gold_fluid_barrel", GoldFluidBarrelBlock::new)
            .initialProperties(() -> Blocks.GOLD_BLOCK)
            .properties(p -> p.mapColor(MapColor.GOLD).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<GoldFluidBarrelEntity> GOLD_FLUID_BARREL_ENTITY = REGISTRATE
            .blockEntity("gold_fluid_barrel", GoldFluidBarrelEntity::new)
            .validBlocks(GOLD_FLUID_BARREL)
            .register();
    public static final BlockEntry<DiamondFluidBarrelBlock> DIAMOND_FLUID_BARREL = REGISTRATE.block("diamond_fluid_barrel", DiamondFluidBarrelBlock::new)
            .initialProperties(() -> Blocks.DIAMOND_BLOCK)
            .properties(p -> p.mapColor(MapColor.DIAMOND).explosionResistance(1200))
            .simpleItem()
            .register();
    public static final BlockEntityEntry<DiamondFluidBarrelEntity> DIAMOND_FLUID_BARREL_ENTITY = REGISTRATE
            .blockEntity("diamond_fluid_barrel", DiamondFluidBarrelEntity::new)
            .validBlocks(DIAMOND_FLUID_BARREL)
            .register();

    public static final BlockEntry<CasingBlock> ZINC_CASING = REGISTRATE.block("zinc_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN))
            .transform(BuilderTransformers.casing(() -> CFASpriteShifts.ZINC_CASING))
            .register();
    public static final BlockEntry<CasingBlock> GOLD_CASING = REGISTRATE.block("gold_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.GOLD))
            .transform(BuilderTransformers.casing(() -> CFASpriteShifts.GOLD_CASING))
            .register();
    public static final BlockEntry<CasingBlock> DIAMOND_CASING = REGISTRATE.block("diamond_casing", CasingBlock::new)
            .properties(p -> p.mapColor(MapColor.DIAMOND))
            .transform(BuilderTransformers.casing(() -> CFASpriteShifts.DIAMOND_CASING))
            .register();

    public static final BlockEntry<PhantomShaft> PHANTOM_SHAFT = REGISTRATE.block("phantom_shaft", PhantomShaft::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.QUARTZ).noCollission())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .simpleItem()
            .register();
    public static final BlockEntry<PhantomCogwheel> PHANTOM_COGWHEEL = REGISTRATE.block("phantom_cogwheel", PhantomCogwheel::small)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.QUARTZ).noCollission())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(CogwheelBlockItem::new)
            .build()
            .register();
    public static final BlockEntry<PhantomCogwheel> PHANTOM_LARGE_COGWHEEL = REGISTRATE.block("phantom_large_cogwheel", PhantomCogwheel::large)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.QUARTZ).noCollission())
            .blockstate(BlockStateGen.axisBlockProvider(false))
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(CogwheelBlockItem::new)
            .build()
            .register();
    public static final BlockEntityEntry<BracketedKineticBlockEntity> PHANTOM_SHAFT_ENTITY = REGISTRATE
            .blockEntity("phantom_shaft", BracketedKineticBlockEntity::new)
            .visual(() -> PhantomShaftVisual::create, false)
            .validBlocks(CFABlocks.PHANTOM_SHAFT, CFABlocks.PHANTOM_COGWHEEL, CFABlocks.PHANTOM_LARGE_COGWHEEL)
            .renderer(() -> BracketedKineticBlockEntityRenderer::new)
            .register();

    public static final BlockEntry<RoseQuartzBulb> ROSE_QUARTZ_BULB = REGISTRATE.block("rose_quartz_bulb", RoseQuartzBulb::new)
            .properties(p -> p.lightLevel($ -> 15).mapColor(DyeColor.ORANGE).strength(0.0F, 6.0F).sound(SoundType.METAL).forceSolidOn().noCollission())
            .simpleItem()
            .register();

    public static final BlockEntry<ShadowSteelTunnelBlock> SHADOW_STEEL_TUNNEL = REGISTRATE.block("shadow_steel_tunnel", ShadowSteelTunnelBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).noOcclusion())
            .transform(displaySource(AllDisplaySources.ACCUMULATE_ITEMS))
            .transform(displaySource(AllDisplaySources.ITEM_THROUGHPUT))
            .item(ChromaticTunnelItem::new)
            .build()
            .register();
    public static final BlockEntityEntry<ShadowSteelTunnelBlockEntity> SHADOW_STEEL_TUNNEL_ENTITY = REGISTRATE
            .blockEntity("shadow_steel_tunnel", ShadowSteelTunnelBlockEntity::new)
            .visual(() -> BeltTunnelVisual::new)
            .validBlocks(SHADOW_STEEL_TUNNEL)
            .renderer(() -> BeltTunnelRenderer::new)
            .register();
    public static final BlockEntry<RefinedRadianceTunnelBlock> REFINED_RADIANCE_TUNNEL = REGISTRATE.block("refined_radiance_tunnel", RefinedRadianceTunnelBlock::new)
            .properties(p -> p.mapColor(MapColor.SNOW).noOcclusion().lightLevel($ -> 12))
            .transform(displaySource(AllDisplaySources.ACCUMULATE_ITEMS))
            .transform(displaySource(AllDisplaySources.ITEM_THROUGHPUT))
            .item(ChromaticTunnelItem::new)
            .build()
            .register();
    public static final BlockEntityEntry<RefinedRadianceTunnelBlockEntity> REFINED_RADIANCE_TUNNEL_ENTITY = REGISTRATE
            .blockEntity("refined_radiance_tunnel", RefinedRadianceTunnelBlockEntity::new)
            .visual(() -> BeltTunnelVisual::new)
            .validBlocks(REFINED_RADIANCE_TUNNEL)
            .renderer(() -> BeltTunnelRenderer::new)
            .register();

    public static final BlockEntry<YinYangEngineBlock> YIN_YANG_ENGINE_BLOCK =
            REGISTRATE.block("yin_yang_engine", YinYangEngineBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY).noOcclusion().lightLevel($ -> 12))
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> yinYangStressProvide))
                    .item()
                    .properties(p -> p.rarity(Rarity.EPIC))
                    .build()
                    .register();
    public static final BlockEntityEntry<YinYangEngineEntity> YIN_YANG_ENGINE_ENTITY = REGISTRATE
            .blockEntity("yin_yang_engine", YinYangEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.YIN_YANG_ENGINE_AXIS), false)
            .validBlocks(YIN_YANG_ENGINE_BLOCK)
            .renderer(() -> YinYangEngineRenderer::new)
            .register();

    public static void init() {}
}
