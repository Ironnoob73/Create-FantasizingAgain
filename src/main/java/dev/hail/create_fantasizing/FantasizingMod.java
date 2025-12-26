package dev.hail.create_fantasizing;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.CFAMenus;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import dev.hail.create_fantasizing.block.crate.AndesiteCrateEntity;
import dev.hail.create_fantasizing.block.transporter.TransporterEntity;
import dev.hail.create_fantasizing.data.CFADataComponents;
import dev.hail.create_fantasizing.event.CFAPackets;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(FantasizingMod.MOD_ID)
public class FantasizingMod
{
    public static final String MOD_ID = "create_fantasizing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    public FantasizingMod(IEventBus modEventBus)
    {
        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        REGISTRATE.setCreativeTab(CFACreativeTab.TAB);
        CFABlocks.init();
        CFAItems.init();
        CFACreativeTab.init(modEventBus);

        CFADataComponents.register(modEventBus);
        CFAPackets.register();
        CFAMenus.register();
        CFAMountedStorageTypes.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, CFAConfig.SPEC_S);
    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        TransporterEntity.registerCapabilities(event);
        AndesiteCrateEntity.registerCapabilities(event);
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
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
        }
    }
    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
