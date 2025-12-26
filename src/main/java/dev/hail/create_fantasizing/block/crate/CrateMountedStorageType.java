package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class CrateMountedStorageType extends SimpleMountedStorageType<CrateMountedStorage> {
    public CrateMountedStorageType() {
        super(CrateMountedStorage.CODEC);
    }

    @Override
    protected IItemHandler getHandler(Level level, BlockEntity be) {
        return be instanceof Container container ? new InvWrapper(container) : null;
    }

    @Override
    protected SimpleMountedStorage createStorage(IItemHandler handler) {
        return new CrateMountedStorage(handler);
    }
}
