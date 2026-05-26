package dev.hail.create_fantasizing.item;

import com.mojang.blaze3d.shaders.Effect;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SculkEngineFrameItem extends Item {
    public SculkEngineFrameItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack heldItem, Player player, LivingEntity entity,
                                                           InteractionHand hand) {
        if (!(entity instanceof Warden))
            return InteractionResult.PASS;

        Level world = player.level();
        spawnCaptureEffects(world, entity.position());
        if (world.isClientSide)
            return InteractionResult.FAIL;

        giveEngineItemTo(player, heldItem, hand);
        entity.discard();
        return InteractionResult.FAIL;
    }

    private void spawnCaptureEffects(Level world, Vec3 vec) {
        if (world.isClientSide) {
            for (int i = 0; i < 75; i++) {
                Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, world.random, .5f);
                world.addParticle(ParticleTypes.SCULK_SOUL, vec.x, vec.y + 1, vec.z, motion.x, motion.y, motion.z);
                Vec3 circle = motion.multiply(1, 1, 1)
                        .normalize()
                        .scale(.5f);
                world.addParticle(ParticleTypes.SCULK_CHARGE_POP, circle.x, vec.y + 1, circle.z, 0, -5, 0);
            }
            return;
        }

        BlockPos soundPos = BlockPos.containing(vec);
        world.playSound(null, soundPos, SoundEvents.WARDEN_DEATH, SoundSource.HOSTILE, .25f, .75f);
        world.playSound(null, soundPos, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, .5f, .75f);
    }
    protected void giveEngineItemTo(Player player, ItemStack heldItem, InteractionHand hand) {
        ItemStack filled = CFABlocks.SCULK_ENGINE.asStack();
        if (!player.isCreative())
            heldItem.shrink(1);
        if (heldItem.isEmpty()) {
            player.setItemInHand(hand, filled);
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 3, false, false));
        player.getInventory()
                .placeItemBackInInventory(filled);
    }
}
