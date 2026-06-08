package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.menu.StorageInteractionWrapper;
import com.simibubi.create.content.contraptions.Contraption;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
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

import java.util.OptionalInt;
import java.util.function.Consumer;
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
        ServerLevel level = player.serverLevel();
        BlockPos localPos = info.pos();
        Vec3 localPosVec = Vec3.atCenterOf(localPos);

        Predicate<Player> stillValid = p -> {
            Vec3 currentPos = contraption.entity.toGlobalVector(localPosVec, 0);
            return this.isMenuValid(player, contraption, currentPos);
        };

        Component menuName = this.getMenuName(info, contraption);
        IItemHandlerModifiable handler = this.getHandlerForMenu(info, contraption);

        Consumer<Player> onClose = p -> {
            Vec3 newPos = contraption.entity.toGlobalVector(localPosVec, 0);
            this.playClosingSound(level, newPos);
        };

        Block sourceBlock = info.state().getBlock();
        StorageInteractionWrapper wrapper = new StorageInteractionWrapper(handler, stillValid, onClose);

        // Calculate total allowed amount for client-side display
        int totalAllow = this.wrapped.allowedAmount;
        if (handler instanceof CombinedInvWrapper) {
            BlockState blockState = info.state();
            CrateMountedStorage other = getOtherHalf(contraption,
                    info.pos().relative(blockState.getValue(AbstractCrateBlock.FACING)),
                    sourceBlock, blockState.getValue(AbstractCrateBlock.FACING));
            if (other != null)
                totalAllow += other.wrapped.allowedAmount;
        }
        final int totalAllowed = totalAllow;

        MenuProvider provider = new SimpleMenuProvider(
                (id, inv, p) -> new MountedCrateMenu(id, inv, wrapper, handler, sourceBlock),
                menuName
        );

        OptionalInt id = player.openMenu(provider, buf -> {
            buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(sourceBlock));
            buf.writeVarInt(handler.getSlots());
            buf.writeVarInt(totalAllowed);
        });

        if (id.isPresent()) {
            Vec3 globalPos = contraption.entity.toGlobalVector(localPosVec, 0);
            this.playOpeningSound(level, globalPos);
            return true;
        } else {
            return false;
        }
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
                ? (CrateMountedStorage) contraption.getStorage().getMountedItems().storages.get(localPos)
                : null;
    }

    public static CrateMountedStorage fromCrate(AbstractCrateEntity crate) {
        return new CrateMountedStorage(crate.getInventoryOfBlock());
    }
}
