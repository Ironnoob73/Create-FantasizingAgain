package dev.hail.create_fantasizing.item.BlockPlacer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;

import java.util.Collection;
import java.util.List;

public abstract class BPShapedBrush extends BPBrush {

    public BPShapedBrush(int amtParams) {
        super(amtParams);
    }

    @Override
    public Collection<BlockPos> addToGlobalPositions(LevelAccessor world, BlockPos targetPos, Direction targetFace,
                                                     Collection<BlockPos> affectedPositions, BlockPlacerTools usedTool) {
        List<BlockPos> includedPositions = getIncludedPositions();
        if (includedPositions == null)
            return affectedPositions;
        for (BlockPos blockPos : includedPositions)
            affectedPositions.add(targetPos.offset(blockPos));
        return affectedPositions;
    }

    abstract List<BlockPos> getIncludedPositions();

}
