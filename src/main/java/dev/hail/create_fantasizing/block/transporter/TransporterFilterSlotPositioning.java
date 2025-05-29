package dev.hail.create_fantasizing.block.transporter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TransporterFilterSlotPositioning extends ValueBoxTransform.Sided {

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction side = getSide();
        float horizontalAngle = AngleHelper.horizontalAngle(side);
        Direction Facing = state.getValue(TransporterBlock.FACING);

        if (!Facing.getAxis().isHorizontal()) {
            Vec3 southLocation = VecHelper.voxelSpace(8, Facing == Direction.DOWN ? 12 : 4, 15.5f);
            return VecHelper.rotateCentered(southLocation, horizontalAngle, Direction.Axis.Y);
        }

        return VecHelper.rotateCentered(VecHelper.voxelSpace(8, 12, 15.5), horizontalAngle, Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = state.getValue(TransporterBlock.FACING);
        if (facing.getAxis().isVertical()) {
            super.rotate(level, pos, state, ms);
            TransformStack.of(ms).rotateXDegrees(22.5f);
            return;
        }

        if (state.getBlock() instanceof TransporterBlock) {
            super.rotate(level, pos, state, ms);
            TransformStack.of(ms).rotateXDegrees(22.5f);
            return;
        }

        float yRot = AngleHelper.horizontalAngle(state.getValue(TransporterBlock.FACING)) + (facing == Direction.DOWN ? 180 : 0);
        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(facing == Direction.DOWN ? -90 : 90);
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = state.getValue(TransporterBlock.FACING);

        if (facing.getAxis().isVertical())
            return direction.getAxis().isHorizontal();
        return direction == facing;
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }

}
