package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class AbstractFluidBarrelEntity extends AbstractDoubleStorageEntity {
    public int singleTankCapacity;
    public int allowedCapacity = -1;
    protected ICapabilityProvider<IFluidHandler> fluidCapability = null;
    public SmartFluidTank tankInventory;
    protected ResetableLazy<IFluidHandler> tankHandler;
    public AbstractFluidBarrelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankHandler = ResetableLazy.of(() -> tankInventory);
        tankInventory = new SmartFluidTank(singleTankCapacity, this::onFluidStackChanged);
    }

    protected void initCapability() {
        if (isSecondaryCrate()) {
            AbstractFluidBarrelEntity mainCrate = (AbstractFluidBarrelEntity) getMainCrate();
            if (mainCrate == null)
                return;
            mainCrate.initCapability();
            fluidCapability = ICapabilityProvider.of(() -> {
                if (mainCrate.isRemoved())
                    return null;
                if (mainCrate.fluidCapability == null)
                    return null;
                return mainCrate.fluidCapability.getCapability();
            });
            return;
        }

        if (getOtherCrate() != null){
            if (isSecondaryCrate()){
                fluidCapability = ICapabilityProvider.of(getOtherCrate().tankInventory);
                getOtherCrate().tankInventory.setCapacity(Math.min(singleTankCapacity * 2, allowedCapacity));
            } else {
                fluidCapability = ICapabilityProvider.of(tankInventory);
                tankInventory.setCapacity(Math.min(singleTankCapacity * 2, allowedCapacity));
            }
        } else {
            fluidCapability = ICapabilityProvider.of(tankInventory);
            tankInventory.setCapacity(Math.min(singleTankCapacity, allowedCapacity));
        }
    }

    @Override
    public AbstractFluidBarrelEntity getOtherCrate() {
        if (super.getOtherCrate() instanceof AbstractFluidBarrelEntity fluidBarrelEntity)
            return fluidBarrelEntity;
        return null;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.tankInventory != null && !tankInventory.isEmpty()){
            compound.put("Tank", tankInventory.getFluid().save(registries));
        }
        compound.putInt("AllowedCapacity", allowedCapacity);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.tankInventory != null) {
            tankInventory.setFluid(FluidStack.parseOptional(registries, compound.getCompound("Tank")));
        }
        allowedCapacity = compound.getInt("AllowedCapacity");
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void onSplit(){
        AbstractDoubleStorageEntity other = getOtherCrate();
        if (other == null)
            return;
        if (other instanceof AbstractFluidBarrelEntity otherBarrel) {
            otherBarrel.invalidateCapabilities();
            if (other == getMainCrate())
                otherBarrel.tankInventory.getFluid().setAmount(Math.min(otherBarrel.tankInventory.getFluid().getAmount(), singleTankCapacity));
            else{
                otherBarrel.tankInventory.setFluid(tankInventory.getFluid());
                otherBarrel.tankInventory.getFluid().setAmount(Math.max(tankInventory.getFluid().getAmount() - singleTankCapacity, 0));
            }
        }
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (level != null && !level.isClientSide) {
            setChanged();
            sendData();
            if (isDoubleCrate()){
                getOtherCrate().setChanged();
                getOtherCrate().sendData();
            }
        }
    }
}
