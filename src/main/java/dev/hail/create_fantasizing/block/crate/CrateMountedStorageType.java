package dev.hail.create_fantasizing.block.crate;

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

public class CrateMountedStorageType extends SimpleMountedStorageType<CrateMountedStorage> {
    public CrateMountedStorageType() {
        super(CrateMountedStorage.CODEC);
    }

    @Override
    protected IItemHandler getHandler(Level level, BlockEntity be) {
        return be instanceof Container container ? new InvWrapper(container) : null;
    }

    @Override
    protected CrateMountedStorage createStorage(IItemHandler handler) {
        return new CrateMountedStorage(handler);
    }

    @Override
    @Nullable
    public CrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return be instanceof AbstractCrateEntity crate ? CrateMountedStorage.fromCrate(crate) : null;
    }
}
