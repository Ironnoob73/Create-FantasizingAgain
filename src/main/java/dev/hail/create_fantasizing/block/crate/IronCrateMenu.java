package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class IronCrateMenu extends AbstractDoubleStorageMenu<IronCrateEntity> {
    public IronCrateMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public IronCrateMenu(MenuType<?> type, int id, Inventory inv, IronCrateEntity be) {
        super(type, id, inv, be);
    }

    public static IronCrateMenu create(int id, Inventory inv, IronCrateEntity be) {
        return new IronCrateMenu(CFAMenus.IRON_CRATE.get(), id, inv, be);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = dualBlock ? 40 : 20;
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
        int x = dualBlock ? 23 : 44;
        int maxCol = dualBlock ? 10 : 5;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < maxCol; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * maxCol < 20 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * maxCol - (col + row * maxCol < 20 ? 0 : 20), x + col * 18, row * 18 + 27));
            }
        }
        // player Slots
        addPlayerSlots(dualBlock ? 46 : 8, 156);
    }
}