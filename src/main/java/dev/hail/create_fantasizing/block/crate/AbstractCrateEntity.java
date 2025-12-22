package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.content.logistics.crate.CrateBlock;
import com.simibubi.create.content.logistics.crate.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AbstractCrateEntity extends CrateBlockEntity {
    public int allowedAmount;
    public AbstractCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isDoubleCrate() {
        return getBlockState().getValue(AbstractCrateBlock.DOUBLE);
    }

    public boolean isSecondaryCrate() {
        if (!hasLevel())
            return false;
        if (!(getBlockState().getBlock() instanceof CrateBlock))
            return false;
        return isDoubleCrate() && getFacing().getAxisDirection() == Direction.AxisDirection.NEGATIVE;
    }

    public Direction getFacing() {
        return getBlockState().getValue(AbstractCrateBlock.FACING);
    }
}
