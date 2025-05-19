package dev.hail.create_fantasizing.block.compat_engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CompactEngineEntity extends GeneratingKineticBlockEntity {
    protected ScrollOptionBehaviour<IControlContraption.RotationMode> movementMode;
    protected ScrollOptionBehaviour<WindmillBearingBlockEntity.RotationDirection> movementDirection;

    public CompactEngineEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.remove(movementMode);
        movementDirection = new ScrollOptionBehaviour<>(WindmillBearingBlockEntity.RotationDirection.class,
                CreateLang.translateDirect("contraptions.windmill.rotation_direction"), this, new MotorValueBox());
        movementDirection.withCallback($ -> onDirectionChanged());
        behaviours.add(movementDirection);
        registerAwardables(behaviours, AllAdvancements.WINDMILL, AllAdvancements.WINDMILL_MAXED);
    }
    private void onDirectionChanged() {
        if (level != null && !level.isClientSide) updateGeneratedRotation();
    }

    @Override
    public void initialize() {
        super.initialize();
        if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
            updateGeneratedRotation();
    }

    @Override
    public float getGeneratedSpeed() {
        if (!CFABlocks.COMPACT_HYDRAULIC_ENGINE.has(getBlockState()) && !CFABlocks.COMPACT_WIND_ENGINE.has(getBlockState()))
            return 0;
        return 16 * getAngleSpeedDirection();
    }
    protected float getAngleSpeedDirection() {
        WindmillBearingBlockEntity.RotationDirection rotationDirection = WindmillBearingBlockEntity.RotationDirection.values()[movementDirection.getValue()];
        return (rotationDirection == WindmillBearingBlockEntity.RotationDirection.CLOCKWISE ? 1 : -1);
    }
    static class MotorValueBox extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 12.5);
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = state.getValue(CreativeMotorBlock.FACING);
            return super.getLocalOffset(level, pos, state).add(Vec3.atLowerCornerOf(facing.getNormal())
                    .scale(-1 / 16f));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            super.rotate(level, pos, state, ms);
            Direction facing = state.getValue(CreativeMotorBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return;
            if (getSide() != Direction.UP)
                return;
            TransformStack.of(ms)
                    .rotateZDegrees(-AngleHelper.horizontalAngle(facing) + 180);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            Direction facing = state.getValue(CreativeMotorBlock.FACING);
            if (facing.getAxis() != Direction.Axis.Y && direction == Direction.DOWN)
                return false;
            return direction.getAxis() != facing.getAxis();
        }

    }
}
