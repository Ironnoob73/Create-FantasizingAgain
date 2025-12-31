package dev.hail.create_fantasizing.block.transporter;

import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TransporterItemHandler implements IItemHandler {

    private final TransporterEntity blockEntity;

    public TransporterItemHandler(TransporterEntity be) {
        this.blockEntity = be;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return blockEntity.item;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!blockEntity.canAcceptItem(stack)){
            return stack;
        }
        ItemStack remainder = ItemHelper.limitCountToMaxStackSize(stack, simulate);
        if (!simulate)
            blockEntity.setItem(stack);
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack remainder = blockEntity.item.copy();
        ItemStack split = remainder.split(amount);
        if (!simulate)
            blockEntity.setItem(remainder);
        return split;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Math.min(64, getStackInSlot(slot).getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

}
