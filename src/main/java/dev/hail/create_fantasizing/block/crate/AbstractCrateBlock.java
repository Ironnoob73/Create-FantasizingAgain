package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.crate.CrateBlock;
import com.simibubi.create.foundation.item.ItemHelper;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractCrateBlock extends CrateBlock {

    public static final EnumProperty<CrateType> CRATE_TYPE = EnumProperty.create("crate_type", CrateType.class);
    public AbstractCrateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP)
                .setValue(CRATE_TYPE, AbstractCrateBlock.CrateType.SINGLE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if(state.getValue(CRATE_TYPE) != CrateType.SINGLE){
            switch (state.getValue(FACING)){
                case SOUTH -> { return Block.box(1, 0, 1, 15, 14, 16);}
                case NORTH -> { return Block.box(1, 0, 0, 15, 14, 15);}
                case WEST -> { return Block.box(0, 0, 1, 15, 14, 15);}
                case EAST -> { return Block.box(1, 0, 1, 16, 14, 15);}
                case UP -> { return Block.box(1, 0, 1, 15, 16, 15);}
            }
        }
        return AllShapes.CRATE_BLOCK_SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
                                           BlockPos currentPos, BlockPos facingPos) {

        boolean isDouble = stateIn.getValue(CRATE_TYPE).isDouble();
        Direction blockFacing = stateIn.getValue(FACING);
        boolean isFacingOther = facingState.getBlock() == this && facingState.getValue(CRATE_TYPE).isDouble()
                && facingState.getValue(FACING) == facing.getOpposite();
        boolean isNowFacingOther = facingState.getBlock() == this && facingState.getValue(FACING) == blockFacing.getOpposite();

        if (!isDouble) {
            if (!isFacingOther)
                return stateIn;
            return stateIn.setValue(CRATE_TYPE, getMainOrSecond(stateIn, facingState))
                    .setValue(FACING, facing);
        }

        if (facing != blockFacing)
            return stateIn;
        if (!isFacingOther && !isNowFacingOther)
            return stateIn.setValue(CRATE_TYPE, CrateType.SINGLE);

        return stateIn;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && state.hasBlockEntity() && state.getValue(CRATE_TYPE).isDouble()) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (!(blockEntity instanceof AbstractCrateEntity be))
                return;

            AbstractCrateEntity other = be.getOtherCrate();
            if (other == null)
                return;

            if (state.getValue(CRATE_TYPE) == CrateType.MAIN) {
                onMerge(be, other);
            } else {
                onMerge(other, be);
            }
        }
    }

    public void onMerge(AbstractCrateEntity be, AbstractCrateEntity other){
        be.inventory.allowedAmount += other.inventory.allowedAmount;
        be.invalidateCapabilities();
        if (other.hasCustomName()){
            be.setCustomName(Objects.requireNonNull(other.getCustomName()));
        }
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();

        if (context.getPlayer() == null || !context.getPlayer()
                .isShiftKeyDown()) {
            for (Direction d : Iterate.directions) {
                BlockState state = world.getBlockState(pos.relative(d));
                if (state.getBlock() == this && !state.getValue(CRATE_TYPE).isDouble())
                    return defaultBlockState().setValue(FACING, d)
                            .setValue(CRATE_TYPE, getMainOrSecond(state, defaultBlockState()));
            }
        }

        Direction placedOnFace = context.getClickedFace()
                .getOpposite();
        BlockState state = world.getBlockState(pos.relative(placedOnFace));
        if (state.getBlock() == this && !state.getValue(CRATE_TYPE).isDouble())
            return defaultBlockState().setValue(FACING, placedOnFace)
                    .setValue(CRATE_TYPE, getMainOrSecond(state, defaultBlockState()));
        return defaultBlockState();
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return originalState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CRATE_TYPE));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof AbstractCrateEntity crateEntity))
                return;
            crateEntity.invalidateCapabilities();
            world.removeBlockEntity(pos);
        }
    }
    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof AbstractCrateEntity abstractCrateEntity) {
            ItemStack itemstack = new ItemStack(blockState.getBlock());
            if (abstractCrateEntity.getLevel() != null) {
                abstractCrateEntity.saveToItem(itemstack, abstractCrateEntity.getLevel().registryAccess());
                CompoundTag copiedComp = Objects.requireNonNull(itemstack.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag();
                Tag crate_inv = copiedComp.getCompound("Inventory").get("Items");
                if (crate_inv instanceof ListTag && !((ListTag) crate_inv).isEmpty()) {
                    List<ItemStack> dropList = new ArrayList<>(List.of());
                    Iterator<Tag> iterator = ((ListTag) crate_inv).iterator();
                    while (iterator.hasNext()){
                        ItemStack stack = ItemStack.parseOptional(abstractCrateEntity.getLevel().registryAccess(), (CompoundTag) iterator.next());
                        if (stack.get(DataComponents.BLOCK_ENTITY_DATA) != null ||
                                stack.get(DataComponents.CONTAINER) != null ) {
                            dropList.add(stack);
                            iterator.remove();
                        }
                    }
                    copiedComp.getCompound("Inventory").put("Items", crate_inv);
                    copiedComp.putInt("AllowedAmount", Math.min(1024, copiedComp.getInt("AllowedAmount")));
                    itemstack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(copiedComp));
                    if (abstractCrateEntity.hasCustomName()){
                        itemstack.set(DataComponents.CUSTOM_NAME, Component.empty().append(blockState.getBlock().getName()).append(" - ").append(Objects.requireNonNull(abstractCrateEntity.getCustomName())));
                    }
                    dropList.add(itemstack);
                    return dropList;
                }
            }
        }
        return super.getDrops(blockState, builder);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof AbstractCrateEntity) {
            AbstractCrateEntity flexCrateBlockEntity = ((AbstractCrateEntity) be).getMainCrate();
            return ItemHelper.calcRedstoneFromInventory(flexCrateBlockEntity.inventory);
        }
        return 0;
    }

    public enum CrateType implements StringRepresentable {
        SINGLE("single"),
        MAIN("main"),
        SECOND("second");

        private final String name;

        CrateType(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public CrateType getOpposite() {
            CrateType var10000;
            switch (this.ordinal()) {
                case 0 -> var10000 = SINGLE;
                case 1 -> var10000 = SECOND;
                case 2 -> var10000 = MAIN;
                default -> throw new MatchException(null, null);
            }

            return var10000;
        }

        public boolean isDouble(){
            return this.ordinal() != 0;
        }
    }

    public CrateType getMainOrSecond(BlockState state, BlockState otherState){
        return otherState.getValue(CRATE_TYPE).isDouble()
                ? otherState.getValue(CRATE_TYPE).getOpposite()
                : state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.NEGATIVE
                ? CrateType.MAIN : CrateType.SECOND;
    }
}
