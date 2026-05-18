package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.item.SmartInventory;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFluidBarrelMenu<T extends AbstractFluidBarrelEntity> extends AbstractDoubleStorageMenu<T> {
    public AbstractFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AbstractFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, T be) {
        super(type, id, inv, be);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = 2;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
        } else
            moveItemStackTo(stack, 0, crateSize, false);

        return ItemStack.EMPTY;
    }

    @Override
    protected void addSlots() {
        dualBlock = contentHolder.isDoubleCrate();
        SmartInventory buckets = contentHolder.bucketSlots;
        if (contentHolder.isSecondaryCrate() && contentHolder.getMainCrate() instanceof AbstractFluidBarrelEntity mainBarrel)
            buckets = mainBarrel.bucketSlots;
        for (int i = 0; i <= 1; i++) {
            this.addSlot(
                    new SlotItemHandler(buckets, i, 31 + (i * 18), 27));
        }
        // player Slots
        addPlayerSlots(28, 102);
    }
}
