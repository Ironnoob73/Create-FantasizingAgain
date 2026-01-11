package dev.hail.create_fantasizing.block.transporter;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TransporterEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    ItemStack item;
    TransporterItemHandler itemHandler;
    LazyOptional<IItemHandler> lazyHandler;
    boolean canPickUpItems;

    private FilteringBehaviour filtering;
    private VersionedInventoryTrackerBehaviour invVersionTracker;

    public TransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        item = ItemStack.EMPTY;
        itemHandler = new TransporterItemHandler(this);
        lazyHandler = LazyOptional.of(() -> itemHandler);
        canPickUpItems = false;
    }
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return lazyHandler.cast();
        return super.getCapability(cap, side);
    }
    @Override
    public void tick() {
        super.tick();

        boolean clientSide = level != null && level.isClientSide && !isVirtual();

        Direction facing = getBlockState().getValue(TransporterBlock.FACING);
        if (!clientSide) {
            if (item.isEmpty() && level.getBlockState(this.worldPosition.relative(facing.getOpposite())).getBlock() != CFABlocks.TRANSPORTER.get()){
                handleInput(grabCapability(facing.getOpposite()).orElse(null));
            }
            handleOutput(grabCapability(facing).orElse(null));
        }

    }

    private void handleInput(@Nullable IItemHandler inv) {
        if (inv == null || !canActivate())
            return;
        Predicate<ItemStack> canAccept = this::canAcceptItem;
        int count = getExtractionAmount();
        ItemHelper.ExtractionCountMode mode = getExtractionMode();
        if (mode == ItemHelper.ExtractionCountMode.UPTO || !ItemHelper.extract(inv, canAccept, mode, count, true).isEmpty()) {
            ItemStack extracted = ItemHelper.extract(inv, canAccept, mode, count, false);
            if (!extracted.isEmpty()) {
                setItem(extracted);
                return;
            }
        }
        invVersionTracker.awaitNewVersion(inv);
    }
    private void handleOutput(@Nullable IItemHandler inv) {
        if (inv == null || !canActivate()/* || invVersionTracker.stillWaiting(inv)*/)
            return;
        setItem(ItemHandlerHelper.insertItemStacked(inv, item, false));
        invVersionTracker.awaitNewVersion(inv);
    }
    private LazyOptional<IItemHandler> grabCapability(Direction side) {
        BlockPos pos = this.worldPosition.relative(side);
        if (level == null)
            return LazyOptional.empty();
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null)
            return LazyOptional.empty();
        if (be instanceof ChuteBlockEntity) {
            if (side != Direction.DOWN || !(be instanceof SmartChuteBlockEntity))
                return LazyOptional.empty();
        }
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getOpposite());
    }

    @Override
    public void initialize() {
        super.initialize();
        refreshBlockState();
    }

    public void setItem(ItemStack stack) {
        item = stack;
        invVersionTracker.reset();
        if (level != null && !level.isClientSide) {
            notifyUpdate();
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));

        filtering = new FilteringBehaviour(this, new TransporterFilterSlotPositioning());
        filtering.showCountWhen(this::supportsAmountOnFilter);
        filtering.withCallback($ -> invVersionTracker.reset());
        behaviours.add(filtering);

        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen((d) -> canPickUpItems));
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
    }

    private boolean supportsAmountOnFilter() {
        BlockState blockState = getBlockState();
        return blockState.getBlock() instanceof TransporterBlock;
    }
    protected boolean canAcceptItem(ItemStack stack) {
        return item.isEmpty() && canActivate() && filtering.test(stack);
    }
    protected boolean canActivate() {
        BlockState blockState = getBlockState();
        return blockState.hasProperty(TransporterBlock.POWERED) && !blockState.getValue(TransporterBlock.POWERED);
    }

    protected int getExtractionAmount() {
        return filtering.isCountVisible() && !filtering.anyAmount() ? filtering.getAmount() : 64;
    }
    protected ItemHelper.ExtractionCountMode getExtractionMode() {
        return filtering.isCountVisible() && !filtering.anyAmount() && !filtering.upTo ? ItemHelper.ExtractionCountMode.EXACTLY
                : ItemHelper.ExtractionCountMode.UPTO;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (!item.isEmpty() && level != null)
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), item);
        setRemoved();
    }
    @Override
    public void invalidate() {
        if (lazyHandler != null)
            lazyHandler.invalidate();
        super.invalidate();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.put("Item", item.serializeNBT());
        super.write(compound, clientPacket);
    }
    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        item = ItemStack.of(compound.getCompound("Item"));

        if (clientPacket)
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate(this));
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!item.isEmpty()){
            CreateLang.translate("tooltip.chute.contains", Component.translatable(item.getDescriptionId())
                            .getString(), item.getCount())
                    .style(ChatFormatting.GREEN)
                    .forGoggles(tooltip);
            return true;
        }
        return false;
    }
}