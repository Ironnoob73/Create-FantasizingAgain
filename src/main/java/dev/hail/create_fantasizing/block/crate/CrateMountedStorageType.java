package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrateMountedStorageType extends MountedItemStorageType<CrateMountedStorage> {
    public CrateMountedStorageType() {
        super(CrateMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public CrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be != null) {
            be.invalidateCaps();
        }
        return be instanceof AbstractCrateEntity crate ? CrateMountedStorage.fromCrate(crate) : null;
    }
}
