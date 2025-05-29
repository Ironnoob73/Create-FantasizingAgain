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
        Vec3 vec = VecHelper.voxelSpace(8, 8, 14.55);

        vec = VecHelper.rotateCentered(vec, AngleHelper.horizontalAngle(getSide()), Direction.Axis.Y);
        vec = VecHelper.rotateCentered(vec, AngleHelper.verticalAngle(getSide()), Direction.Axis.X);

        return vec;
    }

    @Override
    protected boolean isSideActive(BlockState state, Direction direction) {
        Direction facing = state.getValue(TransporterBlock.FACING);
        return direction.getAxis() != facing.getAxis();
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        Direction facing = getSide();
        float xRot = facing == Direction.UP ? 90 : facing == Direction.DOWN ? 270 : 0;
        float yRot = AngleHelper.horizontalAngle(facing) + 180;

        if (facing.getAxis() == Direction.Axis.Y)
            TransformStack.of(ms)
                    .rotateYDegrees(180 + AngleHelper.horizontalAngle(state.getValue(TransporterBlock.FACING)));

        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(xRot);
    }

    @Override
    protected Vec3 getSouthLocation() {
        return Vec3.ZERO;
    }

}
