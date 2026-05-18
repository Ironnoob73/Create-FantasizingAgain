package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.simibubi.create.api.contraption.storage.fluid.WrapperMountedFluidStorage;
import com.simibubi.create.content.contraptions.Contraption;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import dev.hail.create_fantasizing.block.crate.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class FluidBarrelMountedStorage extends WrapperMountedFluidStorage<FluidBarrelMountedStorage.Handler> implements SyncedMountedStorage {
    public static final MapCodec<FluidBarrelMountedStorage> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("capacity").forGetter(FluidBarrelMountedStorage::getCapacity),
            FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(FluidBarrelMountedStorage::getFluid)
    ).apply(i, FluidBarrelMountedStorage::new));

    private boolean dirty;

    protected FluidBarrelMountedStorage(MountedFluidStorageType<?> type, int capacity, FluidStack stack) {
        super(type, new Handler(capacity, stack));
        this.wrapped.onChange = () -> this.dirty = true;
    }

    public FluidBarrelMountedStorage(int capacity, FluidStack stack) {
        this(CFAMountedStorageTypes.FLUID_BARREL.get(), capacity, stack);
    }

    public FluidStack getFluid() {
        return this.wrapped.getFluid();
    }

    public int getCapacity() {
        return this.wrapped.getCapacity();
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof AbstractFluidBarrelEntity barrel) {
            FluidTank inventory = barrel.tankInventory;
            inventory.setFluid(this.wrapped.getFluid());
            inventory.setCapacity(this.wrapped.getCapacity());
        }
    }

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        BlockEntity be = contraption.getBlockEntityClientSide(localPos);
        if (!(be instanceof AbstractFluidBarrelEntity tank))
            return;

        FluidTank inv = tank.tankInventory;
        inv.setFluid(this.getFluid());
        inv.setCapacity(this.getCapacity());
    }

    public static FluidBarrelMountedStorage fromBarrel(AbstractFluidBarrelEntity tank) {
        // tank has update callbacks, make an isolated copy
        FluidTank inventory = tank.tankInventory;
        return new FluidBarrelMountedStorage(inventory.getCapacity(), inventory.getFluid().copy());
    }

    public static FluidBarrelMountedStorage fromLegacy(HolderLookup.Provider registries, CompoundTag nbt) {
        int capacity = nbt.getInt("Capacity");
        FluidStack fluid = FluidStack.parseOptional(registries, nbt);
        return new FluidBarrelMountedStorage(capacity, fluid);
    }

    public static final class Handler extends FluidTank {
        private Runnable onChange = () -> {
        };

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
