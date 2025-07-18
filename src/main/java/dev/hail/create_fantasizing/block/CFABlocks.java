package dev.hail.create_fantasizing.block;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.compat_engine.*;
import dev.hail.create_fantasizing.block.transporter.TransporterBlock;
import dev.hail.create_fantasizing.block.transporter.TransporterEntity;
import dev.hail.create_fantasizing.block.transporter.TransporterRenderer;
import dev.hail.create_fantasizing.block.sturdy_girder.ConnectedSturdyGirderModel;
import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderBlock;
import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderEncasedShaftBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static dev.hail.create_fantasizing.CFAConfig.*;
import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFABlocks {
    public static final BlockEntry<CompactHydraulicEngineBlock> COMPACT_HYDRAULIC_ENGINE =
            REGISTRATE.block("compact_hydraulic_engine", CompactHydraulicEngineBlock::new)
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> (double) hydraulicEngineStressProvide /16))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .blockstate(new CompactEngineBlock.CompactHydraulicEngineGenerator()::generate)
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
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()-> (double) windEngineStressProvide /16))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .blockstate(new CompactEngineBlock.CompactHydraulicEngineGenerator()::generate)
                    .simpleItem()
                    .register();
    public static final BlockEntityEntry<CompactEngineEntity> COMPACT_WIND_ENGINE_ENTITY = REGISTRATE
            .blockEntity("compact_wind_engine", CompactEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.COMPACT_WIND_ENGINE_CORE), true)
            .validBlock(COMPACT_WIND_ENGINE)
            .renderer(() -> CompactWindEngineRenderer::new)
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

    public static void init() {}
}
