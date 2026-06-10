package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.crate.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

/**
 * Copy From {@link MountedCrateProxyEntity}
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MountedFluidBarrelProxyEntity extends AbstractFluidBarrelEntity implements MenuProvider {
    private final BlockState blockState;
    private final Block sourceBlock;
    private MountedFluidBarrelProxyEntity otherProxy;
    private final boolean isDouble;
    private final Predicate<Player> stillValidChecker;

    private MountedFluidBarrelProxyEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                          SmartFluidTank tankInventory, Block sourceBlock,
                                          boolean isDouble, @Nullable MountedFluidBarrelProxyEntity otherProxy,
                                          Predicate<Player> stillValidChecker) {
        super(type, pos, state);
        this.blockState = state;
        this.sourceBlock = sourceBlock;
        this.tankInventory = tankInventory;
        this.isDouble = isDouble;
        this.otherProxy = otherProxy;
        this.stillValidChecker = stillValidChecker;
    }

    private static BlockEntityType<?> getEntityType(Block sourceBlock) {
        if (sourceBlock == CFABlocks.IRON_CRATE.get())
            return CFABlocks.IRON_CRATE_ENTITY.get();
        else if (sourceBlock == CFABlocks.BRASS_CRATE.get())
            return CFABlocks.BRASS_CRATE_ENTITY.get();
        else if (sourceBlock == CFABlocks.STURDY_CRATE.get())
            return CFABlocks.STURDY_CRATE_ENTITY.get();
        else
            return CFABlocks.ANDESITE_CRATE_ENTITY.get(); // default
    }

    @Override
    public boolean isMountedProxy() {
        return true;
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public BlockPos getBlockPos() {
        return BlockPos.ZERO;
    }

    @Override
    public boolean isDoubleCrate() {
        return isDouble;
    }

    @Override
    public AbstractFluidBarrelEntity getOtherCrate() {
        return otherProxy;
    }

    public void setOtherProxy(MountedFluidBarrelProxyEntity other) {
        this.otherProxy = other;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        Block block = sourceBlock;
        if (block == CFABlocks.ZINC_FLUID_BARREL.get())
            return ZincFluidBarrelMenu.create(id, playerInv, this);
        else if (block == CFABlocks.GOLD_FLUID_BARREL.get())
            return GoldFluidBarrelMenu.create(id, playerInv, this);
        else if (block == CFABlocks.DIAMOND_FLUID_BARREL.get())
            return DiamondFluidBarrelMenu.create(id, playerInv, this);
        else
            return CopperFluidBarrelMenu.create(id, playerInv, this);
    }

    @Override
    public boolean canPlayerUse(Player player) {
        return stillValidChecker != null && stillValidChecker.test(player);
    }

    @Override
    public void tick() {
        // no-op: proxy is not in a world
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        // No NBT persistence needed
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        // No NBT persistence needed
    }

    @Override
    public void sendToMenu(net.minecraft.network.RegistryFriendlyByteBuf buf) {
        // Sentinel: BlockPos.ZERO markers this as a proxy (normal entities never have ZERO pos)
        buf.writeBlockPos(BlockPos.ZERO);
        buf.writeResourceLocation(
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(sourceBlock));
        buf.writeBoolean(isDouble);
        buf.writeVarInt(singleTankCapacity * (isDouble && otherProxy != null ? 2 : 1));
        buf.writeVarInt(tankInventory.getCapacity());
        buf.writeUtf(customName != null ? customName : "");
        buf.writeEnum(blockState.getValue(AbstractDoubleStorageBlock.FACING));
        buf.writeEnum(blockState.getValue(AbstractDoubleStorageBlock.CRATE_TYPE));
    }

    public Block getSourceBlock() {
        return sourceBlock;
    }

    public static MountedFluidBarrelProxyEntity create(Block sourceBlock, BlockState state,
                                                 SmartFluidTank tankInv,
                                                 boolean isDouble,
                                                 @Nullable MountedFluidBarrelProxyEntity otherProxy,
                                                 Predicate<Player> stillValidChecker) {
        return new MountedFluidBarrelProxyEntity(
                getEntityType(sourceBlock), BlockPos.ZERO, state, tankInv, sourceBlock,
                isDouble, otherProxy, stillValidChecker);
    }

    /**
     * Creates a client-side proxy from buffer data.
     */
    public static MountedFluidBarrelProxyEntity createClient(Block sourceBlock, int slotCount,
                                                       int capacity, String customName,
                                                       boolean isDouble,
                                                       @Nullable MountedFluidBarrelProxyEntity otherProxy,
                                                       Direction facing,
                                                       AbstractDoubleStorageBlock.CrateType crateType) {
        BlockState state = sourceBlock.defaultBlockState()
                .setValue(AbstractDoubleStorageBlock.FACING, facing)
                .setValue(AbstractDoubleStorageBlock.CRATE_TYPE, crateType);
        SmartFluidTank tankInv = new SmartFluidTank(capacity, null);
        tankInv.setCapacity(capacity);
        MountedFluidBarrelProxyEntity proxy = new MountedFluidBarrelProxyEntity(
                getEntityType(sourceBlock), BlockPos.ZERO, state, tankInv, sourceBlock,
                isDouble, otherProxy, p -> true);
        if (!customName.isEmpty())
            proxy.customName = customName;
        return proxy;
    }
}
