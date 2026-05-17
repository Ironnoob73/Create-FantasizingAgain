package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractDoubleStorageEntity extends CrateBlockEntity implements Nameable, IHaveHoveringInformation, IHaveGoggleInformation, MenuProvider {
    public String customName;

    public AbstractDoubleStorageEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (isSecondaryCrate() && getMainCrate() != null) {
            customName = getMainCrate().customName;
        } else if (customName != null && customName.isEmpty()) {
            customName = null;
        }
    }

    public boolean isDoubleCrate() {
        return getBlockState().getValue(AbstractDoubleStorageBlock.CRATE_TYPE).isDouble();
    }

    public boolean isSecondaryCrate() {
        if (!hasLevel())
            return false;
        if (!(getBlockState().getBlock() instanceof AbstractDoubleStorageBlock))
            return false;
        return isDoubleCrate() && getBlockState().getValue(AbstractDoubleStorageBlock.CRATE_TYPE) == AbstractDoubleStorageBlock.CrateType.SECOND;
    }

    public Direction getFacing() {
        return getBlockState().getValue(AbstractDoubleStorageBlock.FACING);
    }

    public AbstractDoubleStorageEntity getOtherCrate() {
        BlockEntity blockEntity = null;
        if (level != null) {
            blockEntity = level.getBlockEntity(worldPosition.relative(getFacing()));
        }
        if (blockEntity instanceof AbstractDoubleStorageEntity)
            return (AbstractDoubleStorageEntity) blockEntity;
        return null;
    }

    public AbstractDoubleStorageEntity getMainCrate() {
        if (isSecondaryCrate())
            return getOtherCrate();
        return this;
    }

    public void onSplit() {
        AbstractDoubleStorageEntity other = getOtherCrate();
        if (other == null)
            return;
        if (other == getMainCrate())
            other.onSplit();
    }

    @Override
    public void destroy() {
        super.destroy();
        onSplit();
        invalidateCapabilities();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (customName != null)
            compound.putString("CustomName", customName);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
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
    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getName().getString());
    }
}
