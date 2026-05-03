package dev.hail.create_fantasizing.event;

import dev.hail.create_fantasizing.CFAConfig;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerTools;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Deque;

@EventBusSubscriber
public class CFACommonEvent {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!event.getEntity().level().isClientSide)
            BlockPlacerTools.itemTransferTick(event.getEntity().level(), event.getEntity());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Deque<BlockPlacerTools.PendingOp> queue = BlockPlacerTools.QUEUE;
        int budget = CFAConfig.blockPlacerBatchSize;
        while (!queue.isEmpty() && budget > 0) {
            BlockPlacerTools.PendingOp op = queue.peek();
            if (op.player().isRemoved()) {
                queue.poll();
                continue;
            }
            BlockPos pos = op.remaining().poll();
            if (pos == null) {
                queue.poll();
                continue;
            }
            op.tool().runSingle(op.world(), pos, op.paintedState(), op.data(), op.player(), op.stack(), op.hand(), op.patterns());
            budget--;
        }
    }
}
