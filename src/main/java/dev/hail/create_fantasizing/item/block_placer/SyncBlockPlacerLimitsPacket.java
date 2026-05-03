package dev.hail.create_fantasizing.item.block_placer;

import dev.hail.create_fantasizing.CFAConfig;
import dev.hail.create_fantasizing.FantasizingMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBlockPlacerLimitsPacket(
        int cuboidMax,
        int sphereMax,
        int cylinderMaxRadius,
        int cylinderMaxHeight,
        int dynamicMax
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncBlockPlacerLimitsPacket> TYPE =
            new CustomPacketPayload.Type<>(FantasizingMod.resourceLocation("sync_bp_limits"));

    public static final StreamCodec<ByteBuf, SyncBlockPlacerLimitsPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SyncBlockPlacerLimitsPacket::cuboidMax,
                    ByteBufCodecs.VAR_INT, SyncBlockPlacerLimitsPacket::sphereMax,
                    ByteBufCodecs.VAR_INT, SyncBlockPlacerLimitsPacket::cylinderMaxRadius,
                    ByteBufCodecs.VAR_INT, SyncBlockPlacerLimitsPacket::cylinderMaxHeight,
                    ByteBufCodecs.VAR_INT, SyncBlockPlacerLimitsPacket::dynamicMax,
                    SyncBlockPlacerLimitsPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(FantasizingMod.MOD_ID)
                .playToClient(TYPE, STREAM_CODEC, SyncBlockPlacerLimitsPacket::handle);
    }

    private static void handle(SyncBlockPlacerLimitsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            CFAConfig.blockPlacerCuboidMaxSize = packet.cuboidMax();
            CFAConfig.blockPlacerSphereMaxRadius = packet.sphereMax();
            CFAConfig.blockPlacerCylinderMaxRadius = packet.cylinderMaxRadius();
            CFAConfig.blockPlacerCylinderMaxHeight = packet.cylinderMaxHeight();
            CFAConfig.blockPlacerDynamicMaxRadius = packet.dynamicMax();
        });
    }
}
