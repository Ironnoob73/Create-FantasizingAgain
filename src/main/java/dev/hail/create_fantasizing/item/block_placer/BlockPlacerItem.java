package dev.hail.create_fantasizing.item.block_placer;

import com.google.common.base.Predicates;
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
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

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
    static final String SHAPER_BRUSH_PARAMS = "BrushParams"; //BLOCK POS
    static final String SHAPER_BRUSH = "Brush"; //ENUM
    static final String SHAPER_TOOL = "Tool"; //ENUM
    static final String SHAPER_PLACEMENT = "Placement"; //ENUM
    public BlockPlacerItem(Properties properties) {
        super(properties);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new BlockPlacerItemRenderer()));
    }
    public boolean isEnchantable(@NotNull ItemStack itemstack) {
        return true;
    }
    @Override
    @SuppressWarnings("deprecation")
    public int getEnchantmentValue() {
        return 1;
    }
    @Override
    public Component validateUsage(ItemStack item) {
        if (!item.getOrCreateTag().contains(SHAPER_BRUSH_PARAMS))
            return CreateLang.translateDirect("terrainzapper.shiftRightClickToSet");
        return super.validateUsage(item);
    }
    @Override
    protected boolean canActivateWithoutSelectedBlock(ItemStack stack) {
        BlockPlacerTools tool = NBTHelper.readEnum(stack.getOrCreateTag(), SHAPER_TOOL, BlockPlacerTools.class);
        return !tool.requiresSelectedBlock();
    }
    @Override
    protected boolean activate(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace, CompoundTag data) {
        return false;
    }
    public float activate(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace, CompoundTag data, InteractionHand hand) {
        BlockPos targetPos = raytrace.getBlockPos();
        List<BlockPos> affectedPositions = new ArrayList<>();

        CompoundTag tag = stack.getOrCreateTag();
        BPBrush brush = NBTHelper.readEnum(tag, SHAPER_BRUSH, BlockPlacerBrushes.class).get();
        BlockPos params = NbtUtils.readBlockPos(tag.getCompound(SHAPER_BRUSH_PARAMS));
        float multiplier = sizeMultiplier(params, brush);
        PlacementOptions option = NBTHelper.readEnum(tag, SHAPER_PLACEMENT, PlacementOptions.class);
        BlockPlacerTools tool = NBTHelper.readEnum(tag, SHAPER_TOOL, BlockPlacerTools.class);
        brush.set(params.getX(), params.getY(), params.getZ());
        targetPos = targetPos.offset(brush.getOffset(player.getLookAngle(), raytrace.getDirection(), option));
        brush.addToGlobalPositions(world, targetPos, raytrace.getDirection(), affectedPositions, tool);
        brush.redirectTool(tool).run(world, affectedPositions, stateToUse, data, player, stack, hand, applyPattern(affectedPositions, stack));
        return multiplier;
    }
    public int activateCalculation(Level world, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace) {
        BlockPos targetPos = raytrace.getBlockPos();
        List<BlockPos> affectedPositions = new ArrayList<>();

        CompoundTag tag = stack.getOrCreateTag();
        BPBrush brush = NBTHelper.readEnum(tag, SHAPER_BRUSH, BlockPlacerBrushes.class).get();
        BlockPos params = NbtUtils.readBlockPos(tag.getCompound(SHAPER_BRUSH_PARAMS));
        PlacementOptions option = NBTHelper.readEnum(tag, SHAPER_PLACEMENT, PlacementOptions.class);
        BlockPlacerTools tool = NBTHelper.readEnum(tag, SHAPER_TOOL, BlockPlacerTools.class);

        if (params != null) {
            brush.set(params.getX(), params.getY(), params.getZ());
        }
        targetPos = targetPos.offset(brush.getOffset(player.getLookAngle(), raytrace.getDirection(), option));
        brush.addToGlobalPositions(world, targetPos, raytrace.getDirection(), affectedPositions, tool);
        return brush.redirectTool(tool).runCalculate(world, affectedPositions, stateToUse);
    }

    public static PlacementPatterns applyPattern(List<BlockPos> blocksIn, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        PlacementPatterns pattern = !tag.contains("Pattern") ? Solid : PlacementPatterns.valueOf(tag.getString("Pattern"));
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
        CompoundTag nbt = pStack.getOrCreateTag();
        if (pEntity instanceof Player player) {
            BlockState stateToUse = Blocks.AIR.defaultBlockState();
            if (nbt.contains("BlockUsed")) stateToUse = NbtUtils.readBlockState(pLevel.holderLookup(Registries.BLOCK), nbt.getCompound("BlockUsed"));
            stateToUse = BlockHelper.setZeroAge(stateToUse);
            Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
            Vec3 range = player.getLookAngle().scale(getZappingRange(pStack));
            BlockHitResult raytrace = pLevel.clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

            int invAmount;
            if (stateToUse != null) {
                invAmount = BlockPlacerTools.calculateItemsInInventory(stateToUse.getBlock(), true, player);
                int selSize = activateCalculation(pLevel, player, pStack, stateToUse, raytrace);
                if (!nbt.contains("block_amount") || (nbt.getInt("block_amount") != invAmount)) nbt.putInt("block_amount", invAmount);
                if (!nbt.contains("place_size") || (nbt.getInt("place_size") != selSize)) nbt.putInt("place_size", selSize);
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

        CompoundTag nbt = item.getOrCreateTag();
        BlockState stateToUse = Blocks.AIR.defaultBlockState();
        if (nbt.contains("BlockUsed")) stateToUse = NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK), nbt.getCompound("BlockUsed"));
        stateToUse = BlockHelper.setZeroAge(stateToUse);
        CompoundTag data = null;
        if (nbt.contains("BlockData", Tag.TAG_COMPOUND)) data = nbt.getCompound("BlockData");
        Vec3 start = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 range = player.getLookAngle().scale(getZappingRange(item));
        BlockHitResult raytrace = level.clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Vec3 barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, mainHand, new Vec3(.35f, -0.1f, 1));
        BlockPos lookingPos = raytrace.getBlockPos();

        int amount = nbt.getInt("Amount");
        int size = nbt.getInt("Size");
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
                int cooldown = (int) (multiplier * getCooldownDelay(item));
                ShootableGadgetItemMethods.applyCooldown(player, item, hand, this::isZapper, Math.max(cooldown, 5));
                ShootableGadgetItemMethods.sendPackets(player, b -> new ZapperBeamPacket(barrelPos, raytrace.getLocation(), hand, b));
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
        ZapperItem.configureSettings(stack, pattern);
        CompoundTag nbt = stack.getOrCreateTag();
        NBTHelper.writeEnum(nbt, SHAPER_BRUSH, brush);
        nbt.put(SHAPER_BRUSH_PARAMS, NbtUtils.writeBlockPos(new BlockPos(brushParamX, brushParamY, brushParamZ)));
        NBTHelper.writeEnum(nbt, SHAPER_TOOL, tool);
        NBTHelper.writeEnum(nbt, SHAPER_PLACEMENT, placement);
    }
}
