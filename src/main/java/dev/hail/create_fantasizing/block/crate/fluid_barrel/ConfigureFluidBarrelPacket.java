package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import dev.hail.create_fantasizing.event.CFAPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class ConfigureFluidBarrelPacket extends BlockEntityConfigurationPacket<AbstractFluidBarrelEntity> {
    public static final StreamCodec<ByteBuf, ConfigureFluidBarrelPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, p -> p.pos,
            ByteBufCodecs.VAR_INT, packet -> packet.maxCapacity,
            ByteBufCodecs.STRING_UTF8, packet -> packet.customName,
            ConfigureFluidBarrelPacket::new
    );

    private final int maxCapacity;
    private final String customName;

    public ConfigureFluidBarrelPacket(BlockPos pos, int newCapacity, String customName) {
        super(pos);
        this.maxCapacity = newCapacity;
        this.customName = customName;
    }

    @Override
    protected void applySettings(ServerPlayer player, AbstractFluidBarrelEntity be) {
        if (be.isDoubleCrate() && be.isSecondaryCrate()){
            be.getOtherCrate().notifyUpdate();
            be.getOtherCrate().tankInventory.setCapacity(maxCapacity);
        }
        be.tankInventory.setCapacity(maxCapacity);
        be.customName = customName;
        be.notifyUpdate();
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CFAPackets.CONFIGURE_FLUID_BARREL;
    }
}
