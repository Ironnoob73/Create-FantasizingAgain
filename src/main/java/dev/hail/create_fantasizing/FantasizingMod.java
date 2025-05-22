package dev.hail.create_fantasizing;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.CFAPartialModels;
import dev.hail.create_fantasizing.block.CFASpriteShifts;
import dev.hail.create_fantasizing.data.CFADataComponents;
import dev.hail.create_fantasizing.event.CFAPackets;
import dev.hail.create_fantasizing.item.CFAItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import static net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;

@Mod(FantasizingMod.MOD_ID)
public class FantasizingMod
{
    public static final String MOD_ID = "create_fantasizing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATE_FANTASIZING_TAB = CREATIVE_MODE_TABS.register("create_fantasizing_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_fantasizing"))
            .icon(() -> CFABlocks.COMPACT_HYDRAULIC_ENGINE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CFABlocks.COMPACT_HYDRAULIC_ENGINE_ITEM.get());
                output.accept(CFABlocks.COMPACT_WIND_ENGINE_ITEM.get());
                output.accept(CFABlocks.STURDY_GIRDER_ITEM.get());
                output.accept(CFAItems.TREE_CUTTER.get());
                output.accept(CFAItems.BLOCK_PLACER.get());
                output.accept(CFAItems.PRISMARINE_FAN_BLADES.get());
                output.accept(CFAItems.STURDY_CONDUIT.get());
                output.accept(CFAItems.STURDY_HEAVY_CORE.get());
            }).build());

    public FantasizingMod(IEventBus modEventBus)
    {
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);

        CFAItems.ITEMS.register(modEventBus);
        CFABlocks.init();
        CREATIVE_MODE_TABS.register(modEventBus);

        CFADataComponents.register(modEventBus);
        CFAPackets.register();

        CFAPartialModels.init();
        CFASpriteShifts.init();

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
