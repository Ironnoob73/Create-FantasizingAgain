package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureCreatePacket extends BlockEntityConfigurationPacket<AbstractCrateEntity> {

    private int maxItems;
    private String customName;

    public ConfigureCreatePacket(BlockPos pos, int newMaxItems, String customName) {
        super(pos);
        this.maxItems = newMaxItems;
        this.customName = customName;
    }

    public ConfigureCreatePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeInt(maxItems);
        buffer.writeUtf(customName);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        maxItems = buffer.readInt();
        customName = buffer.readUtf();
    }

    @Override
    protected void applySettings(AbstractCrateEntity be) {
        if (be.isDoubleCrate()){
            if (be.isSecondaryCrate()){
                be.inventory.allowedAmount = Math.max(0, maxItems - be.inventory.getSlots() * 64);
                be.getOtherCrate().inventory.allowedAmount = Math.min(maxItems, be.inventory.getSlots() * 64);
            } else{
                be.inventory.allowedAmount = Math.min(maxItems, be.inventory.getSlots() * 64);
                be.getOtherCrate().inventory.allowedAmount = Math.max(0, maxItems - be.inventory.getSlots() * 64);
            }
            be.getOtherCrate().notifyUpdate();
        } else
            be.inventory.allowedAmount = maxItems;
        be.customName = customName;
        be.notifyUpdate();
    }

}
