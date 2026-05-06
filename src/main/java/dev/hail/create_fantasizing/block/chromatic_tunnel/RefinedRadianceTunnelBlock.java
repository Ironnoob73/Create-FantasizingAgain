package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RefinedRadianceTunnelBlock extends ChromaticTunnelBlock {
    public RefinedRadianceTunnelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends BeltTunnelBlockEntity> getBlockEntityType() {
        return CFABlocks.REFINED_RADIANCE_TUNNEL_ENTITY.get();
    }
}
