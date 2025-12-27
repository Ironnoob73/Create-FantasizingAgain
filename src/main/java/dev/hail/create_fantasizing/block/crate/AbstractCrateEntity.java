package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCrateEntity extends CrateBlockEntity {
    public int invSize;
    public int allowedAmount;
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public class Inv extends net.neoforged.neoforge.items.ItemStackHandler {
        public Inv() {
            super(invSize);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < (allowedAmount - (isSecondaryCrate() ? 1024 : 0)) / 64)
                return super.getSlotLimit(slot);
            else if (slot == (allowedAmount - (isSecondaryCrate() ? 1024 : 0)) / 64)
                return (allowedAmount - (isSecondaryCrate() ? 1024 : 0)) % 64;
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot > (allowedAmount - (isSecondaryCrate() ? 1024 : 0)) / 64)
                return false;
            return super.isItemValid(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();

            itemCount = 0;
            for (int i = 0; i < getSlots(); i++) {
                itemCount += getStackInSlot(i).getCount();
            }
        }
    }
    public AbstractCrateEntity.Inv inventory;
    public int itemCount;
    protected ResetableLazy<IItemHandler> invHandler;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if(isSecondaryCrate()){
            allowedAmount = getMainCrate().allowedAmount;
        }
    }

    // Mounted storage
    public ItemStackHandler getInventoryOfBlock() {
        return inventory;
    }
    public void applyInventoryToBlock(ItemStackHandler handler) {
        for (int i = 0; i < inventory.getSlots(); i++)
            inventory.setStackInSlot(i, i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY);
    }

    void initCapability() {
        if (itemCapability != null && itemCapability.getCapability() != null
                && (getOtherCrate() == null || (getOtherCrate().itemCapability != null && getOtherCrate().itemCapability.getCapability() != null)))
            return;
        if (isSecondaryCrate()) {
            AbstractCrateEntity mainCrate = getMainCrate();
            if (mainCrate == null)
                return;
            mainCrate.initCapability();
            itemCapability = ICapabilityProvider.of(() -> {
                if (mainCrate.isRemoved())
                    return null;
                if (mainCrate.itemCapability == null)
                    return null;
                return mainCrate.itemCapability.getCapability();
            });
            return;
        }

        if(getOtherCrate() != null){
            itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper(new CombinedInvWrapper(inventory, getOtherCrate().inventory)));
        }else{
            itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper(new CombinedInvWrapper(inventory)));
        }
    }

    public boolean isDoubleCrate() {
        return getBlockState().getValue(AbstractCrateBlock.DOUBLE);
    }

    public boolean isSecondaryCrate() {
        if (!hasLevel())
            return false;
        if (!(getBlockState().getBlock() instanceof AbstractCrateBlock))
            return false;
        return isDoubleCrate() && getFacing().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
    }

    public Direction getFacing() {
        return getBlockState().getValue(AbstractCrateBlock.FACING);
    }


    public AbstractCrateEntity getOtherCrate() {
        if (!CFABlocks.ANDESITE_CRATE.has(getBlockState()))
            return null;
        BlockEntity blockEntity = null;
        if (level != null) {
            blockEntity = level.getBlockEntity(worldPosition.relative(getFacing()));
        }
        if (blockEntity instanceof AbstractCrateEntity)
            return (AbstractCrateEntity) blockEntity;
        return null;
    }

    public AbstractCrateEntity getMainCrate() {
        if (isSecondaryCrate())
            return getOtherCrate();
        return this;
    }

    public void onSplit() {
        AbstractCrateEntity other = getOtherCrate();
        if (other == null)
            return;
        if (other == getMainCrate()) {
            other.onSplit();
            return;
        }

        other.allowedAmount = Math.max(1, allowedAmount - 1024);
        allowedAmount = Math.min(1024, allowedAmount);
    }

    @Override
    public void destroy() {
        super.destroy();
        onSplit();
        invalidateCapabilities();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("AllowedAmount", allowedAmount);
        compound.put("Inventory", inventory.serializeNBT(registries));

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        allowedAmount = compound.getInt("AllowedAmount");
        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));

        super.read(compound, registries, clientPacket);
    }
}
