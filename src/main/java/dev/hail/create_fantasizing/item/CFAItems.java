package dev.hail.create_fantasizing.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFAItems {
    public static final ItemEntry<TreeCutterItem> TREE_CUTTER = REGISTRATE.item("tree_cutter", TreeCutterItem::new)
            .properties(p->p.attributes(TreeCutterItem.createAttributes(TreeCutterItem.STURDY, 0.0F, -3.0F))
                    .rarity(Rarity.RARE))
            .register();
    public static final ItemEntry<BlockPlacerItem> BLOCK_PLACER = REGISTRATE.item("block_placer", BlockPlacerItem::new)
            .properties(p->p.rarity(Rarity.RARE))
            .register();
    public static final ItemEntry<Item> PRISMARINE_FAN_BLADES = REGISTRATE.item("prismarine_fan_blades", Item::new).register();
    public static final ItemEntry<Item> STURDY_CONDUIT = REGISTRATE.item("sturdy_conduit", Item::new).register();
    public static final ItemEntry<Item> STURDY_HEAVY_CORE = REGISTRATE.item("sturdy_heavy_core", Item::new).register();
    public static final ItemEntry<SculkEngineFrameItem> SCULK_ENGINE_FRAME = REGISTRATE.item("sculk_engine_frame", SculkEngineFrameItem::new).register();
    public static final ItemEntry<AlternativeChromaticCompoundItem> ALTERNATIVE_CHROMATIC_COMPOUND = REGISTRATE.item("alternative_chromatic_compound", AlternativeChromaticCompoundItem::new).register();
    public static final ItemEntry<Item> TAIJI_CHIPSET = REGISTRATE.item("taiji_chipset", Item::new).properties(properties -> properties.rarity(Rarity.UNCOMMON)).register();

    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_COMPACT_HYDRAULIC_ENGINE = sequencedIngredient("incomplete_compact_hydraulic_engine"),
            INCOMPLETE_COMPACT_WIND_ENGINE = sequencedIngredient("incomplete_compact_wind_engine"),
            INCOMPLETE_SCULK_ENGINE = sequencedIngredient("incomplete_sculk_engine"),
            INCOMPLETE_TAIJI_CHIPSET = sequencedIngredient("incomplete_taiji_chipset");
    public static final ItemEntry<Item> INCOMPLETE_ALTERNATIVE_CHROMATIC_COMPOUND = REGISTRATE.item("incomplete_alternative_chromatic_compound", Item::new).register();
    public static final ItemEntry<Item> UNPROCESSED_ECHO_SHARD = REGISTRATE.item("unprocessed_echo_shard", Item::new).register();
    public static final ItemEntry<Item> UNPROCESSED_HEART_OF_THE_SEA = REGISTRATE.item("unprocessed_heart_of_the_sea", Item::new).register();
    public static final ItemEntry<Item> UNPROCESSED_HEAVY_CORE = REGISTRATE.item("unprocessed_heavy_core", Item::new).register();

    public static final ItemEntry<Item> THINKING_PUFFERFISH = REGISTRATE.item("thinking_pufferfish", Item::new).register();

    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }
    public static void init(){}
}
