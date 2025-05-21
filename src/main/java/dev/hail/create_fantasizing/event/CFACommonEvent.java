package dev.hail.create_fantasizing.event;

import dev.hail.create_fantasizing.item.BlockPlacer.BlockPlacerTools;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class CFACommonEvent {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide)
            BlockPlacerTools.itemTransferTick(event.getEntity().level(), event.getEntity());
    }
}
