package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ChromaticTunnelBlockEntity extends BeltTunnelBlockEntity {
    public ChromaticTunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
