package dev.hail.create_fantasizing.block.crate;

import com.mojang.serialization.Codec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.api.contraption.storage.item.WrapperMountedItemStorage;
import com.simibubi.create.content.contraptions.Contraption;
import dev.hail.create_fantasizing.block.CFAMenus;
import dev.hail.create_fantasizing.block.CFAMountedStorageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

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

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        ServerLevel level = player.serverLevel();
        BlockPos localPos = info.pos();
        Vec3 localPosVec = Vec3.atCenterOf(localPos);
        Component menuName = this.getMenuName(info, contraption);

        AbstractCrateEntity blockEntity = (AbstractCrateEntity) contraption.presentBlockEntities.get(localPos);
        OptionalInt id = player.openMenu(this.createMenuProvider(menuName, blockEntity));
        if (id.isPresent()) {
            Vec3 globalPos = contraption.entity.toGlobalVector(localPosVec, 0);
            this.playOpeningSound(level, globalPos);
            return true;
        } else {
            return false;
        }
    }

    protected MenuProvider createMenuProvider(Component name, AbstractCrateEntity blockEntity) {
        MenuConstructor constructor = (id, inv, player) -> new AndesiteCrateMenu(CFAMenus.ANDESITE_CRATE.get(), id, inv, (AndesiteCrateEntity) blockEntity);
        return new SimpleMenuProvider(constructor, name);
    }

    @Override
    protected IItemHandlerModifiable getHandlerForMenu(StructureTemplate.StructureBlockInfo info, Contraption contraption) {
        BlockState state = info.state();
        boolean type = state.getValue(AbstractCrateBlock.CRATE_TYPE).isDouble();
        this.wrapped.crateEntity = (AbstractCrateEntity) contraption.presentBlockEntities.get(info.pos());
        if (!type)
            return this;

        Direction facing = state.getValue(AbstractCrateBlock.FACING);
        BlockPos otherHalfPos = info.pos().relative(facing);

        MountedItemStorage otherHalf = this.getOtherHalf(contraption, otherHalfPos, state.getBlock(), facing);
        if (otherHalf == null)
            return this;

        if (state.getValue(AbstractCrateBlock.FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE) {
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

        return facing == thisFacing && type
                ? (CrateMountedStorage) contraption.getStorage().getMountedItems().storages.get(localPos)
                : null;
    }
    public static CrateMountedStorage fromCrate(AbstractCrateEntity crate) {
        return new CrateMountedStorage(crate.getInventoryOfBlock());
    }
}
