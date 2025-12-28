package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.content.logistics.vault.ItemVaultMountedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrateMountedStorageType extends MountedItemStorageType<CrateMountedStorage> {
    public CrateMountedStorageType() {
        super(CrateMountedStorage.CODEC);
    }

    @Override
    @Nullable
    public CrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return be instanceof AbstractCrateEntity crate ? CrateMountedStorage.fromCrate(crate) : null;
    }
}
