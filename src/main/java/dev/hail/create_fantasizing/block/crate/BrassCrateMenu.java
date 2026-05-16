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

public class BrassCrateMenu extends AbstractDoubleStorageMenu<BrassCrateEntity> {
    public boolean isFold;
    //public int page;

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
    protected void addSlots() {
        dualBlock = contentHolder.isDoubleCrate();
        /*if (player.hasData(CFAAttachmentTypes.FOLD_INTERFACE)){
            player.getData(CFAAttachmentTypes.FOLD_INTERFACE);
            isFold = player.getData(CFAAttachmentTypes.FOLD_INTERFACE);
        }*/
        player.setData(CFAAttachmentTypes.FOLD_INTERFACE, isFold);
        int x = 22;
        int maxRow = isFullInterface() ? 8 : 4;
        for (int row = 0; row < maxRow; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * 9 < 36 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * 9 - (col + row * 9 < 36 ? 0 : 36), x + col * 18, row * 18 + 27));
            }
        }
        // player Slots
        addPlayerSlots(28, isFullInterface() ? 228 : 156);
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