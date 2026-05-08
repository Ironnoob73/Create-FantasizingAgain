package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ResetableLazy;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCrateEntity extends CrateBlockEntity implements Nameable, IHaveHoveringInformation, IHaveGoggleInformation {
    private static final Logger log = LoggerFactory.getLogger(AbstractCrateEntity.class);
    public String customName;
    protected ICapabilityProvider<IItemHandler> itemCapability = null;
    public CrateInventory inventory;
    protected ResetableLazy<IItemHandler> invHandler;

    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
            itemCapability = ICapabilityProvider.of(new ItemHandlerWrapper(new CombinedInvWrapper(inventory, getOtherCrate().inventory)));
        }else{
            itemCapability = ICapabilityProvider.of(new ItemHandlerWrapper(new CombinedInvWrapper(inventory)));
        }
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
        Minecraft mc = Minecraft.getInstance();
        if (GogglesItem.isWearingGoggles(mc.player))
            return false;
        if (hasCustomName() && !Objects.equals(customName, "")){
            CreateLang.text(getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);
            return true;
        }
        return false;
    }
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (hasCustomName() && !Objects.equals(customName, ""))
            CreateLang.text(getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);
        else
            CreateLang.text(getBlockState().getBlock().getName().getString()).style(ChatFormatting.WHITE).forGoggles(tooltip);

        if (isDoubleCrate() && isSecondaryCrate())
            CreateLang.builder().add(getOtherCrate().componentHelper(false)).forGoggles(tooltip);
        CreateLang.builder().add(componentHelper(false)).forGoggles(tooltip);
        if (isDoubleCrate() && !isSecondaryCrate())
            CreateLang.builder().add(getOtherCrate().componentHelper(false)).forGoggles(tooltip);

        if (itemCapability != null){
            for (Component component: contentList(Objects.requireNonNull(itemCapability.getCapability())))
                CreateLang.builder().add(component).forGoggles(tooltip);
        }
        return true;
    }
    private MutableComponent componentHelper(boolean useBlocksAsBars) {
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
