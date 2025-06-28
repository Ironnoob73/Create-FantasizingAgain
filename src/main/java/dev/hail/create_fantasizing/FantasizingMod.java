package dev.hail.create_fantasizing;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.item.CFAItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(FantasizingMod.MOD_ID)
public class FantasizingMod
{
    public static final String MOD_ID = "create_fantasizing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public FantasizingMod(IEventBus modEventBus)
    {
        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        REGISTRATE.setCreativeTab(CFACreativeTab.TAB);
        CFABlocks.init();
        CFAItems.init();
        CFACreativeTab.init(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        //TransporterEntity.registerCapabilities(event);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
