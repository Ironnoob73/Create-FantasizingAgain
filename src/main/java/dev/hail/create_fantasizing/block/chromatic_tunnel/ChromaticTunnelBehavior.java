package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChromaticTunnelBehavior extends BeltProcessingBehaviour {

    public boolean doParticles;
    
    public ChromaticTunnelBehavior(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        if (clientPacket) {
            doParticles = compound.getBoolean("DoParticle");
            if (doParticles)
                spawnParticles();
        }
    }
    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        if (clientPacket) {
            compound.putBoolean("DoParticle", doParticles);
            doParticles = false;
        }
    }

    protected void spawnParticles() {
        Level level = getWorld();
        BlockPos worldPosition = getPos();
        if (level != null && level.isClientSide) {
            level.addParticle(ParticleTypes.FLASH, worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, 0, 0, 0);
            for (int i = 0; i < 20; i++) {
                Vec3 motion = VecHelper.offsetRandomly(new Vec3(0, 1, 0), level.random, 1);
                level.addParticle(ParticleTypes.WITCH, worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, motion.x, motion.y, motion.z);
                level.addParticle(ParticleTypes.END_ROD, worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, motion.x, motion.y, motion.z);
            }
            level.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1, false);
        }
    }
}
