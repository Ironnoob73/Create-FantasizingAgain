package dev.hail.create_fantasizing.block.transporter;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;

public class TransporterEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    ItemStack item;
    TransporterItemHandler itemHandler;
    boolean canPickUpItems;

    private FilteringBehaviour filtering;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    private final EnumMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> capCaches = new EnumMap<>(Direction.class);

    public TransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        item = ItemStack.EMPTY;
        itemHandler = new TransporterItemHandler(this);
        canPickUpItems = false;
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CFABlocks.TRANSPORTER_ENTITY.get(),
                (be, context) -> be.itemHandler
        );
    }
    @Override
    public void tick() {
        super.tick();

        boolean clientSide = level != null && level.isClientSide && !isVirtual();

        Direction facing = getBlockState().getValue(TransporterBlock.FACING);
        if (!clientSide) {
            if (item.isEmpty() && level.getBlockState(this.worldPosition.relative(facing.getOpposite())).getBlock() != CFABlocks.TRANSPORTER.get()){
                handleInput(grabCapability(facing.getOpposite()));
            }
            handleOutput(grabCapability(facing));
        }

    }

    private void handleInput(@Nullable IItemHandler inv) {
        if (inv == null || !canActivate()/* || invVersionTracker.stillWaiting(inv)*/)
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
    private @Nullable IItemHandler grabCapability(@NotNull Direction facing) {
        BlockPos pos = this.worldPosition.relative(facing);
        if (level == null)
            return null;
        if (capCaches.get(facing) == null) {
            if (level instanceof ServerLevel serverLevel) {
                BlockCapabilityCache<IItemHandler, @Nullable Direction> cache = BlockCapabilityCache.create(
                        Capabilities.ItemHandler.BLOCK,
                        serverLevel,
                        pos,
                        facing.getOpposite()
                );
                capCaches.put(facing, cache);
                return cache.getCapability();
            } else {
                return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, facing.getOpposite());
            }
        } else {
            return capCaches.get(facing).getCapability();
        }
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
        if (itemHandler != null)
            invalidateCapabilities();
        capCaches.clear();
        super.invalidate();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Item", item.saveOptional(registries));
    }
    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        item = ItemStack.parseOptional(registries, compound.getCompound("Item"));

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