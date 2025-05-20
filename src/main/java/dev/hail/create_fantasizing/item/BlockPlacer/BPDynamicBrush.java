package dev.hail.create_fantasizing.item.BlockPlacer;

import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BPDynamicBrush extends BPBrush {

    public static final int MAX_RADIUS = 10;

    private boolean surface;

    public BPDynamicBrush(boolean surface) {
        super(1);
        this.surface = surface;
    }

    Component getParamLabel(int paramIndex) {
        return CreateLang.translateDirect("generic.range");
    }

    @Override
    public BlockPlacerTools[] getSupportedTools() {
        return surface ? new BlockPlacerTools[] { BlockPlacerTools.Overlay, BlockPlacerTools.Replace, BlockPlacerTools.Clear }
                : new BlockPlacerTools[] { BlockPlacerTools.Replace, BlockPlacerTools.Clear };
    }

    @Override
    public boolean hasPlacementOptions() {
        return false;
    }

    @Override
    public boolean hasConnectivityOptions() {
        return true;
    }

    @Override
    int getMax(int paramIndex) {
        return MAX_RADIUS;
    }

    @Override
    int getMin(int paramIndex) {
        return 1;
    }

    @Override
    public BlockPlacerTools redirectTool(BlockPlacerTools tool) {
        if (tool == BlockPlacerTools.Overlay)
            return BlockPlacerTools.Place;
        return super.redirectTool(tool);
    }

    @Override
    public Collection<BlockPos> addToGlobalPositions(LevelAccessor world, BlockPos targetPos, Direction targetFace,
                                                     Collection<BlockPos> affectedPositions, BlockPlacerTools usedTool) {

        boolean searchDiagonals = param1 == 0;
        boolean fuzzy = param2 == 0;
        boolean replace = usedTool != BlockPlacerTools.Overlay;
        int searchRange = param0;

        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> frontier = new LinkedList<>();

        BlockState state = world.getBlockState(targetPos);
        List<BlockPos> offsets = new LinkedList<>();

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    if (Math.abs(x) + Math.abs(y) + Math.abs(z) < 2 || searchDiagonals)
                        if (targetFace.getAxis()
                                .choose(x, y, z) == 0 || !surface)
                            offsets.add(new BlockPos(x, y, z));

        BlockPos startPos = replace ? targetPos : targetPos.relative(targetFace);
        frontier.add(startPos);

        while (!frontier.isEmpty()) {
            BlockPos currentPos = frontier.remove(0);
            if (visited.contains(currentPos))
                continue;
            visited.add(currentPos);
            if (!currentPos.closerThan(startPos, searchRange))
                continue;

            // Replace Mode
            if (replace) {
                BlockState stateToReplace = world.getBlockState(currentPos);
                BlockState stateAboveStateToReplace = world.getBlockState(currentPos.relative(targetFace));

                // Criteria
                if (stateToReplace.getDestroySpeed(world, currentPos) == -1)
                    continue;
                if (stateToReplace.getBlock() != state.getBlock() && !fuzzy)
                    continue;
                if (stateToReplace.canBeReplaced())
                    continue;
                if (BlockHelper.hasBlockSolidSide(stateAboveStateToReplace, world, currentPos.relative(targetFace),
                        targetFace.getOpposite()) && surface)
                    continue;
                affectedPositions.add(currentPos);

                // Search adjacent spaces
                for (BlockPos offset : offsets)
                    frontier.add(currentPos.offset(offset));
                continue;
            }

            // Place Mode
            BlockState stateToPlaceAt = world.getBlockState(currentPos);
            BlockState stateToPlaceOn = world.getBlockState(currentPos.relative(targetFace.getOpposite()));

            // Criteria
            if (stateToPlaceOn.canBeReplaced())
                continue;
            if (stateToPlaceOn.getBlock() != state.getBlock() && !fuzzy)
                continue;
            if (!stateToPlaceAt.canBeReplaced())
                continue;
            affectedPositions.add(currentPos);

            // Search adjacent spaces
            for (BlockPos offset : offsets)
                frontier.add(currentPos.offset(offset));
            continue;
        }

        return affectedPositions;
    }
}
