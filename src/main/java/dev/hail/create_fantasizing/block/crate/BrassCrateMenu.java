package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.hail.create_fantasizing.CFAConfig;
import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class BrassCrateMenu extends MenuBase<BrassCrateEntity> {
    public boolean doubleCrate;
    public boolean isFold;
    public int page;

    public BrassCrateMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public BrassCrateMenu(MenuType<?> type, int id, Inventory inv, BrassCrateEntity be) {
        super(type, id, inv, be);
    }

    public static BrassCrateMenu create(int id, Inventory inv, BrassCrateEntity be) {
        return new BrassCrateMenu(CFAMenus.BRASS_CRATE.get(), id, inv, be);
    }

    @Override
    protected BrassCrateEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = null;
        if (world != null) {
            blockEntity = world.getBlockEntity(readBlockPos);
        }
        if (blockEntity instanceof BrassCrateEntity brassCrateEntity)
            return brassCrateEntity;
        return null;
    }
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = isFullInterface() ? 72 : 36;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
            contentHolder.inventory.onContentsChanged(index);
        } else
            moveItemStackTo(stack, 0, crateSize, false);

        return ItemStack.EMPTY;
    }
    @Override
    protected void initAndReadInventory(BrassCrateEntity contentHolder) {}

    @Override
    protected void addSlots() {
        doubleCrate = contentHolder.isDoubleCrate();
        int x = 8;
        int maxRow = isFullInterface() ? 8 : 4;
        int colYOffset = isFullInterface() ? 36 : 0;
        for (int row = 0; row < maxRow; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * 9 < 36 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * 9 - (col + row * 9 < 36 ? 0 : 36), x + col * 18, row * 18 - 12 - colYOffset));
            }
        }

        // player Slots
        int xOffset = 14;
        int yOffset = isFullInterface() ? 153 : 117;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, xOffset + hotbarSlot * 18, yOffset + 58));
        }

        broadcastChanges();
    }

    public boolean isFullInterface(){
        return doubleCrate && !isFold;
    }

    public void refreshMenu(){
        this.player.openMenu(contentHolder);
        CFAConfig.foldInterface = isFold;
    }

    @Override
    protected void saveData(BrassCrateEntity contentHolder) {}

    @Override
    public boolean stillValid(Player player) {
        return contentHolder != null && contentHolder.canPlayerUse(player);
    }
}