package dev.hail.create_fantasizing.block.sturdy_girder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.decoration.girder.GirderEncasedShaftBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SturdyGirderEncasedShaftBlock extends GirderEncasedShaftBlock {
    public SturdyGirderEncasedShaftBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return CFABlocks.STURDY_GIRDER.getDefaultState()
                .setValue(WATERLOGGED, originalState.getValue(WATERLOGGED))
                .setValue(SturdyGirderBlock.X, originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.Z)
                .setValue(SturdyGirderBlock.Z, originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.X)
                .setValue(SturdyGirderBlock.AXIS, originalState.getValue(HORIZONTAL_AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X)
                .setValue(SturdyGirderBlock.BOTTOM, originalState.getValue(BOTTOM))
                .setValue(SturdyGirderBlock.TOP, originalState.getValue(TOP));
    }
    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return CFABlocks.STURDY_GIRDER_ENCASED_SHAFT_ENTITY.get();
    }
    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        return ItemRequirement.of(AllBlocks.SHAFT.getDefaultState(), be)
                .union(ItemRequirement.of(CFABlocks.STURDY_GIRDER.getDefaultState(), be));
    }
}
