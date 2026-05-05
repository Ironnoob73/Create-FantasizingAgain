package dev.hail.create_fantasizing.item;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelItem;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.integration.encased.EncasedHelper;
import fr.iglee42.createcasing.casings.CasingSets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaticTunnelItem extends BeltTunnelItem {
    public ChromaticTunnelItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, Player player, ItemStack itemStack,
                                                 BlockState state) {
        if (EncasedHelper.IS_LOADED){
            if (!world.isClientSide) {
                BeltBlockEntity belt = BeltHelper.getSegmentBE(world, pos.below());
                if (belt != null && belt.casing == BeltBlockEntity.CasingType.NONE)
                    belt.setCasingType(CFABlocks.SHADOW_STEEL_TUNNEL.has(state) ? CasingSets.SHADOW_STEEL.getBeltCasingType() : CasingSets.REFINED_RADIANCE.getBeltCasingType());
            }
        }
        return super.updateCustomBlockEntityTag(pos, world, player, itemStack, state);
    }
}
