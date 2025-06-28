package dev.hail.create_fantasizing.event;

import dev.hail.create_fantasizing.item.block_placer.BlockPlacerTools;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CFACommonEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START && event.side == LogicalSide.SERVER)
            BlockPlacerTools.itemTransferTick(event.player.level(), event.player);
    }
}
