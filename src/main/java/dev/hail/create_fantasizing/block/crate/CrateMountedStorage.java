package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrateMountedStorage extends WrapperMountedItemStorage<CrateInventory> {
    public static final Codec<CrateMountedStorage> CODEC = CrateInventory.CODEC.xmap(
            CrateMountedStorage::new, storage -> storage.wrapped
    );

    protected CrateMountedStorage(MountedItemStorageType<?> type, CrateInventory handler) {
        super(type, handler);
    }

    public CrateMountedStorage(CrateInventory handler) {
        this(CFAMountedStorageTypes.CRATE.get(), handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        if (be instanceof AbstractCrateEntity crate)
            crate.applyInventoryToBlock(this.wrapped);
    }

    public static CrateMountedStorage fromCrate(AbstractCrateEntity crate) {
        return new CrateMountedStorage(crate.getInventoryOfBlock());
    }
}
