package dev.hail.create_fantasizing.item;

import com.simibubi.create.content.logistics.tunnel.BeltTunnelItem;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class ChromaticTunnelItem extends BeltTunnelItem {
    public ChromaticTunnelItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext ctx, BlockState state) {
        Player playerEntity = ctx.getPlayer();
        CollisionContext iSelectionContext =
                playerEntity == null ? CollisionContext.empty() : CollisionContext.of(playerEntity);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return (!this.mustSurvive() || CFABlocks.SHADOW_STEEL_TUNNEL.get()
                .isValidPositionForPlacement(state, world, pos)) && world.isUnobstructed(state, pos, iSelectionContext);
    }
}
