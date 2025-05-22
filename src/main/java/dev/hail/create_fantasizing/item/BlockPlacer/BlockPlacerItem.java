package dev.hail.create_fantasizing.item.BlockPlacer;

import com.google.common.base.Predicates;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.content.equipment.zapper.ZapperBeamPacket;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.hail.create_fantasizing.data.CFADataComponents;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.simibubi.create.content.equipment.zapper.PlacementPatterns.Solid;

/**
 * Special thanks to @LopyLuna!
 * Most codes here are copied from: <a href="https://github.com/LopyLuna/Block-n-Zapping">...</a>
 */

@ParametersAreNonnullByDefault
public class BlockPlacerItem extends ZapperItem {
    public BlockPlacerItem(Properties properties) {
        super(properties);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("removal")
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new BlockPlacerItemRenderer()));
    }
    @Override
    public Component validateUsage(ItemStack item) {
        if (!item.has(AllDataComponents.SHAPER_BRUSH_PARAMS))
            return CreateLang.translateDirect("terrainzapper.shiftRightClickToSet");
        return super.validateUsage(item);
    }
    @Override
    protected boolean canActivateWithoutSelectedBlock(ItemStack stack) {
        BlockPlacerTools tool = stack.getOrDefault(CFADataComponents.SHAPER_TOOL, BlockPlacerTools.Fill);
        return !tool.requiresSelectedBlock();
    }
    @Override
    protected boolean activate(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace, CompoundTag data) {
        return false;
    }
    public float activate(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace, CompoundTag data, InteractionHand hand) {
        BlockPos targetPos = raytrace.getBlockPos();
        List<BlockPos> affectedPositions = new ArrayList<>();

        BPBrush brush = stack.getOrDefault(CFADataComponents.SHAPER_BRUSH, BlockPlacerBrushes.Cuboid).get();
        BlockPos params = stack.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
        float multiplier = sizeMultiplier(params, brush);
        PlacementOptions option = stack.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, PlacementOptions.Merged);
        BlockPlacerTools tool = stack.getOrDefault(CFADataComponents.SHAPER_TOOL, BlockPlacerTools.Fill);
        brush.set(params.getX(), params.getY(), params.getZ());
        targetPos = targetPos.offset(brush.getOffset(player.getLookAngle(), raytrace.getDirection(), option));
        brush.addToGlobalPositions(world, targetPos, raytrace.getDirection(), affectedPositions, tool);
        brush.redirectTool(tool).run(world, affectedPositions, stateToUse, data, player, stack, hand, applyPattern(affectedPositions, stack));
        return multiplier;
    }
    public int activateCalculation(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace) {
        BlockPos targetPos = raytrace.getBlockPos();
        List<BlockPos> affectedPositions = new ArrayList<>();

        BPBrush brush = stack.getOrDefault(CFADataComponents.SHAPER_BRUSH, BlockPlacerBrushes.Cuboid).get();
        BlockPos params = stack.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
        PlacementOptions option = stack.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, PlacementOptions.Merged);
        BlockPlacerTools tool = stack.getOrDefault(CFADataComponents.SHAPER_TOOL, BlockPlacerTools.Fill);

        if (params != null) {
            brush.set(params.getX(), params.getY(), params.getZ());
        }
        targetPos = targetPos.offset(brush.getOffset(player.getLookAngle(), raytrace.getDirection(), option));
        brush.addToGlobalPositions(world, targetPos, raytrace.getDirection(), affectedPositions, tool);
        return brush.redirectTool(tool).runCalculate(world, affectedPositions, stateToUse);
    }

    public static PlacementPatterns applyPattern(List<BlockPos> blocksIn, ItemStack stack) {
        PlacementPatterns pattern = !stack.has(AllDataComponents.PLACEMENT_PATTERN) ? Solid : stack.get(AllDataComponents.PLACEMENT_PATTERN);
        Predicate<BlockPos> filter = Predicates.alwaysFalse();

        switch (pattern) {
            case Checkered -> filter = pos -> (pos.getX() + pos.getY() + pos.getZ()) % 2 == 0;
            case InverseCheckered -> filter = pos -> (pos.getX() + pos.getY() + pos.getZ()) % 2 != 0;
            default -> {
            }
        }


        blocksIn.removeIf(filter);
        return pattern;
    }
    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity instanceof Player player) {
            BlockState stateToUse = Blocks.AIR.defaultBlockState();
            if (pStack.has(AllDataComponents.SHAPER_BLOCK_USED)) stateToUse = pStack.get(AllDataComponents.SHAPER_BLOCK_USED);
            stateToUse = BlockHelper.setZeroAge(stateToUse);
            Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 range = player.getLookAngle().scale(getZappingRange(pStack));
            BlockHitResult raytrace = pLevel.clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

            int invAmount;
            if (stateToUse != null) {
                invAmount = BlockPlacerTools.calculateItemsInInventory(stateToUse.getBlock(), true, player);
                int selSize = activateCalculation(pLevel, player, pStack, stateToUse, raytrace);
                if (!pStack.has(CFADataComponents.BLOCK_AMOUNT) || (pStack.get(CFADataComponents.BLOCK_AMOUNT) != invAmount))
                    pStack.set(CFADataComponents.BLOCK_AMOUNT, invAmount);
                if (!pStack.has(CFADataComponents.PLACE_SIZE) || (pStack.get(CFADataComponents.PLACE_SIZE) != selSize))
                    pStack.set(CFADataComponents.PLACE_SIZE, selSize);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        boolean mainHand = hand == InteractionHand.MAIN_HAND;

        // Shift -> Open GUI
        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> openHandgunGUI(item, hand));
                player.getCooldowns()
                        .addCooldown(item.getItem(), 10);
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, item);
        }

        BlockState stateToUse = Blocks.AIR.defaultBlockState();
        if (item.has(AllDataComponents.SHAPER_BLOCK_USED)) stateToUse = item.get(AllDataComponents.SHAPER_BLOCK_USED);
        stateToUse = BlockHelper.setZeroAge(stateToUse);
        CompoundTag data = null;
        if (item.has(AllDataComponents.SHAPER_BLOCK_DATA)) data = item.get(AllDataComponents.SHAPER_BLOCK_DATA);
        Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 range = player.getLookAngle().scale(getZappingRange(item));
        BlockHitResult raytrace = level.clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, mainHand, new Vec3(.35f, -0.1f, 1));
        BlockPos lookingPos = raytrace.getBlockPos();

        int amount = item.has(CFADataComponents.BLOCK_AMOUNT) ? item.get(CFADataComponents.BLOCK_AMOUNT) : 0;
        int size = item.has(CFADataComponents.PLACE_SIZE) ? item.get(CFADataComponents.PLACE_SIZE) : 0;
        boolean items = ((amount >= size || player.isCreative()) && size != 0) || (size == 999999);
        boolean lookingAtBlock = level.getWorldBorder().isWithinBounds(lookingPos) && raytrace.getType() != HitResult.Type.MISS;
        if (level.isClientSide) {
            if (!player.isShiftKeyDown() && (!items || !lookingAtBlock)) {
                if (size!=0 && size!=999999 && lookingAtBlock) player.displayClientMessage(Component.translatable("item.create_fantasizing.block_placer.not_enough_blocks").append(" "+amount+"/"+size).withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, player, player.blockPosition());
                return new InteractionResultHolder<>(InteractionResult.FAIL, item);
            }
            CreateClient.ZAPPER_RENDER_HANDLER.dontAnimateItem(hand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, item);
        } else {
            if (!player.isShiftKeyDown() && (!items || !lookingAtBlock)) {
                player.getCooldowns().addCooldown(item.getItem(), 10);
                return new InteractionResultHolder<>(InteractionResult.FAIL, item);
            } else if (!player.isShiftKeyDown() && items && lookingAtBlock) {
                float multiplier = activate(level, player, item, stateToUse, raytrace, data, hand);
                int cooldown = (int) (multiplier * getCooldownDelay(item) * size);
                ShootableGadgetItemMethods.applyCooldown(player, item, hand, this::isZapper, Math.max(cooldown, 5));
                ShootableGadgetItemMethods.sendPackets(player, b -> new ZapperBeamPacket(barrelPos, hand, b, raytrace.getLocation()));
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, item);
            }  else return super.use(level, player, hand);
        }
    }
    public float sizeMultiplier(BlockPos params, BPBrush brush) {
        float size = 16;
        float radius = 6;
        float radiusSize = (int) (radius * 1.25);
        float max = 1;
        if (brush instanceof BPCuboidBrush) max = size;
        if (brush instanceof BPSphereBrush) max = radiusSize;
        if (brush instanceof BPCylinderBrush) max = radius;
        if (brush instanceof BPDynamicBrush) max = radiusSize;
        float x = params.getX();
        float y = params.getY();
        float z = params.getZ();
        return (x*y*z) / (max*max*max);
    }
    @Override
    @OnlyIn(value = Dist.CLIENT)
    protected void openHandgunGUI(ItemStack item, InteractionHand hand) {
        ScreenOpener.open(new BlockPlacerScreen(item, hand));
    }
    @Override
    protected int getCooldownDelay(ItemStack item) {
        return 2;
    }
    @Override
    protected int getZappingRange(ItemStack stack) {
        return 128;
    }
    public static void configureSettings(ItemStack stack, PlacementPatterns pattern, BlockPlacerBrushes brush,
                                         int brushParamX, int brushParamY, int brushParamZ, BlockPlacerTools tool, PlacementOptions placement) {
        stack.set(AllDataComponents.PLACEMENT_PATTERN, pattern);
        stack.set(CFADataComponents.SHAPER_BRUSH, brush);
        stack.set(AllDataComponents.SHAPER_BRUSH_PARAMS, new BlockPos(brushParamX, brushParamY, brushParamZ));
        stack.set(CFADataComponents.SHAPER_TOOL, tool);
        stack.set(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, placement);
    }
}
