package dev.hail.create_fantasizing;

import dev.hail.create_fantasizing.block.CFAPartialModels;
import dev.hail.create_fantasizing.block.CFASpriteShifts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CFAClient {
    public CFAClient(IEventBus modEventBus) {
        CFAPartialModels.init();
        CFASpriteShifts.init();
        modEventBus.addListener(CFAClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
    }
}
