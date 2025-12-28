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
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("allowedAmount").forGetter(inv->inv.allowedAmount),
            Codec.BOOL.fieldOf("main").forGetter(inv->inv.crateEntity.isSecondaryCrate())
    ).apply(instance, CrateInventory::deserialize));

    public int allowedAmount;
    public boolean main;
    private final AbstractCrateEntity crateEntity;

    public CrateInventory(@Nullable AbstractCrateEntity be, int size) {
        super(size);
        this.crateEntity = be;
        if (be != null) {
            this.main = !be.isSecondaryCrate();
        }
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
            this.main = !crateEntity.isSecondaryCrate();
            crateEntity.setChanged();
            crateEntity.itemCount = 0;
            for (int i = 0; i < getSlots(); i++) {
                crateEntity.itemCount += getStackInSlot(i).getCount();
            }
        }
    }

    private boolean isMain(){
        main = !crateEntity.isSecondaryCrate();
        return main;
    }

    private void notifyUpdate() {
        if (crateEntity != null)
            crateEntity.notifyUpdate();
    }

    private static CrateInventory deserialize(ItemSlots slots, int allowedAmount, boolean main) {
        CrateInventory inventory = new CrateInventory(null, slots.getSize());
        slots.forEach(inventory::setStackInSlot);
        inventory.main = main;
        return inventory;
    }
}