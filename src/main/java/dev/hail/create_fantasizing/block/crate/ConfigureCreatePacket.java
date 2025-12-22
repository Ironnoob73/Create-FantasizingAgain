package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureCreatePacket extends BlockEntityConfigurationPacket<AbstractCrateEntity> {

    private int maxItems;

    public ConfigureCreatePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ConfigureCreatePacket(BlockPos pos, int newMaxItems) {
        super(pos);
        this.maxItems = newMaxItems;
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(maxItems);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        maxItems = buffer.readInt();
    }

    @Override
    protected void applySettings(AbstractCrateEntity be) {
        be.allowedAmount = maxItems;
    }

}
