package dev.hail.create_fantasizing.block;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.block.compat_engine.water.CompactHydraulicEngineBlock;
import dev.hail.create_fantasizing.block.compat_engine.water.CompactHydraulicEngineEntity;
import dev.hail.create_fantasizing.block.compat_engine.water.CompactHydraulicEngineRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredItem;

import static dev.hail.create_fantasizing.FantasizingMod.MOD_ID;
import static dev.hail.create_fantasizing.item.CFAItems.ITEMS;

public class CFABlocks {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static final BlockEntry<CompactHydraulicEngineBlock> COMPACT_HYDRAULIC_ENGINE =
            REGISTRATE.block("compact_hydraulic_engine", CompactHydraulicEngineBlock::new)
                    .onRegister((block) -> BlockStressValues.CAPACITIES.register(block, ()->8192/4))
                    .initialProperties(SharedProperties::stone)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLUE).forceSolidOn())
                    .blockstate(new CompactHydraulicEngineBlock.CompactHydraulicEngineGenerator()::generate)
                    .register();
    public static final BlockEntityEntry<CompactHydraulicEngineEntity> COMPACT_HYDRAULIC_ENGINE_ENTITY = REGISTRATE
            .blockEntity("compact_hydraulic_engine", CompactHydraulicEngineEntity::new)
            .visual(() -> OrientedRotatingVisual.of(CFAPartialModels.COMPACT_HYDRAULIC_ENGINE_HEART), false)
            .validBlocks(COMPACT_HYDRAULIC_ENGINE)
            .renderer(() -> CompactHydraulicEngineRenderer::new)
            .register();
    public static final DeferredItem<BlockItem> COMPACT_HYDRAULIC_ENGINE_ITEM = ITEMS.registerSimpleBlockItem("compact_hydraulic_engine", COMPACT_HYDRAULIC_ENGINE);
}
