package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ResetableLazy;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCrateEntity extends AbstractDoubleStorageEntity {
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public CrateInventory inventory;
    protected ResetableLazy<IItemHandler> invHandler;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        invHandler = ResetableLazy.of(() -> inventory);
    }

    public int getOverallAllowedAmount(){
        if(isDoubleCrate()){
            if(isSecondaryCrate()) {
                AbstractCrateEntity mainCrate = (AbstractCrateEntity) getMainCrate();
                return inventory.allowedAmount + mainCrate.inventory.allowedAmount;
            }else{
                AbstractCrateEntity otherCrate = getOtherCrate();
                return inventory.allowedAmount + otherCrate.inventory.allowedAmount;
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

    protected void initCapability() {
        if (isSecondaryCrate()) {
            AbstractCrateEntity mainCrate = (AbstractCrateEntity) getMainCrate();
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

        if(getOtherCrate() != null)
            itemCapability = ICapabilityProvider.of(new ItemHandlerWrapper(new CombinedInvWrapper(inventory, getOtherCrate().inventory)));
        else
            itemCapability = ICapabilityProvider.of(new ItemHandlerWrapper(new CombinedInvWrapper(inventory)));
    }

    @Override
    public AbstractCrateEntity getOtherCrate() {
        if (super.getOtherCrate() instanceof AbstractCrateEntity crateEntity)
            return crateEntity;
        return null;
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.inventory != null){
            compound.putInt("AllowedAmount", inventory.allowedAmount);
            compound.put("Inventory", inventory.serializeNBT(registries));
        }
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (this.inventory != null) {
            inventory.allowedAmount = compound.getInt("AllowedAmount");
            inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        }
        super.read(compound, registries, clientPacket);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (hasCustomName() && !Objects.equals(customName, ""))
            CreateLang.text(getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);
        else
            CreateLang.text(getBlockState().getBlock().getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);

        AbstractCrateEntity other = getOtherCrate();
        if (isDoubleCrate() && isSecondaryCrate() && other != null)
            CreateLang.builder().add(other.componentHelper(false)).forGoggles(tooltip);
        CreateLang.builder().add(componentHelper(false)).forGoggles(tooltip);
        if (isDoubleCrate() && !isSecondaryCrate() && other != null)
            CreateLang.builder().add(other.componentHelper(false)).forGoggles(tooltip);

        if (itemCapability != null && itemCapability.getCapability() != null){
            for (Component component: contentList(Objects.requireNonNull(itemCapability.getCapability())))
                CreateLang.builder().add(component).forGoggles(tooltip);
        }
        return true;
    }
    private MutableComponent componentHelper(boolean useBlocksAsBars) {// TODO
        return useBlocksAsBars ? blockComponent() : barComponent(inventory);
    }
    public MutableComponent blockComponent() {
        return Component.literal("" +
                "\u2588".repeat(inventory.itemCount) +
                "\u2592".repeat(inventory.allowedAmount - inventory.itemCount) +
                "\u2591".repeat(inventory.getSlots() - inventory.allowedAmount));
    }
    public static MutableComponent barComponent(CrateInventory inventory) {
        MutableComponent bar = Component.empty();
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (i > inventory.allowedAmount / 64 || (i == inventory.allowedAmount / 64 && inventory.allowedAmount % 64 == 0)){
                if (inventory.getStackInSlot(i).isEmpty())
                    bar.append(bars(1, ChatFormatting.DARK_GRAY));
                else
                    bar.append(bars(1, ChatFormatting.RED));
            }else{
                if (inventory.getStackInSlot(i).isEmpty()){
                    if (i < inventory.allowedAmount / 64)
                        bar.append(bars(1, ChatFormatting.DARK_GREEN));
                    else
                        bar.append(bars(1, ChatFormatting.GRAY));
                }
                else if (inventory.getStackInSlot(i).getCount() >= inventory.getStackInSlot(i).getMaxStackSize())
                    bar.append(bars(1, ChatFormatting.WHITE));
                else
                    bar.append(bars(1, ChatFormatting.YELLOW));
            }
        }
        return bar;
    }
    private static MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }
    public static List<Component> contentList(IItemHandler itemHandler){
        List<Component> result = new ArrayList<>();
        int infoCount = 0;
        ChatFormatting infoFormatting = ChatFormatting.WHITE;
        for (int i = 0; i < itemHandler.getSlots(); i++)
            if (itemHandler.getStackInSlot(i) != ItemStack.EMPTY){
                infoCount ++;
                if (infoCount >= 2)
                    infoFormatting = ChatFormatting.GRAY;
                if (infoCount >= 3)
                    infoFormatting = ChatFormatting.DARK_GRAY;
                if (infoCount <= 3)
                    result.add(Component.translatable("container.shulkerBox.itemCount", itemHandler.getStackInSlot(i).getHoverName(), itemHandler.getStackInSlot(i).getCount()).withStyle(infoFormatting));
            }
        if (infoCount - 3 > 0)
            result.add(Component.translatable("container.shulkerBox.more", infoCount - 3).withStyle(ChatFormatting.ITALIC));
        return result;
    }
}
