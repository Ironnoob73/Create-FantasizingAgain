package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageEntity;
import joptsimple.internal.Strings;
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.Mth.ceil;
import static net.minecraft.util.Mth.floor;

public abstract class AbstractFluidBarrelEntity extends AbstractDoubleStorageEntity {
    public int singleTankCapacity;
    protected ICapabilityProvider<IFluidHandler> fluidCapability = null;
    public SmartFluidTank tankInventory;
    protected ResetableLazy<IFluidHandler> tankHandler;
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public SmartInventory bucketSlots;
    protected ResetableLazy<IItemHandler> bucketHandler;
    public AbstractFluidBarrelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankHandler = ResetableLazy.of(() -> tankInventory);
        bucketHandler = ResetableLazy.of(() -> bucketSlots);
        tankInventory = new SmartFluidTank(singleTankCapacity, this::onFluidStackChanged);
        bucketSlots = new SmartInventory(2, this, (slot, stack) ->
                slot == 0 && (GenericItemEmptying.canItemBeEmptied(this.getLevel(), stack) || GenericItemFilling.canItemBeFilled(this.getLevel(), stack)));
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
            itemCapability = ICapabilityProvider.of(() -> {
                if (mainCrate.isRemoved())
                    return null;
                if (mainCrate.itemCapability == null)
                    return null;
                return mainCrate.itemCapability.getCapability();
            });
            return;
        }

        if (getOtherCrate() != null){
            if (isSecondaryCrate()){
                fluidCapability = ICapabilityProvider.of(getOtherCrate().tankInventory);
                //getOtherCrate().tankInventory.setCapacity(Math.min(singleTankCapacity * 2, allowedCapacity));
                itemCapability = ICapabilityProvider.of(getOtherCrate().bucketSlots);
            } else {
                fluidCapability = ICapabilityProvider.of(tankInventory);
                //tankInventory.setCapacity(Math.min(singleTankCapacity * 2, allowedCapacity));
                itemCapability = ICapabilityProvider.of(bucketSlots);
            }
        } else {
            fluidCapability = ICapabilityProvider.of(tankInventory);
            //tankInventory.setCapacity(Math.min(singleTankCapacity, allowedCapacity));
            itemCapability = ICapabilityProvider.of(bucketSlots);
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
        if (this.tankInventory != null){
            compound.putInt("Capacity", tankInventory.getCapacity());
            if (!tankInventory.isEmpty())
                compound.put("Tank", tankInventory.getFluid().save(registries));
        }
        if (this.bucketSlots != null)
            compound.put("Buckets", bucketSlots.serializeNBT(registries));
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.tankInventory != null) {
            tankInventory.setCapacity(compound.getInt("Capacity"));
            tankInventory.setFluid(FluidStack.parseOptional(registries, compound.getCompound("Tank")));
        }
        if (this.bucketSlots != null)
            bucketSlots.deserializeNBT(registries, compound.getCompound("Buckets"));
        super.read(compound, registries, clientPacket);
    }

    @Override
    public void tick(){
        super.tick();
        if (isSecondaryCrate() && getMainCrate() instanceof AbstractFluidBarrelEntity mainBarrel){
            tankInventory = mainBarrel.tankInventory;
            bucketSlots = mainBarrel.bucketSlots;
        } else if (!bucketSlots.isEmpty()) {
            if (GenericItemEmptying.canItemBeEmptied(this.getLevel(), bucketSlots.getStackInSlot(0))) {
                Pair<FluidStack, ItemStack> emptyItem = GenericItemEmptying.emptyItem(level, bucketSlots.getStackInSlot(0), true);
                FluidStack fluidFromItem = emptyItem.getFirst();
                if (tankInventory.fill(fluidFromItem, IFluidHandler.FluidAction.SIMULATE) != fluidFromItem.getAmount())
                    return;
                emptyItem = GenericItemEmptying.emptyItem(level, bucketSlots.getStackInSlot(0).copy(), false);
                ItemStack out = emptyItem.getSecond();
                if (bucketSlots.getStackInSlot(1).isEmpty()) {
                    bucketSlots.getStackInSlot(0).shrink(1);
                    bucketSlots.setStackInSlot(1, out);
                } else {
                    ItemStack outputCopy = bucketSlots.getStackInSlot(1).copy();
                    if (ItemStack.isSameItemSameComponents(outputCopy, out) && outputCopy.getCount() + out.getCount() <= outputCopy.getMaxStackSize()) {
                        bucketSlots.getStackInSlot(0).shrink(1);
                        bucketSlots.getStackInSlot(1).setCount(outputCopy.getCount() + out.getCount());
                    } else {
                        return;
                    }
                }
                tankInventory.fill(fluidFromItem, IFluidHandler.FluidAction.EXECUTE);
                notifyUpdate();
            } else if (GenericItemFilling.canItemBeFilled(this.getLevel(), bucketSlots.getStackInSlot(0))) {
                int fillAmount = GenericItemFilling.getRequiredAmountForItem(level, bucketSlots.getStackInSlot(0), tankInventory.getFluid());
                if (fillAmount == -1)
                    return;
                ItemStack out = GenericItemFilling.fillItem(level, fillAmount, bucketSlots.getStackInSlot(0).copy(), tankInventory.getFluid().copy());
                if (bucketSlots.getStackInSlot(1).isEmpty()) {
                    bucketSlots.getStackInSlot(0).shrink(1);
                    bucketSlots.setStackInSlot(1, out);
                } else {
                    ItemStack outputCopy = bucketSlots.getStackInSlot(1).copy();
                    if (ItemStack.isSameItemSameComponents(outputCopy, out) && outputCopy.getCount() + out.getCount() <= outputCopy.getMaxStackSize()) {
                        bucketSlots.getStackInSlot(0).shrink(1);
                        bucketSlots.getStackInSlot(1).setCount(outputCopy.getCount() + out.getCount());
                    } else {
                        return;
                    }
                }
                tankInventory.drain(fillAmount, IFluidHandler.FluidAction.EXECUTE);
                notifyUpdate();
            }
        }
    }

    @Override
    public void onSplit(){
        AbstractDoubleStorageEntity other = getOtherCrate();
        if (other == null)
            return;
        if (other instanceof AbstractFluidBarrelEntity otherBarrel) {
            otherBarrel.invalidateCapabilities();
            if (isSecondaryCrate()){
                otherBarrel.tankInventory.setCapacity(Math.min(otherBarrel.tankInventory.getCapacity(), singleTankCapacity));
                otherBarrel.tankInventory.getFluid().setAmount(Math.min(tankInventory.getFluid().getAmount(), singleTankCapacity));
            }
            else{
                otherBarrel.tankInventory.setCapacity(Math.max(otherBarrel.tankInventory.getCapacity() - singleTankCapacity, 0));
                otherBarrel.tankInventory.getFluid().setAmount(Math.max(tankInventory.getFluid().getAmount() - singleTankCapacity, 0));
            }
        }
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (level != null && !level.isClientSide) {
            notifyUpdate();
            if (isDoubleCrate()){
                getOtherCrate().notifyUpdate();
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (hasCustomName() && !Objects.equals(customName, ""))
            CreateLang.text(getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);
        else
            CreateLang.text(getBlockState().getBlock().getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);

        if (isDoubleCrate() && isSecondaryCrate())
            CreateLang.builder().add(getOtherCrate().componentHelper(false)).forGoggles(tooltip);
        else
            CreateLang.builder().add(componentHelper(false)).forGoggles(tooltip);

        if (fluidCapability != null && (!isDoubleCrate() && !tankInventory.isEmpty() || isDoubleCrate() && getMainCrate() instanceof AbstractFluidBarrelEntity mainFluidBarrel && !mainFluidBarrel.tankInventory.isEmpty())){
            for (Component component: contentList(Objects.requireNonNull(isDoubleCrate() && getMainCrate() instanceof AbstractFluidBarrelEntity mainFluidBarrel ? mainFluidBarrel.tankInventory : tankInventory), isPlayerSneaking))
                CreateLang.builder().add(component).forGoggles(tooltip);
        }
        return true;
    }
    private MutableComponent componentHelper(boolean useBlocksAsBars) {// TODO
        return barComponent(tankInventory, singleTankCapacity * (isDoubleCrate()? 2 : 1));
    }
    public static MutableComponent barComponent(SmartFluidTank tank, int maxCapacity) {
        MutableComponent bar = Component.empty();
        bar.append(bars(floor(Math.min(tank.getFluidAmount() / 1000f, tank.getCapacity() / 1000f)), ChatFormatting.WHITE));
        if (tank.getFluidAmount() % 1000 != 0 && tank.getFluidAmount() < ceil(tank.getCapacity() / 1000f) * 1000)
            bar.append(bars(1, ChatFormatting.YELLOW));
        bar.append(bars(floor(Math.max((tank.getCapacity() - tank.getFluidAmount())/ 1000f, 0)), ChatFormatting.DARK_GREEN));
        if ((maxCapacity - tank.getCapacity())% 1000f != 0 && tank.getFluidAmount() < floor(tank.getCapacity() / 1000f) * 1000)
            bar.append(bars(1, ChatFormatting.GRAY));
        bar.append(bars(ceil(Math.max((tank.getFluidAmount() - tank.getCapacity())/ 1000f, 0)), ChatFormatting.RED));
        bar.append(bars(floor(Math.min((maxCapacity - tank.getCapacity())/ 1000f, (maxCapacity - tank.getFluidAmount())/ 1000f)), ChatFormatting.DARK_GRAY));
        return bar;
    }
    private static MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }
    public static List<Component> contentList(SmartFluidTank tank, boolean sneaking){
        List<Component> result = new ArrayList<>();
        result.add(Component.translatable("container.shulkerBox.itemCount", tank.getFluid().getHoverName(), (sneaking ? tank.getFluid().getAmount() + "mB" : String.format("%.1f", tank.getFluid().getAmount() / 1000f) + "B")));
        return result;
    }
}
