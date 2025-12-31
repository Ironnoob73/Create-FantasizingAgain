package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.item.ItemSlots;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CrateInventory extends ItemStackHandler {
    public static final Codec<CrateInventory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemSlots.maxSizeCodec(Integer.MAX_VALUE).fieldOf("items").forGetter(ItemSlots::fromHandler),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("allowedAmount").forGetter(inv->inv.allowedAmount)
    ).apply(instance, CrateInventory::deserialize));

    public int allowedAmount;
    public int itemCount;
    public AbstractCrateEntity crateEntity;

    public CrateInventory(AbstractCrateEntity be, int size) {
        super(size);
        this.crateEntity = be;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot < allowedAmount / 64)
            return super.getSlotLimit(slot);
        else if (slot == allowedAmount / 64)
            return allowedAmount % 64;
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot > allowedAmount / 64)
            return false;
        return super.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (crateEntity != null) {
            crateEntity.setChanged();
            itemCount = 0;
            for (int i = 0; i < getSlots(); i++) {
                itemCount += Math.min(getStackInSlot(i).getCount(), getSlotLimit(i));
            }
        }
        notifyUpdate(true);
    }

    boolean isMain(){
        if (crateEntity != null)
            return !crateEntity.isSecondaryCrate();
        return false;
    }

    private void notifyUpdate(Boolean first) {
        if (crateEntity != null){
            crateEntity.notifyUpdate();
            if (crateEntity.getLevel() != null) {
                crateEntity.getLevel().updateNeighbourForOutputSignal(crateEntity.getBlockPos(), crateEntity.getBlockState().getBlock());
            }
            if (first && crateEntity.isDoubleCrate())
                crateEntity.getOtherCrate().inventory.notifyUpdate(false);
        }
    }

    private static CrateInventory deserialize(ItemSlots slots, int allowedAmount) {
        CrateInventory inventory = new CrateInventory(null, slots.getSize());
        slots.forEach(inventory::setStackInSlot);
        inventory.allowedAmount = allowedAmount;
        return inventory;
    }
}