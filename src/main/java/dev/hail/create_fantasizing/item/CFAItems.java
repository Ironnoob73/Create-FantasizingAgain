package dev.hail.create_fantasizing.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFAItems {
    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_COMPACT_HYDRAULIC_ENGINE = sequencedIngredient("incomplete_compact_hydraulic_engine"),
            INCOMPLETE_COMPACT_WIND_ENGINE = sequencedIngredient("incomplete_compact_wind_engine");
    public static final ItemEntry<TreeCutterItem> TREE_CUTTER = REGISTRATE.item("tree_cutter", TreeCutterItem::new)
            .properties(p->p.rarity(Rarity.RARE))
            .register();
    public static final ItemEntry<BlockPlacerItem> BLOCK_PLACER = REGISTRATE.item("block_placer", BlockPlacerItem::new)
            .properties(p->p.rarity(Rarity.RARE))
            .register();
    public static final ItemEntry<Item> PRISMARINE_FAN_BLADES = REGISTRATE.item("prismarine_fan_blades", Item::new).register();
    public static final ItemEntry<Item> STURDY_CONDUIT = REGISTRATE.item("sturdy_conduit", Item::new).register();
    public static final ItemEntry<Item> STURDY_HEAVY_CORE = REGISTRATE.item("sturdy_heavy_core", Item::new).register();
    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }
    public static void init(){}
}
