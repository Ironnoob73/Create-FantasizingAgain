package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractDoubleStorageMenu<T extends AbstractDoubleStorageEntity> extends MenuBase<T> {
    public boolean dualBlock;

    public AbstractDoubleStorageMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AbstractDoubleStorageMenu(MenuType<?> type, int id, Inventory inv, T be) {
        super(type, id, inv, be);
    }

    @Override
    protected T createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = null;
        if (world != null) {
            blockEntity = world.getBlockEntity(readBlockPos);
        }
        if (blockEntity instanceof AbstractDoubleStorageEntity doubleStorageEntity) {
            return (T) doubleStorageEntity;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(T contentHolder) {}

    @Override
    protected void saveData(T contentHolder) {}

    @Override
    public boolean stillValid(Player player) {
        return contentHolder != null && contentHolder.canPlayerUse(player);
    }
}
