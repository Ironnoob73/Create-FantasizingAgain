package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import dev.hail.create_fantasizing.block.crate.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * THIS CLASS WAS REWRITTEN BY DEEPSEEK V4
 * Mounted fluid storage for fluid barrels on contraptions.
 * <p>
 * Stores both the fluid tank contents AND the two bucket slot items from the barrel.
 * Implements {@link IMountedFluidStorageInteraction} to allow right-click GUI interaction
 * on contraptions (similar to how {@link dev.hail.create_fantasizing.block.crate.CrateMountedStorage}
 * handles interaction via {@code MountedItemStorage.handleInteraction}).
 * </p>
 * <p>
 * Bucket items are stored as raw {@link ItemStack} fields (NOT as {@code SmartInventory})
 * because {@code SmartInventory} requires a valid {@code SyncedBlockEntity} owner — its
 * {@code onContentsChanged} unconditionally calls {@code owner.notifyUpdate()}, which NPEs
 * with a null owner. The {@code SmartInventory} is only created when a valid proxy entity
 * (which IS a {@code SyncedBlockEntity}) is available in {@link #handleInteraction}.
 * </p>
 * <p>
 * The bucket slots are NOT exposed as an {@link net.neoforged.neoforge.items.IItemHandler}
 * capability on the contraption — this prevents auto-I/O through funnels/chutes while
 * still allowing manual interaction through the GUI.
 * </p>
 *
 * @see IMountedFluidStorageInteraction
 * @see MountedFluidBarrelProxyEntity
 */
public class FluidBarrelMountedStorage extends WrapperMountedFluidStorage<FluidBarrelMountedStorage.Handler>
        implements SyncedMountedStorage, IMountedFluidStorageInteraction {

    // ========================================================================
    // CODEC — serializes capacity, fluid, and both bucket slot items
    // ========================================================================
    public static final MapCodec<FluidBarrelMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            net.minecraft.util.ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidBarrelMountedStorage::getCapacity),
            FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidBarrelMountedStorage::getFluid),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("bucket_slot_0", ItemStack.EMPTY).forGetter(s -> s.bucketSlot0),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("bucket_slot_1", ItemStack.EMPTY).forGetter(s -> s.bucketSlot1)
    ).apply(i, FluidBarrelMountedStorage::new));

    private boolean dirty;

    /**
     * Bucket slot items stored as raw ItemStacks.
     * Stored this way (not as SmartInventory) to avoid NPE from
     * {@code SmartInventory.SyncedStackHandler.onContentsChanged()} which unconditionally
     * calls {@code blockEntity.notifyUpdate()} — with no valid owner, this crashes.
     * A proper SmartInventory is created in {@link #handleInteraction} when the proxy entity is available.
     */
    private ItemStack bucketSlot0 = ItemStack.EMPTY;
    private ItemStack bucketSlot1 = ItemStack.EMPTY;

    // ========================================================================
    // Constructors
    // ========================================================================

    /**
     * Full constructor used by the codec and {@link #fromBarrel(AbstractFluidBarrelEntity)}.
     *
     * @param type     the mounted fluid storage type
     * @param capacity fluid tank capacity (mB)
     * @param stack    the fluid currently stored
     * @param slot0    item in bucket slot 0
     * @param slot1    item in bucket slot 1
     */
    protected FluidBarrelMountedStorage(MountedFluidStorageType<?> type, int capacity,
                                         FluidStack stack, ItemStack slot0, ItemStack slot1) {
        super(type, new Handler(capacity, stack));
        this.wrapped.onChange = () -> this.dirty = true;

        // Store bucket items as raw ItemStacks — no SmartInventory (see class javadoc)
        this.bucketSlot0 = slot0.copy();
        this.bucketSlot1 = slot1.copy();
    }

    /**
     * Convenience constructor for default type.
     */
    public FluidBarrelMountedStorage(int capacity, FluidStack stack, ItemStack slot0, ItemStack slot1) {
        this(CFAMountedStorageTypes.FLUID_BARREL.get(), capacity, stack, slot0, slot1);
    }

    // ========================================================================
    // Accessors
    // ========================================================================

    public FluidStack getFluid() {
        return this.wrapped.getFluid();
    }

    public int getCapacity() {
        return this.wrapped.getCapacity();
    }

    // ========================================================================
    // SyncedMountedStorage
    // ========================================================================

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }

    // ========================================================================
    // Mount / Unmount lifecycle
    // ========================================================================

    /**
     * Restore fluid and bucket slot contents back to the world block entity
     * when the contraption is disassembled.
     */
    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof AbstractFluidBarrelEntity barrel) {
            FluidTank inventory = barrel.tankInventory;
            inventory.setFluid(this.wrapped.getFluid());
            inventory.setCapacity(this.wrapped.getCapacity());

            // ---- Restore bucket slots (barrel entity IS in-world here, so SmartInventory is safe) ----
            barrel.bucketSlots.setStackInSlot(0, this.bucketSlot0.copy());
            barrel.bucketSlots.setStackInSlot(1, this.bucketSlot1.copy());
        }
    }

    /**
     * Sync mounted data to the client-side block entity for visual rendering.
     * Only syncs fluid state — bucket slots do not need client-side visual sync
     * (and touching SmartInventory on a client-only render entity can cause issues).
     */
    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        BlockEntity be = contraption.getBlockEntityClientSide(localPos);
        if (!(be instanceof AbstractFluidBarrelEntity tank))
            return;

        FluidTank inv = tank.tankInventory;
        inv.setFluid(this.getFluid());
        inv.setCapacity(this.getCapacity());
    }

    // ========================================================================
    // Factory methods
    // ========================================================================

    /**
     * Create a mounted storage from a world fluid barrel block entity.
     * Copies fluid and bucket slot contents into an isolated storage.
     */
    public static FluidBarrelMountedStorage fromBarrel(AbstractFluidBarrelEntity tank) {
        FluidTank inventory = tank.tankInventory;
        return new FluidBarrelMountedStorage(
                inventory.getCapacity(),
                inventory.getFluid().copy(),
                tank.bucketSlots.getStackInSlot(0).copy(),
                tank.bucketSlots.getStackInSlot(1).copy()
        );
    }

    /**
     * Legacy deserialization from NBT.
     */
    public static FluidBarrelMountedStorage fromLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        int capacity = nbt.getInt("Capacity");
        FluidStack fluid = FluidStack.parseOptional(registries, nbt);

        // ---- Deserialize bucket slots ----
        ItemStack slot0 = ItemStack.EMPTY;
        ItemStack slot1 = ItemStack.EMPTY;
        if (nbt.contains("Buckets")) {
            CompoundTag bucketsTag = nbt.getCompound("Buckets");
            if (bucketsTag.contains("Items")) {
                net.minecraft.nbt.ListTag items = bucketsTag.getList("Items", net.minecraft.nbt.Tag.TAG_COMPOUND);
                for (int i = 0; i < items.size(); i++) {
                    CompoundTag itemTag = items.getCompound(i);
                    int slot = itemTag.getInt("Slot");
                    ItemStack stack = ItemStack.parseOptional(registries, itemTag);
                    if (slot == 0) slot0 = stack;
                    else if (slot == 1) slot1 = stack;
                }
            }
        }

        return new FluidBarrelMountedStorage(capacity, fluid, slot0, slot1);
    }

    // ========================================================================
    // IMountedFluidStorageInteraction — GUI interaction on contraptions
    // ========================================================================

    /**
     * Handles right-click interaction on a contraption.
     * <p>
     * Creates a {@link MountedFluidBarrelProxyEntity} with fluid and bucket slot data,
     * then opens the appropriate fluid barrel menu. For double barrels, the other half
     * is located and linked so the combined UI works correctly.
     * </p>
     * <p>
     * This is the fluid-storage equivalent of
     * {@link dev.hail.create_fantasizing.block.crate.CrateMountedStorage#handleInteraction}.
     * </p>
     *
     * @param player      the server-side player who right-clicked
     * @param contraption the contraption being interacted with
     * @param info        block info at the clicked position
     * @return {@code true} if the menu was opened successfully
     */
    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption,
                                      StructureTemplate.StructureBlockInfo info) {
        BlockPos localPos = info.pos();
        Vec3 localPosVec = Vec3.atCenterOf(localPos);
        BlockState state = info.state();
        Block sourceBlock = state.getBlock();
        boolean isDouble = state.getValue(AbstractDoubleStorageBlock.CRATE_TYPE).isDouble();

        // ---- Distance-based menu validity checker (replicates MountedItemStorage.isMenuValid) ----
        Predicate<Player> stillValid = p -> {
            if (!contraption.entity.isAlive())
                return false;
            Vec3 currentPos = contraption.entity.toGlobalVector(localPosVec, 0);
            return p.distanceToSqr(currentPos) < 64.0;
        };

        // ---- Create a snapshot SmartFluidTank for the proxy ----
        // Copies current state from the Handler. Fluid changes via pipes/pumps on the
        // contraption do NOT update the GUI in real-time (this is consistent with how
        // standard Create contraption fluid storages work). The GUI shows a snapshot
        // taken when the menu is opened.
        SmartFluidTank mainTank = new SmartFluidTank(getCapacity(), fs -> {
            this.wrapped.setFluid(fs);
            this.dirty = true;
        });
        mainTank.setFluid(getFluid().copy());
        mainTank.setCapacity(getCapacity());

        // ---- Handle double barrels: locate the other half ----
        MountedFluidBarrelProxyEntity otherProxy = null;
        FluidBarrelMountedStorage mainStorage = null;
        if (isDouble && state.getValue(AbstractDoubleStorageBlock.CRATE_TYPE) == AbstractDoubleStorageBlock.CrateType.SECOND) {
            Direction facing = state.getValue(AbstractDoubleStorageBlock.FACING);
            BlockPos otherPos = localPos.relative(facing);
            mainStorage = getOtherHalf(contraption, otherPos, sourceBlock, facing);
            /*

            // ---- Look up the other half via the contraption's fluid storage map ----
            var otherFluidStorage = contraption.getStorage().getFluids().storages.get(otherPos);
            if (otherFluidStorage instanceof FluidBarrelMountedStorage otherBarrelStorage) {
                StructureTemplate.StructureBlockInfo otherInfo = contraption.getBlocks().get(otherPos);
                BlockState otherState = otherInfo != null ? otherInfo.state() : state;

                SmartFluidTank otherTank = new SmartFluidTank(otherBarrelStorage.getCapacity(), fs -> {
                    otherBarrelStorage.wrapped.setFluid(fs);
                    otherBarrelStorage.dirty = true;
                });
                otherTank.setFluid(otherBarrelStorage.getFluid().copy());
                otherTank.setCapacity(otherBarrelStorage.getCapacity());

                // Create other half proxy with bucket items passed as raw ItemStacks
                otherProxy = MountedFluidBarrelProxyEntity.create(
                        sourceBlock, otherState, otherTank,
                        otherBarrelStorage.bucketSlot0.copy(),
                        otherBarrelStorage.bucketSlot1.copy(),
                        true, null, stillValid);
            } //else {
                // Could not find other half — fall back to single barrel
                //isDouble = false;
            //}*/
        }

        MountedFluidBarrelProxyEntity mainProxy;
        if (mainStorage != null) {
            mainTank.setCapacity(mainStorage.getCapacity());
            mainProxy = MountedFluidBarrelProxyEntity.create(
                    sourceBlock, state, mainTank,
                    mainStorage.bucketSlot0.copy(),
                    mainStorage.bucketSlot1.copy(),
                    true, null, stillValid);
        }
        else
            mainProxy = MountedFluidBarrelProxyEntity.create(
                    sourceBlock, state, mainTank,
                    this.bucketSlot0.copy(),
                    this.bucketSlot1.copy(),
                    isDouble, null, stillValid);

        // ---- Link proxies for double barrels ----
        //if (otherProxy != null)
        //    otherProxy.setOtherProxy(mainProxy);

        // ---- Open the fluid barrel menu ----
        player.openMenu(mainProxy, mainProxy::sendToMenu);

        return true;
    }

    /**
     * Copy from {@link CrateMountedStorage}
     */
    @Nullable
    protected FluidBarrelMountedStorage getOtherHalf(Contraption contraption, BlockPos localPos, Block block, Direction thisFacing) {
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null)
            return null;
        BlockState state = info.state();
        if (!state.is(block))
            return null;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        boolean type = state.getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();

        return facing == thisFacing.getOpposite() && type
                ? (FluidBarrelMountedStorage) contraption.getStorage().getFluids().storages.get(localPos) // MODIFIED BY DEEPSEEK V4
                : null;
    }

    // ========================================================================
    // Inner Handler — FluidTank with change callback
    // ========================================================================

    public static final class Handler extends FluidTank {
        private Runnable onChange = () -> {};

        public Handler(int capacity, FluidStack stack) {
            super(capacity);
            this.setFluid(stack);
        }

        @Override
        protected void onContentsChanged() {
            this.onChange.run();
        }
    }
}
