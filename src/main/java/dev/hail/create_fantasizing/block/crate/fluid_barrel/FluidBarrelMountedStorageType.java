package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidBarrelMountedStorageType extends MountedFluidStorageType<FluidBarrelMountedStorage> {
    public FluidBarrelMountedStorageType() {
        super(FluidBarrelMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public FluidBarrelMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be != null) {
            be.invalidateCapabilities();
        }
        if (be instanceof AbstractFluidBarrelEntity barrel && !barrel.isSecondaryCrate())
            return FluidBarrelMountedStorage.fromBarrel(barrel);
        return null;
    }
}
