package dev.hail.create_fantasizing.block.sturdy_girder;

import com.google.common.base.Predicates;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.girder.GirderPlacementHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
public class SturdyGirderPlacementHelper extends GirderPlacementHelper {
    @Override
    // Modified By Deepseek V4.1 Flash 1017:
    public Predicate<ItemStack> getItemPredicate() {
        return Predicates.or(AllBlocks.METAL_GIRDER::isIn, CFABlocks.STURDY_GIRDER::isIn);
    }
    @Override
    // Modified By Deepseek V4.1 Flash 1017:
    public Predicate<BlockState> getStatePredicate() {
        return Predicates.or(AllBlocks.METAL_GIRDER::has,
                CFABlocks.STURDY_GIRDER::has, CFABlocks.STURDY_GIRDER_ENCASED_SHAFT::has);
    }
}
