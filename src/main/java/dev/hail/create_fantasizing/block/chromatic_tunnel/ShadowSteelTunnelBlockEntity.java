package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.*;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.data.CFARecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Optional;

public class ShadowSteelTunnelBlockEntity extends ChromaticTunnelBlockEntity {

    public ShadowSteelTunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CFABlocks.SHADOW_STEEL_TUNNEL_ENITIY.get(),
                (be, context) ->  {
                    if (be.cap == null && be.level != null) {
                        if (AllBlocks.BELT.has(be.level.getBlockState(be.worldPosition.below()))) {
                            BlockEntity beBelow = be.level.getBlockEntity(be.worldPosition.below());
                            if (beBelow != null) {
                                IItemHandler capBelow = be.level.getCapability(Capabilities.ItemHandler.BLOCK, be.worldPosition.below(), Direction.UP);
                                if (capBelow != null) {
                                    be.cap = capBelow;
                                }
                            }
                        }
                    }
                    return be.cap;
                }
        );
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<RecipeHolder<ShadowPlatingRecipe>> recipe = getRecipe(input.stack);
        if (recipe.isEmpty())
            return false;
        if (simulate)
            return true;
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level,
                input.stack, recipe.get().value(), true);

        for (ItemStack created : outputs) {
            if (!created.isEmpty()) {
                break;
            }
        }

        outputList.addAll(outputs);
        return true;
    }

    public Optional<RecipeHolder<ShadowPlatingRecipe>> getRecipe(ItemStack item) {
        Optional<RecipeHolder<ShadowPlatingRecipe>> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(level, item, CFARecipeTypes.SHADOW_PLATING.getType(), ShadowPlatingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;

        return CFARecipeTypes.SHADOW_PLATING.find(new SingleRecipeInput(item), level);
    }
}
