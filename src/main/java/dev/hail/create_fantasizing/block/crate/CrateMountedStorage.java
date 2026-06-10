package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class CrateMountedStorage extends WrapperMountedItemStorage<CrateInventory> {
    public static final MapCodec<CrateMountedStorage> CODEC = CrateInventory.CODEC.xmap(
            CrateMountedStorage::new, storage -> storage.wrapped
    ).fieldOf("value");

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


    /**
     * THIS METHOD WAS REWRITTEN BY DEEPSEEK V4
     */
    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        BlockPos localPos = info.pos();
        Vec3 localPosVec = Vec3.atCenterOf(localPos);
        BlockState state = info.state();
        Block sourceBlock = state.getBlock();
        boolean isDouble = state.getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();

        Predicate<Player> stillValid = p -> {
            Vec3 currentPos = contraption.entity.toGlobalVector(localPosVec, 0);
            return this.isMenuValid(player, contraption, currentPos);
        };

        // Create proxy for other half (double crates only)
        MountedCrateProxyEntity otherProxy = null;
        if (isDouble) {
            Direction facing = state.getValue(AbstractCrateBlock.FACING);
            BlockPos otherPos = localPos.relative(facing);
            CrateMountedStorage otherStorage = getOtherHalf(contraption, otherPos, sourceBlock, facing);
            if (otherStorage != null) {
                // Get the other block's state for its proxy
                StructureTemplate.StructureBlockInfo otherInfo = contraption.getBlocks().get(otherPos);
                BlockState otherState = otherInfo != null ? otherInfo.state() : state;
                otherProxy = MountedCrateProxyEntity.create(
                        sourceBlock, otherState, otherStorage.wrapped, true, null, stillValid);
            } else {
                // Could not find other half — treat as single crate to avoid NPE
                isDouble = false;
            }
        }

        // Create main proxy with this storage's inventory
        MountedCrateProxyEntity mainProxy = MountedCrateProxyEntity.create(
                sourceBlock, state, this.wrapped, isDouble, otherProxy, stillValid);

        // Link other proxy back to main proxy
        if (otherProxy != null)
            otherProxy.setOtherProxy(mainProxy);

        player.openMenu(mainProxy, mainProxy::sendToMenu);

        return true;
    }

    @Override
    protected IItemHandlerModifiable getHandlerForMenu(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        BlockState state = info.state();
        boolean type = state.getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();
        if (!type)
            return this;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        BlockPos otherHalfPos = info.pos().relative(facing);

        MountedItemStorage otherHalf = this.getOtherHalf(contraption, otherHalfPos, state.getBlock(), facing);
        if (otherHalf == null)
            return this;

        if (state.getValue(AbstractCrateBlock.CRATE_TYPE) == AbstractDoubleStorageBlock.CrateType.MAIN) {
            return new CombinedInvWrapper(this, otherHalf);
        } else {
            return new CombinedInvWrapper(otherHalf, this);
        }
    }

    @Nullable
    protected CrateMountedStorage getOtherHalf(Contraption contraption, BlockPos localPos, Block block, Direction thisFacing) {
        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        if (info == null)
            return null;
        BlockState state = info.state();
        if (!state.is(block))
            return null;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        boolean type = state.getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();

        return facing == thisFacing.getOpposite() && type
                ? (CrateMountedStorage) contraption.getStorage().getAllItemStorages().get(localPos) // MODIFIED BY DEEPSEEK V4
                : null;
    }

    public static CrateMountedStorage fromCrate(AbstractCrateEntity crate) {
        return new CrateMountedStorage(crate.getInventoryOfBlock());
    }
}
