package dev.hail.create_fantasizing.item.block_placer;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.foundation.gui.AllIcons;
import dev.hail.create_fantasizing.data.CFATags;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public enum BlockPlacerTools implements StringRepresentable {

    Fill(AllIcons.I_FILL),
    Place(AllIcons.I_PLACE),
    Replace(AllIcons.I_REPLACE),
    Clear(AllIcons.I_CLEAR),
    Overlay(AllIcons.I_OVERLAY),
    ;
    public static final TagKey<Block> UNBREAKABLE_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.withDefaultNamespace("wither_immune"));
    public static final Codec<BlockPlacerTools> CODEC = StringRepresentable.fromValues(BlockPlacerTools::values);
    public static final StreamCodec<ByteBuf, BlockPlacerTools> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(BlockPlacerTools.class);
    public final String translationKey;
    public final AllIcons icon;

    BlockPlacerTools(AllIcons icon) {
        this.translationKey = Lang.asId(name());
        this.icon = icon;
    }
    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

    public boolean requiresSelectedBlock() {
        return this != Clear;
    }

    public void run(Level world, List<BlockPos> targetPositions, BlockState paintedState, CompoundTag data,
                    Player player, ItemStack stack, InteractionHand hand, PlacementPatterns patterns) {
        switch (this) {
            case Clear -> {
                for (var p : targetPositions)
                    zapperFunction(world, Blocks.AIR.defaultBlockState(), p, world.getBlockState(p), stack, player, hand, data, patterns);
            }
            case Fill -> {
                for (var p : targetPositions) {
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace)) continue;
                    zapperFunction(world, paintedState, p, toReplace, stack, player, hand, data, patterns);
                }
            }
            case Overlay -> {
                for (var p : targetPositions) {
                    BlockState toOverlay = world.getBlockState(p);
                    if (isReplaceable(toOverlay) || (toOverlay == paintedState)) continue;
                    p = p.above();
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace)) continue;
                    zapperFunction(world, paintedState, p, toReplace, stack, player, hand, data, patterns);
                }
            }
            case Place -> {
                for (var p : targetPositions)
                    zapperFunction(world, paintedState, p, world.getBlockState(p), stack, player, hand, data, patterns);
            }
            case Replace -> {
                for (var p : targetPositions) {
                    BlockState toReplace = world.getBlockState(p);
                    if (isReplaceable(toReplace)) continue;
                    zapperFunction(world, paintedState, p, toReplace, stack, player, hand, data, patterns);
                }
            }
        }
    }

    public int runCalculate(Level world, List<BlockPos> targetPositions, BlockState paintedState) {
        int size = 0;
        switch (this) {
            case Clear -> {
                return 999999;
            }
            case Place -> {
                for (var p : targetPositions)
                    if (meetRequirements(world, paintedState, p, world.getBlockState(p)) == 0)
                        size++;
            }
            case Fill -> {
                for (var p : targetPositions) {
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace)) continue;
                    if (meetRequirements(world, paintedState, p, toReplace) == 0)
                        size++;
                }
            }
            case Overlay -> {
                for (var p : targetPositions) {
                    BlockState toOverlay = world.getBlockState(p);
                    if (isReplaceable(toOverlay) || (toOverlay == paintedState)) continue;
                    p = p.above();
                    BlockState toReplace = world.getBlockState(p);
                    if (!isReplaceable(toReplace)) continue;
                    if (meetRequirements(world, paintedState, p, toReplace) == 0)
                        size++;
                }
            }
            case Replace -> {
                for (var p : targetPositions) {
                    BlockState toReplace = world.getBlockState(p);
                    if (isReplaceable(toReplace)) continue;
                    if (meetRequirements(world, paintedState, p, toReplace) == 0)
                        size++;
                }
            }
        }
        return size;
    }

    public static boolean isReplaceable(BlockState toReplace) {
        return toReplace.canBeReplaced();
    }

    public static void zapperFunction(Level pLevel, BlockState paintState, BlockPos replacePos, BlockState replaceState,
                                      ItemStack stack, Player player, InteractionHand hand, CompoundTag data, PlacementPatterns patterns) {
        Random r = new Random();
        if (switch (patterns) {
            case Chance25 -> r.nextBoolean() || r.nextBoolean();
            case Chance50 -> r.nextBoolean();
            case Chance75 -> r.nextBoolean() && r.nextBoolean();
            default -> false;
        }) return;
        if (meetRequirements(pLevel, paintState, replacePos, replaceState) != 0) return;
        Block paintBlock = paintState.getBlock();
        boolean creative = player.isCreative();
        if (!creative && !paintState.isAir() && !hasItemInInventory(paintBlock, player)) return;
        if (!creative && !paintState.isAir()) calculateItemsInInventory(paintBlock, false, player,
                stack.getEnchantmentLevel(pLevel.holderOrThrow(Enchantments.INFINITY)) >= 1);

        dropResources(replaceState, pLevel, replacePos, replaceState.hasBlockEntity() ? pLevel.getBlockEntity(replacePos) : null, player, stack);
        paintBlock.setPlacedBy(pLevel, replacePos, paintState, player, stack);
        pLevel.gameEvent(GameEvent.BLOCK_PLACE, replacePos, GameEvent.Context.of(player, paintState));
        pLevel.setBlock(replacePos, paintState, Block.UPDATE_CLIENTS);
        pLevel.setBlockAndUpdate(replacePos, paintState);
        ZapperItem.setBlockEntityData(pLevel, replacePos, paintState, data, player);
        if (!creative) stack.hurtAndBreak(2, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    public static boolean hasItemInInventory(Block paintBlock, Player player) {
        var inv = player.getInventory();
        int size = inv.getContainerSize();
        for (int slot = 0; slot < size; slot++) {
            var item = inv.getItem(slot);
            if (item.isEmpty()) continue;
            if (item.is(paintBlock.asItem())) return true;
        }
        return false;
    }

    public static int calculateItemsInInventory(Block paintBlock, boolean calculate, Player player, boolean generator) {
        int amount = 0;
        var inv = player.getInventory();
        int size = inv.getContainerSize();
        if (paintBlock.defaultBlockState().is(CFATags.RENEWABLE_BLOCKS) && generator) amount = size * 128;
        else for (int slot = 0; slot < size; slot++) {
            var item = inv.getItem(slot);
            if (item.isEmpty())
                continue;
            if (item.is(paintBlock.asItem())) {
                if (!calculate) {
                    item.shrink(1);
                    return 1;
                } else amount = amount + item.getCount();
            }
        }
        return amount;
    }

    public static boolean blacklist(BlockState toBlacklist) {
        return toBlacklist.is(AllTags.AllBlockTags.NON_MOVABLE.tag) || AllTags.AllBlockTags.SAFE_NBT.matches(toBlacklist) || toBlacklist.is(UNBREAKABLE_TAG);
    }
    public static int meetRequirements(Level pLevel, BlockState paintState, BlockPos replacePos, BlockState replaceState) {
        if (paintState == replaceState)
            return 8;
        if (replaceState.getBlock().defaultDestroyTime() > 50 && !replaceState.isAir())
            return 6;
        if (blacklist(replaceState))
            return 4;
        if (!paintState.canSurvive(pLevel, replacePos))
            return 4;
        return pLevel.getWorldBorder().isWithinBounds(replacePos) ? 0 : 4;
    }

    public static void dropResources(BlockState pState, Level pLevel, BlockPos pPos, @Nullable BlockEntity pBlockEntity, @Nullable Entity pEntity, ItemStack pTool) {
        if (pLevel instanceof ServerLevel serverLevel) {
            BlockPos dropPos;
            if (pEntity != null) dropPos = pEntity.getOnPos().above();
            else dropPos = pPos;

            Block.getDrops(pState, serverLevel, pPos, pBlockEntity, pEntity, pTool).forEach((p_49944_) -> popResource(pLevel, dropPos, p_49944_));
            pState.spawnAfterBreak(serverLevel, pPos, pTool, false);

            int exp = pState.getExpDrop(serverLevel, pPos, pBlockEntity, pEntity, pTool);
            if (exp > 0) pState.getBlock().popExperience(serverLevel, dropPos, exp);
        }
    }

    static int tick = 0;
    public static void itemTransferTick(Level pLevel, Player player) {
        tick = ++tick % 2;
        if (tick==1) {
            Vec3 center = getCenterPos(player);
            AABB bb = new AABB(center, center).inflate(64);

            for (ItemEntity entity : pLevel.getEntitiesOfClass(ItemEntity.class, bb)) {
                if (entity == null || entity.isRemoved() || !entity.getTags().contains("sendToNearestPlayer")) continue;
                boolean fast = entity.getTags().contains("sendToNearestPlayerInstant");
                Vec3 diff = getCenterPos(entity).subtract(center);
                double distance = diff.length();
                int range = fast ? 64 : 32;
                if (distance > range) continue;

                Vec3 pushVec = diff.normalize().scale((range - distance) * -1);
                float forceFactor = 1 / 128f;
                Vec3 force = pushVec.scale(forceFactor * (fast ? 1.0 : 0.5));

                entity.push(force.x, force.y, force.z);
                entity.fallDistance = 0;
                entity.hurtMarked = true;

                float limit = fast ? 4.0f : 2.0f;
                Vec3 currentMovement = entity.getDeltaMovement();
                if (currentMovement.length() > limit) {
                    Vec3 limitedMovement = currentMovement.normalize().scale(limit);
                    entity.setDeltaMovement(limitedMovement);
                }
            }
        }
    }
    public static Vec3 getCenterPos(Entity entity) {
        AABB boundingBox = entity.getBoundingBox();

        double centerX = (boundingBox.minX + boundingBox.maxX) / 2.0;
        double centerY = (boundingBox.minY + boundingBox.maxY) / 2.0;
        double centerZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0;

        return new Vec3(centerX, centerY, centerZ);
    }

    public static void popResource(Level pLevel, BlockPos pPos, ItemStack pStack) {
        double d0 = (double) EntityType.ITEM.getHeight() / 2.0D;
        double d1 = (double)pPos.getX() + 0.5D + Mth.nextDouble(pLevel.random, -0.25D, 0.25D);
        double d2 = (double)pPos.getY() + 0.5D + Mth.nextDouble(pLevel.random, -0.25D, 0.25D) - d0;
        double d3 = (double)pPos.getZ() + 0.5D + Mth.nextDouble(pLevel.random, -0.25D, 0.25D);
        ItemEntity item = new ItemEntity(pLevel, d1, d2, d3, pStack);
        item.addTag("sendToNearestPlayerInstant");
        popResource(pLevel, () -> item, pStack);
    }

    private static void popResource(Level pLevel, Supplier<ItemEntity> pItemEntitySupplier, ItemStack pStack) {
        if (!pLevel.isClientSide && !pStack.isEmpty() && pLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !pLevel.restoringBlockSnapshots) {
            ItemEntity itementity = pItemEntitySupplier.get();
            itementity.setDefaultPickUpDelay();
            pLevel.addFreshEntity(itementity);
        }
    }

}