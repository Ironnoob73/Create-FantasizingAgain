package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.logistics.crate.CrateBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.hail.create_fantasizing.block.crate.fluid_barrel.AbstractFluidBarrelEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractDoubleStorageBlock extends CrateBlock implements IBE<AbstractDoubleStorageEntity> {
    public static final EnumProperty<AbstractDoubleStorageBlock.CrateType> CRATE_TYPE = EnumProperty.create("crate_type", AbstractDoubleStorageBlock.CrateType.class);
    public AbstractDoubleStorageBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP)
                .setValue(CRATE_TYPE, AbstractDoubleStorageBlock.CrateType.SINGLE));
    }

    @Override
    public Class<AbstractDoubleStorageEntity> getBlockEntityClass() {
        return AbstractDoubleStorageEntity.class;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if(state.getValue(CRATE_TYPE) != AbstractDoubleStorageBlock.CrateType.SINGLE){
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
            return stateIn.setValue(CRATE_TYPE, AbstractDoubleStorageBlock.CrateType.SINGLE);

        return stateIn;
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        if (oldState.getBlock() != state.getBlock() && state.hasBlockEntity()) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (!(blockEntity instanceof AbstractDoubleStorageEntity be))
                return;
            be.notifyUpdate();

            if (be.components().has(DataComponents.CUSTOM_NAME) && be.customName == null){
                MutableComponent customNameComponent = Objects.requireNonNull(be.components().get(DataComponents.CUSTOM_NAME)).copy();
                be.customName = customNameComponent.getString();
            }

            if (state.getValue(CRATE_TYPE).isDouble() && !isMoving /* Fix MountStorage unmount capacity issue By Deepseek V4*/ ){
                AbstractDoubleStorageEntity other = be.getOtherCrate();
                if (other != null) {
                    other.notifyUpdate();
                    if (state.getValue(CRATE_TYPE) == AbstractDoubleStorageBlock.CrateType.MAIN) {
                        onMerge(be, other);
                    } else {
                        onMerge(other, be);
                    }
                }
            }
        }
    }

    public void onMerge(AbstractDoubleStorageEntity be, AbstractDoubleStorageEntity other){
        be.invalidateCapabilities();
        if (other.hasCustomName()){
            be.setCustomName(Objects.requireNonNull(other.getCustomName()));
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                           BlockHitResult hit) {
        if (player.isCrouching())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (player instanceof FakePlayer)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (worldIn.isClientSide)
            return ItemInteractionResult.SUCCESS;
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
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
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof AbstractDoubleStorageEntity storageEntity))
                return;
            storageEntity.invalidateCapabilities();
            storageEntity.onSplit();
            world.removeBlockEntity(pos);
        }
        super.onRemove(state, world, pos, newState, isMoving);
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
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
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
