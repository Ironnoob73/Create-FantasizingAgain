package dev.hail.create_fantasizing.item;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.item.BlockPlacer.BlockPlacerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.hail.create_fantasizing.FantasizingMod.MOD_ID;

public class CFAItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_COMPACT_HYDRAULIC_ENGINE = sequencedIngredient("incomplete_compact_hydraulic_engine"),
            INCOMPLETE_COMPACT_WIND_ENGINE = sequencedIngredient("incomplete_compact_wind_engine");
    public static final DeferredItem<Item> TREE_CUTTER = ITEMS.registerItem("tree_cutter", TreeCutterItem::new,
            new Item.Properties().attributes(TreeCutterItem.createAttributes(TreeCutterItem.STURDY, 0.0F, -3.0F))
                    .rarity(Rarity.RARE));
    public static final ItemEntry<BlockPlacerItem> BLOCK_PLACER = CFABlocks.REGISTRATE.item("block_placer", BlockPlacerItem::new)
            .properties(p -> p.rarity(Rarity.RARE)).register();
    public static final DeferredItem<Item> PRISMARINE_FAN_BLADES = ITEMS.registerSimpleItem("prismarine_fan_blades");
    public static final DeferredItem<Item> STURDY_CONDUIT = ITEMS.registerSimpleItem("sturdy_conduit");
    public static final DeferredItem<Item> STURDY_HEAVY_CORE = ITEMS.registerSimpleItem("sturdy_heavy_core");
    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return CFABlocks.REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }
}
