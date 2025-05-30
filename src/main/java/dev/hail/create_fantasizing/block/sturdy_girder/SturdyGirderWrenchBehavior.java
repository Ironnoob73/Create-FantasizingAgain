package dev.hail.create_fantasizing.block.sturdy_girder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.girder.GirderWrenchBehavior;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SturdyGirderWrenchBehavior {
    @OnlyIn(Dist.CLIENT)
    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !(mc.hitResult instanceof BlockHitResult result))
            return;

        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        Player player = mc.player;
        ItemStack heldItem = player.getMainHandItem();

        if (player.isShiftKeyDown())
            return;

        if (!CFABlocks.STURDY_GIRDER.has(world.getBlockState(pos)))
            return;

        if (!AllItems.WRENCH.isIn(heldItem))
            return;

        Pair<Direction, SturdyGirderWrenchBehavior.Action> dirPair = getDirectionAndAction(result, world, pos);
        if (dirPair == null)
            return;

        Vec3 center = VecHelper.getCenterOf(pos);
        Vec3 edge = center.add(Vec3.atLowerCornerOf(dirPair.getFirst()
                        .getNormal())
                .scale(0.4));
        Direction.Axis[] axes = Arrays.stream(Iterate.axes)
                .filter(axis -> axis != dirPair.getFirst()
                        .getAxis())
                .toArray(Direction.Axis[]::new);

        double normalMultiplier = dirPair.getSecond() == SturdyGirderWrenchBehavior.Action.PAIR ? 4 : 1;
        Vec3 corner1 = edge
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[0], Direction.AxisDirection.POSITIVE)
                                .getNormal())
                        .scale(0.3))
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[1], Direction.AxisDirection.POSITIVE)
                                .getNormal())
                        .scale(0.3))
                .add(Vec3.atLowerCornerOf(dirPair.getFirst()
                                .getNormal())
                        .scale(0.1 * normalMultiplier));

        normalMultiplier = dirPair.getSecond() == SturdyGirderWrenchBehavior.Action.HORIZONTAL ? 9 : 2;
        Vec3 corner2 = edge
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[0], Direction.AxisDirection.NEGATIVE)
                                .getNormal())
                        .scale(0.3))
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[1], Direction.AxisDirection.NEGATIVE)
                                .getNormal())
                        .scale(0.3))
                .add(Vec3.atLowerCornerOf(dirPair.getFirst()
                                .getOpposite()
                                .getNormal())
                        .scale(0.1 * normalMultiplier));

        Outliner.getInstance().showAABB("girderWrench", new AABB(corner1, corner2))
                .lineWidth(1 / 32f)
                .colored(new Color(255, 223, 127));
    }

    @Nullable
    private static Pair<Direction, SturdyGirderWrenchBehavior.Action> getDirectionAndAction(BlockHitResult result, Level world, BlockPos pos) {
        List<Pair<Direction, SturdyGirderWrenchBehavior.Action>> validDirections = getValidDirections(world, pos);

        if (validDirections.isEmpty())
            return null;

        List<Direction> directions = IPlacementHelper.orderedByDistance(pos, result.getLocation(),
                validDirections.stream()
                        .map(Pair::getFirst)
                        .toList());

        if (directions.isEmpty())
            return null;

        Direction dir = directions.get(0);
        return validDirections.stream()
                .filter(pair -> pair.getFirst() == dir)
                .findFirst()
                .orElseGet(() -> Pair.of(dir, SturdyGirderWrenchBehavior.Action.SINGLE));
    }

    public static List<Pair<Direction, SturdyGirderWrenchBehavior.Action>> getValidDirections(BlockGetter level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);

        if (!CFABlocks.STURDY_GIRDER.has(blockState))
            return Collections.emptyList();

        return Arrays.stream(Iterate.directions)
                .<Pair<Direction, SturdyGirderWrenchBehavior.Action>>mapMulti((direction, consumer) -> {
                    BlockState other = level.getBlockState(pos.relative(direction));

                    if (!blockState.getValue(SturdyGirderBlock.X) && !blockState.getValue(SturdyGirderBlock.Z))
                        return;

                    if (direction.getAxis() == Direction.Axis.Y) {
                        if (!CFABlocks.STURDY_GIRDER.has(other)) {
                            if (!blockState.getValue(SturdyGirderBlock.X) ^ !blockState.getValue(SturdyGirderBlock.Z))
                                consumer.accept(Pair.of(direction, SturdyGirderWrenchBehavior.Action.SINGLE));
                            return;
                        }
                        if (blockState.getValue(SturdyGirderBlock.X) == blockState.getValue(SturdyGirderBlock.Z))
                            return;
                        if (other.getValue(SturdyGirderBlock.X) == other.getValue(SturdyGirderBlock.Z))
                            return;
                        consumer.accept(Pair.of(direction, SturdyGirderWrenchBehavior.Action.PAIR));
                    }

                })
                .toList();
    }

    public static boolean handleClick(Level level, BlockPos pos, BlockState state, BlockHitResult result) {
        Pair<Direction, SturdyGirderWrenchBehavior.Action> dirPair = getDirectionAndAction(result, level, pos);
        if (dirPair == null)
            return false;
        if (level.isClientSide)
            return true;
        if (!state.getValue(SturdyGirderBlock.X) && !state.getValue(SturdyGirderBlock.Z))
            return false;

        Direction dir = dirPair.getFirst();

        BlockPos otherPos = pos.relative(dir);
        BlockState other = level.getBlockState(otherPos);

        if (dir == Direction.UP) {
            level.setBlock(pos, postProcess(state.cycle(SturdyGirderBlock.TOP)), 2 | 16);
            if (dirPair.getSecond() == SturdyGirderWrenchBehavior.Action.PAIR && CFABlocks.STURDY_GIRDER.has(other))
                level.setBlock(otherPos, postProcess(other.cycle(SturdyGirderBlock.BOTTOM)), 2 | 16);
            return true;
        }

        if (dir == Direction.DOWN) {
            level.setBlock(pos, postProcess(state.cycle(SturdyGirderBlock.BOTTOM)), 2 | 16);
            if (dirPair.getSecond() == SturdyGirderWrenchBehavior.Action.PAIR && CFABlocks.STURDY_GIRDER.has(other))
                level.setBlock(otherPos, postProcess(other.cycle(SturdyGirderBlock.TOP)), 2 | 16);
            return true;
        }

        return true;
    }

    private static BlockState postProcess(BlockState newState) {
        if (newState.getValue(SturdyGirderBlock.TOP) && newState.getValue(SturdyGirderBlock.BOTTOM))
            return newState;
        if (newState.getValue(SturdyGirderBlock.AXIS) != Direction.Axis.Y)
            return newState;
        return newState.setValue(SturdyGirderBlock.AXIS, newState.getValue(SturdyGirderBlock.X) ? Direction.Axis.X : Direction.Axis.Z);
    }

    private enum Action {
        SINGLE, PAIR, HORIZONTAL
    }
}
