package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class CrateMountedStorage extends SimpleMountedStorage {
    public static final MapCodec<CrateMountedStorage> CODEC = SimpleMountedStorage.codec(CrateMountedStorage::new);

    protected CrateMountedStorage(MountedItemStorageType<?> type, IItemHandler handler) {
        super(type, handler);
    }

    public CrateMountedStorage(IItemHandler handler) {
        this(CFAMountedStorageTypes.CRATE.get(), handler);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        // the capability will include both sides of chests, but mounted storage is 1:1
        if (be instanceof Container container && this.getSlots() == container.getContainerSize()) {
            ItemHelper.copyContents(this, new InvWrapper(container));
        }
    }

    @Override
    protected IItemHandlerModifiable getHandlerForMenu(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        BlockState state = info.state();
        boolean type = state.getValue(AbstractCrateBlock.DOUBLE);
        if (!type)
            return this;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        //Direction connectedDirection = AbstractCrateBlock.getConnectedDirection(state);
        BlockPos otherHalfPos = info.pos().relative(facing);

        MountedItemStorage otherHalf = this.getOtherHalf(contraption, otherHalfPos, state.getBlock(), facing, type);
        if (otherHalf == null)
            return this;

        if (state.getValue(AbstractCrateBlock.FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            return new CombinedInvWrapper(this, otherHalf);
        } else {
            return new CombinedInvWrapper(otherHalf, this);
        }
    }

    @Nullable
    protected MountedItemStorage getOtherHalf(Contraption contraption, BlockPos localPos, Block block,
                                              Direction thisFacing, Boolean thisType) {
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null)
            return null;
        BlockState state = info.state();
        if (!state.is(block))
            return null;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        Boolean type = state.getValue(AbstractCrateBlock.DOUBLE);

        return facing == thisFacing && type == thisType
                ? contraption.getStorage().getMountedItems().storages.get(localPos)
                : null;
    }
}
