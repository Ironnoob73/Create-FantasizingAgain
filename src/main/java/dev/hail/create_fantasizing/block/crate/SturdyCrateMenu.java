package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFAMenus;
import dev.hail.create_fantasizing.data.CFAAttachmentTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SturdyCrateMenu extends AbstractDoubleStorageMenu<SturdyCrateEntity> {
    public boolean isFold;
    //public int page;

    public SturdyCrateMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public SturdyCrateMenu(MenuType<?> type, int id, Inventory inv, SturdyCrateEntity be) {
        super(type, id, inv, be);
    }

    public static SturdyCrateMenu create(int id, Inventory inv, SturdyCrateEntity be) {
        return new SturdyCrateMenu(CFAMenus.STURDY_CRATE.get(), id, inv, be);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = isFullInterface() ? 100 : 50;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
            contentHolder.inventory.onContentsChanged(index);
        } else
            moveItemStackTo(stack, 0, crateSize, false);

        return ItemStack.EMPTY;
    }

    @Override
    protected void addSlots() {
        dualBlock = contentHolder.isDoubleCrate();
        /*if (player.hasData(CFAAttachmentTypes.FOLD_INTERFACE)){
            player.getData(CFAAttachmentTypes.FOLD_INTERFACE);
            isFold = player.getData(CFAAttachmentTypes.FOLD_INTERFACE);
        }*/
        player.setData(CFAAttachmentTypes.FOLD_INTERFACE, isFold);
        int x = 14;
        int maxRow = isFullInterface() ? 10 : 5;
        for (int row = 0; row < maxRow; ++row) {
            for (int col = 0; col < 10; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * 10 < 50 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * 10 - (col + row * 10 < 50 ? 0 : 50), x + col * 18, row * 18 + 27));
            }
        }

        // player Slots
        int xOffset = 38;
        int yOffset = isFullInterface() ? 246 : 156;
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
        return dualBlock && !isFold;
    }

    public void refreshMenu(){
        this.player.closeContainer();
        this.init(playerInventory, contentHolder);
        this.player.openMenu(contentHolder);
        //CFAConfig.foldInterface = isFold;
    }

    public void setPlayerInterfaceFold(boolean fold){
        this.player.setData(CFAAttachmentTypes.FOLD_INTERFACE, fold);
    }
}