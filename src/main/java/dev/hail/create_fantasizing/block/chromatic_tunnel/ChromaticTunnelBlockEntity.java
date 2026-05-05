package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class ChromaticTunnelBlockEntity extends BeltTunnelBlockEntity {
    TransportedItemStack stackToProcess;
    float fromPos;
    boolean fromSideProcess;

    public ChromaticTunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract List<ItemStack> tryProcessOnBelt(TransportedItemStack input, boolean simulate);

    public void setStackToProcess(TransportedItemStack stack, boolean side) {
        if (tryProcessOnBelt(stack, true) == null)
            return;
        if (!side)
            stackToProcess = stack;
        else
            stackToProcess = stack.copy();
        fromPos = stack.beltPosition;
        fromSideProcess = side;
        sendData();
        setChanged();
    }

    @Override
    public void tick() {
        super.tick();

        BeltBlockEntity beltBelow = null;
        if (level != null) {
            beltBelow = BeltHelper.getSegmentBE(level, worldPosition.below());
        }

        if (beltBelow == null || beltBelow.getSpeed() == 0)
            return;
        if (stackToProcess == null || stackToProcess.stack == ItemStack.EMPTY)
            return;
        if (level.isClientSide && !isVirtual())
            return;

        if (Math.abs(fromPos - stackToProcess.beltPosition) >= 0.75 || fromSideProcess){
            BlockPos offset = getBlockPos().below().relative(beltBelow.getMovementFacing());
            DirectBeltInputBehaviour sideOutput = BlockEntityBehaviour.get(level, offset, DirectBeltInputBehaviour.TYPE);
            List<ItemStack> results = null;
            if (sideOutput != null && sideOutput.canInsertFromSide(beltBelow.getMovementFacing()) ||
                    sideOutput == null && !BlockHelper.hasBlockSolidSide(level.getBlockState(offset), level, offset, beltBelow.getMovementFacing().getOpposite()))
                results = tryProcessOnBelt(stackToProcess, false);
            if (results != null){
                /*List<TransportedItemStack> collect = results.stream()
                        .map(stack -> {
                            TransportedItemStack copy = stackToProcess.copy();
                            boolean centered = BeltHelper.isItemUpright(stack);
                            copy.stack = stack;
                            copy.locked = true;
                            copy.angle = centered ? 180 : Create.RANDOM.nextInt(360);
                            return copy;
                        })
                        .toList();
                for (TransportedItemStack stack : collect){
                    beltBelow.getInventory().addItem(stack);
                }*/
                if (!results.isEmpty())
                    flap(beltBelow.getMovementFacing(), false);
                for (ItemStack stack : results){
                    if (sideOutput != null){
                        TransportedItemStack stackToOutput = new TransportedItemStack(stack);
                        sideOutput.handleInsertion(stackToOutput, beltBelow.getMovementFacing(), false);
                        stackToOutput.beltPosition = stackToProcess.beltPosition;
                    }
                    else {
                        BeltBlockEntity controllerBE = beltBelow.getControllerBE();
                        if (controllerBE == null)
                            return;
                        float beltMovementSpeed = beltBelow.getDirectionAwareBeltMovementSpeed();
                        float movementSpeed = Math.max(Math.abs(beltMovementSpeed), 1 / 8f);
                        int additionalOffset = beltMovementSpeed > 0 ? 1 : 0;
                        Vec3 outPos = BeltHelper.getVectorForOffset(controllerBE, beltBelow.index + additionalOffset);
                        Vec3 outMotion = Vec3.atLowerCornerOf(beltBelow.getMovementFacing().getNormal())
                                .scale(movementSpeed)
                                .add(0, 1 / 8f, 0);
                        ItemEntity entity = new ItemEntity(level, outPos.x, outPos.y + 6 / 16f, outPos.z, stack);
                        entity.setDeltaMovement(outMotion);
                        entity.setDefaultPickUpDelay();
                        entity.hurtMarked = true;
                        level.addFreshEntity(entity);
                        controllerBE.notifyUpdate();
                    }
                }
                if (level != null) {// Didn't work well so disabled
                    level.addParticle(ParticleTypes.FLASH, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 0, 0, 0);
                    for (int i = 0; i < 20; i++) {
                        Vec3 motion = VecHelper.offsetRandomly(new Vec3(0, 1, 0), level.random, 1);
                        level.addParticle(ParticleTypes.WITCH, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), motion.x, motion.y, motion.z);
                        level.addParticle(ParticleTypes.END_ROD, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), motion.x, motion.y, motion.z);
                    }
                    level.playLocalSound(getBlockPos(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1, false);
                }
                stackToProcess = null;
                fromSideProcess = false;
            }
        }
    }
}
