package dev.hail.create_fantasizing.event;

import dev.hail.create_fantasizing.block.sturdy_girder.SturdyGirderWrenchBehavior;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CFAClientEvents {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) { onTick();}
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
