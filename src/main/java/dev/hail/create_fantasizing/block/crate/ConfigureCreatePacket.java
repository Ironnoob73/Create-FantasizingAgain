package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import dev.hail.create_fantasizing.event.CFAPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureCreatePacket extends BlockEntityConfigurationPacket<AbstractCrateEntity> {
    public static final StreamCodec<ByteBuf, ConfigureCreatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.pos,
            ByteBufCodecs.VAR_INT, packet -> packet.maxItems,
            ConfigureCreatePacket::new
    );

    private final int maxItems;

    public ConfigureCreatePacket(BlockPos pos, int newMaxItems) {
        super(pos);
        this.maxItems = newMaxItems;
    }

    /*@Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(maxItems);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        maxItems = buffer.readInt();
    }*/

    @Override
    protected void applySettings(ServerPlayer player, AbstractCrateEntity be) {
        be.allowedAmount = maxItems;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CFAPackets.CONFIGURE_CREATE;
    }
}
