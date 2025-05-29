package dev.hail.create_fantasizing.event;

import com.simibubi.create.Create;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.item.block_placer.ConfigureBlockPlacerPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum CFAPackets implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_BLOCK_PLACER(ConfigureBlockPlacerPacket.class, ConfigureBlockPlacerPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;
    <T extends BasePacketPayload> CFAPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
                clazz, codec
        );
    }
    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }
    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(FantasizingMod.MOD_ID, 1);
        for (CFAPackets packet : CFAPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
