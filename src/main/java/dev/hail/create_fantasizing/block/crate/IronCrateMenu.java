package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.gui.menu.MenuBase;
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

public class IronCrateMenu extends MenuBase<IronCrateEntity> {
    public boolean doubleCrate;

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
    protected IronCrateEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = null;
        if (world != null) {
            blockEntity = world.getBlockEntity(readBlockPos);
        }
        if (blockEntity instanceof IronCrateEntity ironCrateEntity)
            return ironCrateEntity;
        return null;
    }
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = doubleCrate ? 40 : 20;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
            contentHolder.inventory.onContentsChanged(index);
        } else
            moveItemStackTo(stack, 0, crateSize, false);

        return ItemStack.EMPTY;
    }
    @Override
    protected void initAndReadInventory(IronCrateEntity contentHolder) {}

    @Override
    protected void addSlots() {
        doubleCrate = contentHolder.isDoubleCrate();
        int x = doubleCrate ? 14 : 44;
        int maxCol = doubleCrate ? 10 : 5;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < maxCol; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * maxCol < 20 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * maxCol - (col + row * maxCol < 20 ? 0 : 20), x + col * 18, row * 18 - 12));
            }
        }

        // player Slots
        int xOffset = doubleCrate ? 20 : 8;
        int yOffset = 117;
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

    @Override
    protected void saveData(IronCrateEntity contentHolder) {}

    @Override
    public boolean stillValid(Player player) {
        return contentHolder != null && contentHolder.canPlayerUse(player);
    }
}