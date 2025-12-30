package dev.hail.create_fantasizing.block.sturdy_girder;

import com.google.common.base.Predicates;
import com.simibubi.create.content.decoration.girder.GirderPlacementHelper;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
public class SturdyGirderPlacementHelper extends GirderPlacementHelper {
    @Override
    public Predicate<ItemStack> getItemPredicate() {
        return CFABlocks.STURDY_GIRDER::isIn;
    }
    @Override
    public Predicate<BlockState> getStatePredicate() {
        return Predicates.or(CFABlocks.STURDY_GIRDER::has, CFABlocks.STURDY_GIRDER_ENCASED_SHAFT::has);
    }
}
