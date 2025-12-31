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
            ByteBufCodecs.STRING_UTF8, packet -> packet.customName,
            ConfigureCreatePacket::new
    );

    private final int maxItems;
    private final String customName;

    public ConfigureCreatePacket(BlockPos pos, int newMaxItems, String customName) {
        super(pos);
        this.maxItems = newMaxItems;
        this.customName = customName;
    }

    @Override
    protected void applySettings(ServerPlayer player, AbstractCrateEntity be) {
        if (be.isDoubleCrate()){
            if (be.isSecondaryCrate()){
                be.inventory.allowedAmount = Math.max(0, maxItems-1024);
                be.getOtherCrate().inventory.allowedAmount = Math.min(maxItems, 1024);
            } else{
                be.inventory.allowedAmount = Math.min(maxItems, 1024);
                be.getOtherCrate().inventory.allowedAmount = Math.max(0, maxItems-1024);
            }
            be.getOtherCrate().notifyUpdate();
        } else
            be.inventory.allowedAmount = maxItems;
        be.customName = customName;
        be.notifyUpdate();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CFAPackets.CONFIGURE_CREATE;
    }
}
