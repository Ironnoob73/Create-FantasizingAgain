package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.item.ItemSlots;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateInventory extends ItemStackHandler {
    public static final Codec<CrateInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemSlots.maxSizeCodec(Integer.MAX_VALUE).fieldOf("items").forGetter(ItemSlots::fromHandler),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("allowedAmount").forGetter(inv->inv.allowedAmount)
    ).apply(instance, CrateInventory::deserialize));

    public int allowedAmount;
    public int itemCount;
    public AbstractCrateEntity crateEntity;

    public CrateInventory(@Nullable AbstractCrateEntity be, int size) {
        super(size);
        this.crateEntity = be;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < (allowedAmount - (isMain() ? 0 : 1024)) / 64)
            return super.getSlotLimit(slot);
        else if (slot == (allowedAmount - (isMain() ? 0 : 1024)) / 64)
            return (allowedAmount - (isMain() ? 0 : 1024)) % 64;
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (slot > (allowedAmount - (isMain() ? 0 : 1024)) / 64)
            return false;
        return super.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        notifyUpdate();
        super.onContentsChanged(slot);
        if (crateEntity != null) {
            crateEntity.setChanged();
            itemCount = 0;
            for (int i = 0; i < getSlots(); i++) {
                itemCount += getStackInSlot(i).getCount();
            }
        }
    }

    boolean isMain(){
        if (crateEntity != null)
            return !crateEntity.isSecondaryCrate();
        return false;
    }

    private void notifyUpdate() {
        if (crateEntity != null)
            crateEntity.notifyUpdate();
    }

    private static CrateInventory deserialize(ItemSlots slots, int allowedAmount) {
        CrateInventory inventory = new CrateInventory(null, slots.getSize());
        slots.forEach(inventory::setStackInSlot);
        inventory.allowedAmount = allowedAmount;
        return inventory;
    }
}