package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class AbstractCrateEntity extends CrateBlockEntity implements Nameable, IHaveHoveringInformation {
    public String customName;
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public CrateInventory inventory;
    public int itemCount;
    protected ResetableLazy<IItemHandler> invHandler;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if(isSecondaryCrate()){
            inventory.allowedAmount = getMainCrate().inventory.allowedAmount;
            customName = getMainCrate().customName;
        }else if(customName != null && customName.isEmpty()){
            customName = null;
        }
    }

    // Mounted storage
    public CrateInventory getInventoryOfBlock() {
        return inventory;
    }
    public void applyInventoryToBlock(CrateInventory handler) {
        for (int i = 0; i < inventory.getSlots(); i++)
            inventory.setStackInSlot(i, i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY);
    }

    void initCapability() {
        if (itemCapability != null && itemCapability.getCapability() != null
                && (getOtherCrate() == null || (getOtherCrate().itemCapability != null && getOtherCrate().itemCapability.getCapability() != null))){
            if (isDoubleCrate() && itemCapability.getCapability().getSlots() == inventory.getSlots() * 2)
                return;
        }
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

        other.inventory.allowedAmount = Math.max(1, inventory.allowedAmount - 1024);
        inventory.allowedAmount = Math.min(1024, inventory.allowedAmount);
    }

    @Override
    public void destroy() {
        super.destroy();
        onSplit();
        invalidateCapabilities();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("AllowedAmount", inventory.allowedAmount);
        compound.put("Inventory", inventory.serializeNBT(registries));
        if (customName != null)
            compound.putString("CustomName", customName);

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        inventory.allowedAmount = compound.getInt("AllowedAmount");
        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        if (compound.contains("CustomName", 8))
            this.customName = compound.getString("CustomName");

        super.read(compound, registries, clientPacket);
    }

    protected void swapContents(){
        CrateInventory contents = inventory;
        inventory = getMainCrate().inventory;
        getMainCrate().inventory = contents;
    }

    public void setCustomName(Component customName) {
        this.customName = customName.getString();
    }

    @Override
    public Component getCustomName() {
        return Component.literal(customName);
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public @NotNull Component getName() {
        return Component.literal(customName);
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (hasCustomName()){
            CreateLang.text(getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);
            return true;
        }
        return false;
    }
}
