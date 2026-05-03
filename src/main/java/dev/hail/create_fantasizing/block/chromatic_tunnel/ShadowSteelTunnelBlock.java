package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShadowSteelTunnelBlock extends ChromaticTunnelBlock {
    public ShadowSteelTunnelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends BeltTunnelBlockEntity> getBlockEntityType() {
        return CFABlocks.SHADOW_STEEL_TUNNEL_ENITIY.get();
    }
}
