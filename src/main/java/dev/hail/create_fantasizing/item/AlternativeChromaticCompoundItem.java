package dev.hail.create_fantasizing.item;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlternativeChromaticCompoundItem extends Item {
    public AlternativeChromaticCompoundItem(Properties properties) {
        super(properties);
    }

    public int getLight(ItemStack stack) {
        return stack.getOrDefault(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT, 0);
    }
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getLight(stack) != 0;
    }
    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (getLight(stack) + AllConfigs.server().recipes.lightSourceCountForRefinedRadiance.get())
                / (AllConfigs.server().recipes.lightSourceCountForRefinedRadiance.get() * 2));
    }
    @Override
    public int getBarColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level world = entity.level();
        ItemStack itemStack = entity.getItem();
        Vec3 positionVec = entity.position();
        CRecipes config = AllConfigs.server().recipes;

        if (world.isClientSide) {
            int light = getLight(itemStack);
            if (world.random.nextInt(config.lightSourceCountForRefinedRadiance.get() + 20) < light) {
                Vec3 start = VecHelper.offsetRandomly(positionVec, world.random, 3);
                Vec3 motion = positionVec.subtract(start)
                        .normalize()
                        .scale(.2f);
                world.addParticle(ParticleTypes.END_ROD, start.x, start.y, start.z, motion.x, motion.y, motion.z);
            }
            return false;
        }

        double y = entity.getY();
        double yMotion = entity.getDeltaMovement().y;
        int minHeight = world.getMinBuildHeight();
        CompoundTag data = entity.getPersistentData();

        // Convert
        if (getLight(itemStack) >= config.lightSourceCountForRefinedRadiance.get()) {
            ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
            ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
            world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1);
            newEntity.getPersistentData()
                    .putBoolean("JustCreated", true);
            itemStack.remove(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT);
            world.addFreshEntity(newEntity);

            stack.split(1);
            entity.setItem(stack);
            if (stack.isEmpty())
                entity.discard();
            return false;
        }
        if (getLight(itemStack) <= - config.lightSourceCountForRefinedRadiance.get()) {
            ItemStack newStack = AllItems.SHADOW_STEEL.asStack();
            ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
            world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1);
            newEntity.getPersistentData()
                    .putBoolean("JustCreated", true);
            itemStack.remove(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT);
            world.addFreshEntity(newEntity);

            stack.split(1);
            entity.setItem(stack);
            if (stack.isEmpty())
                entity.discard();
            return false;
        }

        RandomSource r = world.random;
        int range = 3;
        float rate = 1 / 2f;
        BlockPos randomOffset = BlockPos.containing(VecHelper.offsetRandomly(positionVec, r, range));
        BlockState randomState = world.getBlockState(randomOffset);
        TransportedItemStackHandlerBehaviour behaviour =
                BlockEntityBehaviour.get(world, randomOffset, TransportedItemStackHandlerBehaviour.TYPE);

        // Convert to Shadow steel if in void
        if (config.enableShadowSteelRecipe.get()) {
            if (y < minHeight && y - yMotion < -10 + minHeight){
                world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1);
                ItemStack newStack = AllItems.SHADOW_STEEL.asStack();
                newStack.setCount(stack.getCount());
                data.putBoolean("JustCreated", true);
                entity.setItem(newStack);
            } else if (randomOffset.getY() < minHeight && r.nextFloat() > 0.75 && behaviour == null) {
                doCollectingChange(stack, entity, world, -1);
                world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.BLOCKS, 1, 1);
            }
        }

        if (!config.enableRefinedRadianceRecipe.get())
            return false;

        // Is inside beacon beam?
        boolean isOverBeacon = false;
        int entityX = Mth.floor(entity.getX());
        int entityZ = Mth.floor(entity.getZ());
        int localWorldHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, entityX, entityZ);

        BlockPos.MutableBlockPos testPos =
                new BlockPos.MutableBlockPos(entityX, Math.min(Mth.floor(entity.getY()), localWorldHeight), entityZ);

        while (testPos.getY() > minHeight) {
            testPos.move(Direction.DOWN);
            BlockState state = world.getBlockState(testPos);
            if (state.getLightBlock(world, testPos) >= 15 && state.getBlock() != Blocks.BEDROCK)
                break;
            if (state.getBlock() == Blocks.BEACON) {
                BlockEntity be = world.getBlockEntity(testPos);

                if (!(be instanceof BeaconBlockEntity bte))
                    break;

                if (!bte.getBeamSections().isEmpty())
                    isOverBeacon = true;

                break;
            }
        }

        if (isOverBeacon) {
            world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1, 1);
            ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
            newStack.setCount(stack.getCount());
            data.putBoolean("JustCreated", true);
            entity.setItem(newStack);
            return false;
        }

        // Find a placed light source
        if (behaviour == null && r.nextFloat() < rate) {
            if (checkLight(stack, entity, world, positionVec, randomOffset, randomState))
                world.playSound(entity, BlockPos.containing(entity.position()), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.BLOCKS, 1, 1);
            return false;
        }

        return false;
    }

    public boolean checkLight(ItemStack stack, ItemEntity entity, Level world, Vec3 positionVec,
                              BlockPos randomOffset, BlockState state) {
        if (state.getLightEmission(world, randomOffset) == 0)
            return false;
        if (state.getBlock() == Blocks.BEACON)
            return false;

        ClipContext context = new ClipContext(positionVec.add(new Vec3(0, 0.5, 0)), VecHelper.getCenterOf(randomOffset),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        if (!randomOffset.equals(world.clip(context)
                .getBlockPos()))
            return false;

        doCollectingChange(stack, entity, world, 1);
        return true;
    }

    public void doCollectingChange(ItemStack stack, ItemEntity entity, Level world, int valueChanged){
        ItemStack newStack = stack.split(1);
        newStack.set(AllDataComponents.CHROMATIC_COMPOUND_COLLECTING_LIGHT, getLight(newStack) + valueChanged);
        ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
        newEntity.setDeltaMovement(entity.getDeltaMovement());
        newEntity.setDefaultPickUpDelay();
        world.addFreshEntity(newEntity);
        entity.lifespan = 6000;
        if (stack.isEmpty())
            entity.discard();
    }
}
