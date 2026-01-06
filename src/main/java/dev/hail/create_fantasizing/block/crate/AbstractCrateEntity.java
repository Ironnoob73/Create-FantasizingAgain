package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ResetableLazy;
import com.simibubi.create.foundation.utility.SameSizeCombinedInvWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCrateEntity extends CrateBlockEntity implements Nameable, IHaveHoveringInformation {
    public String customName;
    protected LazyOptional<IItemHandler> itemCapability = null;
    public CrateInventory inventory;
    protected ResetableLazy<IItemHandler> invHandler;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            if (!itemCapability.isPresent())
                return LazyOptional.empty();
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        super.tick();

        if(isSecondaryCrate() && getMainCrate() != null){
            customName = getMainCrate().customName;
        }else if(customName != null && customName.isEmpty()){
            customName = null;
        }
    }

    public int getOverallAllowedAmount(){
        if(isDoubleCrate()){
            if(isSecondaryCrate()){
                return inventory.allowedAmount + getMainCrate().inventory.allowedAmount;
            }else{
                return inventory.allowedAmount + getOtherCrate().inventory.allowedAmount;
            }
        }
        return inventory.allowedAmount;
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
        if (isSecondaryCrate()) {
            AbstractCrateEntity mainCrate = getMainCrate();
            if (mainCrate == null)
                return;
            mainCrate.initCapability();
            if (mainCrate.isRemoved())
                itemCapability = LazyOptional.empty();
            else if (!mainCrate.itemCapability.isPresent())
                itemCapability = LazyOptional.empty();
            else
                itemCapability = mainCrate.itemCapability.cast();
            return;
        }

        IItemHandler itemHandler;
        if(getOtherCrate() != null){
            itemHandler = new VersionedInventoryWrapper(SameSizeCombinedInvWrapper.create(inventory, getOtherCrate().inventory));
        }else{
            itemHandler = new VersionedInventoryWrapper(SameSizeCombinedInvWrapper.create(inventory));
        }
        itemCapability = LazyOptional.of(() -> itemHandler);
    }

    public boolean isDoubleCrate() {
        return getBlockState().getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();
    }

    public boolean isSecondaryCrate() {
        if (!hasLevel())
            return false;
        if (!(getBlockState().getBlock() instanceof AbstractCrateBlock))
            return false;
        return isDoubleCrate() && getBlockState().getValue(AbstractCrateBlock.CRATE_TYPE) == AbstractCrateBlock.CrateType.SECOND;
    }

    public Direction getFacing() {
        return getBlockState().getValue(AbstractCrateBlock.FACING);
    }

    public AbstractCrateEntity getOtherCrate() {
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
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        onSplit();
        invalidateCaps();
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("AllowedAmount", inventory.allowedAmount);
        compound.put("Inventory", inventory.serializeNBT());
        if (customName != null)
            compound.putString("CustomName", customName);

        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        inventory.allowedAmount = compound.getInt("AllowedAmount");
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        if (compound.contains("CustomName", 8))
            this.customName = compound.getString("CustomName");

        super.read(compound, clientPacket);
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
