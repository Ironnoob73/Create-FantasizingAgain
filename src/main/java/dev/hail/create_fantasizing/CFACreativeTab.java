package dev.hail.create_fantasizing;

import com.simibubi.create.AllItems;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.item.CFAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CFACreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FantasizingMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("create_fantasizing_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_fantasizing"))
            .icon(CFAItems.TREE_CUTTER::asStack)
            .displayItems((parameters, output) -> {
                output.accept(CFABlocks.COMPACT_HYDRAULIC_ENGINE.get());
                output.accept(CFABlocks.COMPACT_WIND_ENGINE.get());
                output.accept(CFABlocks.STURDY_GIRDER.get());
                output.accept(CFAItems.TREE_CUTTER.get());
                output.accept(CFAItems.BLOCK_PLACER.get());
                output.accept(CFABlocks.TRANSPORTER.get());
                output.accept(CFABlocks.ANDESITE_CRATE.get());
                output.accept(CFAItems.PRISMARINE_FAN_BLADES.get());
                output.accept(CFAItems.STURDY_CONDUIT.get());
                output.accept(CFAItems.STURDY_HEAVY_CORE.get());
                output.accept(AllItems.CHROMATIC_COMPOUND.get());
                output.accept(AllItems.REFINED_RADIANCE.get());
                output.accept(AllItems.SHADOW_STEEL.get());
            }).build());

    public static void init(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
