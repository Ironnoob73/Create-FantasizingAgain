package dev.hail.create_fantasizing.block.transporter;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class TransporterEntity extends SmartBlockEntity{
    ItemStack item;
    TransporterItemHandler itemHandler;
    boolean canPickUpItems;

    private FilteringBehaviour filtering;
    private VersionedInventoryTrackerBehaviour invVersionTracker;
    LerpedFloat flap;
    //private final EnumMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> capCaches = new EnumMap<>(Direction.class);

    public TransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        item = ItemStack.EMPTY;
        itemHandler = new TransporterItemHandler(this);
        canPickUpItems = false;
        flap = createChasingFlap();
    }
    /*public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(
                ForgeCapabilities.ITEM_HANDLER,
                CFABlocks.TRANSPORTER_ENTITY.get(),
                (be, context) -> be.ITEM_HANDLER
        );
    }*/
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
        if (inv == null || !canActivate() || invVersionTracker.stillWaiting(inv))
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
        if (inv == null || !canActivate() || invVersionTracker.stillWaiting(inv))
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

    public void setItem(ItemStack stack) {
        item = stack;
        invVersionTracker.reset();
    }
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        InvManipulationBehaviour invManipulation = new InvManipulationBehaviour(this, (w, p, s) -> new BlockFace(p, s.getValue(TransporterBlock.FACING).getOpposite()));
        behaviours.add(invManipulation);

        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));

        filtering = new FilteringBehaviour(this, new TransporterFilterSlotPositioning());
        filtering.withCallback($ -> invVersionTracker.reset());
        behaviours.add(filtering);

        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen((d) -> canPickUpItems));
        behaviours.add(invVersionTracker = new VersionedInventoryTrackerBehaviour(this));
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

    private LerpedFloat createChasingFlap() {
        return LerpedFloat.linear()
                .startWithValue(.25f)
                .chase(0, .05f, LerpedFloat.Chaser.EXP);
    }

}