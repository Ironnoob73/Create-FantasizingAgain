package dev.hail.create_fantasizing;

import dev.hail.create_fantasizing.block.CFAPartialModels;
import dev.hail.create_fantasizing.block.CFASpriteShifts;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = FantasizingMod.MOD_ID, dist = Dist.CLIENT)
public class CFAClient {
    public CFAClient(IEventBus modEventBus) {
        CFAPartialModels.init();
        CFASpriteShifts.init();
        modEventBus.addListener(CFAClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
    }
}
