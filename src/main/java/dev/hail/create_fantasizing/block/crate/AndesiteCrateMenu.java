package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class AndesiteCrateMenu extends MenuBase<AndesiteCrateEntity> {
    public boolean doubleCrate;

    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, AndesiteCrateEntity be) {
        super(type, id, inv, be);
    }

    public static AndesiteCrateMenu create(int id, Inventory inv, AndesiteCrateEntity be) {
        return new AndesiteCrateMenu(CFAMenus.ANDESITE_CRATE.get(), id, inv, be);
    }

    @Override
    protected AndesiteCrateEntity createOnClient(FriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = null;
        if (world != null) {
            blockEntity = world.getBlockEntity(readBlockPos);
        }
        if (blockEntity instanceof AndesiteCrateEntity andesiteCrateEntity)
            return andesiteCrateEntity;
        return null;
    }
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = doubleCrate ? 32 : 16;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
            contentHolder.inventory.onContentsChanged(index);
        } else
            moveItemStackTo(stack, 0, crateSize, false);

        return ItemStack.EMPTY;
    }
    @Override
    protected void initAndReadInventory(AndesiteCrateEntity contentHolder) {}

    @Override
    protected void addSlots() {
        doubleCrate = contentHolder.isDoubleCrate();
        int x = doubleCrate ? 31 : 53;
        int maxCol = doubleCrate ? 8 : 4;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < maxCol; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * maxCol < 16 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * maxCol - (col + row * maxCol < 16 ? 0 : 16), x + col * 18, row * 18 + 27));
            }
        }

        // player Slots
        int xOffset = doubleCrate ? 28 : 8;
        int yOffset = 156;
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
    protected void saveData(AndesiteCrateEntity contentHolder) {}

    @Override
    public boolean stillValid(Player player) {
        return contentHolder != null && contentHolder.canPlayerUse(player);
    }
}
