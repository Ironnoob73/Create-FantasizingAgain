package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.utility.ResetableLazy;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCrateEntity extends CrateBlockEntity implements IMultiBlockEntityContainer.Inventory {
    public int invSize;
    public int allowedAmount;
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public class Inv extends net.neoforged.neoforge.items.ItemStackHandler {
        public Inv() {
            super(invSize);
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
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot > allowedAmount / 64)
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
    protected InventoryIdentifier invId;
    public int itemCount;
    protected ResetableLazy<IItemHandler> invHandler;
    protected BlockPos controller;

    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void updateConnectivity() {
        updateConnectivity = false;
        if (level != null && level.isClientSide()) return;
        if (!isController())
            return;
        //ConnectivityHandler.formMulti(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition)) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();
    }

    public void initCapability() {
        if (itemCapability != null && itemCapability.getCapability() != null)
            return;
        if (!isController()) {
            AbstractCrateEntity controllerBE = getControllerBE();
            if (controllerBE == null)
                return;
            controllerBE.initCapability();
            itemCapability = ICapabilityProvider.of(() -> {
                if (controllerBE.isRemoved())
                    return null;
                if (controllerBE.itemCapability == null)
                    return null;
                return controllerBE.itemCapability.getCapability();
            });
            invId = controllerBE.invId;
        }
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (isCrate(state)) { // safety
            if (level != null) {
                level.setBlock(getBlockPos(), state.setValue(AbstractCrateBlock.DOUBLE, isDoubleCrate()), 6);
            }
        }
        itemCapability = null;
        invalidateCapabilities();
        setChanged();
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

    @Override
    public boolean isController() {
        return !isSecondaryCrate();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    public Direction getFacing() {
        return getBlockState().getValue(AbstractCrateBlock.FACING);
    }


    public AbstractCrateEntity getOtherCrate() {
        if (!isCrate(getBlockState()))
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

    @SuppressWarnings("unchecked")
    @Override
    public AbstractCrateEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = null;
        if (level != null) {
            if(this.controller != null){
                blockEntity = level.getBlockEntity(this.controller);
            }else{
                blockEntity = getMainCrate();
            }
        }
        if (blockEntity instanceof AbstractCrateEntity)
            return (AbstractCrateEntity) blockEntity;
        return null;
    }

    public void removeController(boolean keepContents) {
        if (level != null && level.isClientSide()) return;
        updateConnectivity = true;
        controller = null;
        //radius = 1;
        //length = 1;

        BlockState state = getBlockState();
        if (isCrate(state)) {
            state = state.setValue(AbstractCrateBlock.DOUBLE, false);
            if (getLevel() != null) {
                getLevel().setBlock(worldPosition, state, 22);
            }
        }

        itemCapability = null;
        invalidateCapabilities();
        setChanged();
        sendData();
    }

    @Override
    public void setController(BlockPos controller) {
        if (level != null && level.isClientSide && !isVirtual()) return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        itemCapability = null;
        invalidateCapabilities();
        setChanged();
        sendData();
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
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
        itemCapability = null;
        invalidateCapabilities();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("AllowedAmount", allowedAmount);
        compound.put("Inventory", inventory.serializeNBT(registries));

        if (lastKnownPos != null)
            compound.put("LastKnownPos", NbtUtils.writeBlockPos(lastKnownPos));
        if (!isController() && controller != null)
            compound.put("Controller", NbtUtils.writeBlockPos(controller));

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        allowedAmount = compound.getInt("AllowedAmount");
        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));

        lastKnownPos = null;
        if (compound.contains("LastKnownPos"))
            lastKnownPos = NBTHelper.readBlockPos(compound, "LastKnownPos");

        controller = null;
        if (compound.contains("Controller"))
            controller = NBTHelper.readBlockPos(compound, "Controller");

        super.read(compound, registries, clientPacket);
    }

    public ItemStackHandler getInventoryOfBlock() {
        return inventory;
    }

    public void applyInventoryToBlock(ItemStackHandler handler) {
        for (int i = 0; i < inventory.getSlots(); i++)
            inventory.setStackInSlot(i, i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY);
    }

    @Override
    public boolean hasInventory() { return true; }

    // Other
    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return null;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return 2;
    }

    @Override
    public int getMaxWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public void setWidth(int width) {

    }

    public boolean isCrate(BlockState state) {
        return false;
    }
}
