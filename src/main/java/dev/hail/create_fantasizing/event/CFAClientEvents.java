package dev.hail.create_fantasizing.event;

import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderWrenchBehavior;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerRenderHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class CFAClientEvents {
    @SubscribeEvent public static void onTickPre(ClientTickEvent.Pre event) { onTick();}
    public static void onTick() {
        if (!isGameActive())
            return;
        SturdyGirderWrenchBehavior.tick();
        BlockPlacerRenderHandler.tick();
    }
    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }
}
