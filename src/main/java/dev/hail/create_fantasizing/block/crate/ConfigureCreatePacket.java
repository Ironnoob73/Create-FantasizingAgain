package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

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
        be.inventory.allowedAmount = maxItems;
        be.customName = customName;
    }

}
