package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer.AnimatedContainerBehaviour;
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

public class AndesiteCrateMenu extends MenuBase<AndesiteCrateEntity> {
    public AndesiteCrateEntity be;
    public Inventory playerInventory;
    public boolean doubleCrate;

    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        this.playerInventory = inv;
        if (blockEntity instanceof AndesiteCrateEntity) {
            this.be = (AndesiteCrateEntity) blockEntity;
            this.be.handleUpdateTag(extraData.readNbt());
            init();
        }
    }

    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, AndesiteCrateEntity be) {
        super(type, id, inv, be);
        this.be = be;
        this.playerInventory = inv;
        init();
    }

    public static AndesiteCrateMenu create(int id, Inventory inv, AndesiteCrateEntity te) {
        return new AndesiteCrateMenu(CFAMenus.CRATE.get(), id, inv, te);
    }

    private void init() {
        doubleCrate = be.isDoubleCrate();
        int x = doubleCrate ? 23 : 53;
        int maxCol = doubleCrate ? 8 : 4;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < maxCol; ++col) {
                this.addSlot(new SlotItemHandler(be.inventory, col + row * maxCol, x + col * 18, 20 + row * 18));
            }
        }

        // player Slots
        int xOffset = doubleCrate ? 20 : 8;
        int yOffset = 149;
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
    protected AndesiteCrateEntity createOnClient(FriendlyByteBuf extraData) {
        BlockPos readBlockPos = extraData.readBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        BlockEntity blockEntity = world.getBlockEntity(readBlockPos);
        if (blockEntity instanceof AndesiteCrateEntity ppbe)
            return ppbe;
        return null;
    }
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        int crateSize = doubleCrate ? 32 : 16;
        if (index < crateSize) {
            moveItemStackTo(stack, crateSize, slots.size(), false);
            be.inventory.onContentsChanged(index);
        } else
            moveItemStackTo(stack, 0, crateSize - 1, false);

        return ItemStack.EMPTY;
    }
    @Override
    protected void initAndReadInventory(AndesiteCrateEntity contentHolder) {}

    @Override
    protected void addSlots() {
        AndesiteCrateEntity.Inv inventory = contentHolder.inventory;
        int x = 4;
        int y = 4;

        for (int row = 0; row < 2; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new SlotItemHandler(inventory, row * 9 + col, x + col * 18, y + row * 18));

        addPlayerSlots(38, 108);
    }

    @Override
    protected void saveData(AndesiteCrateEntity contentHolder) {}

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        if (!playerIn.level().isClientSide)
            BlockEntityBehaviour.get(contentHolder, AnimatedContainerBehaviour.TYPE)
                    .stopOpen(playerIn);
    }
    @Override
    public boolean stillValid(Player player) {
        return be != null && be.canPlayerUse(player);
    }
}
