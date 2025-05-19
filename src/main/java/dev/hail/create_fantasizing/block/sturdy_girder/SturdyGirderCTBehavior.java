package dev.hail.create_fantasizing.block.sturdy_girder;

import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.ConnectedTextureBehaviour;
import dev.hail.create_fantasizing.block.CFASpriteShifts;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SturdyGirderCTBehavior extends ConnectedTextureBehaviour.Base {
    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (!state.hasProperty(GirderBlock.X))
            return null;
        return !state.getValue(GirderBlock.X) && !state.getValue(GirderBlock.Z) && direction.getAxis() != Direction.Axis.Y
                ? CFASpriteShifts.STURDY_GIRDER_POLE
                : null;
    }
    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos,
                              BlockPos otherPos, Direction face) {
        if (other.getBlock() != state.getBlock())
            return false;
        return !other.getValue(GirderBlock.X) && !other.getValue(GirderBlock.Z);
    }
}
