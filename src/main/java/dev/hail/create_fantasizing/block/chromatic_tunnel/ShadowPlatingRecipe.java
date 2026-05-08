package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.data.CFARecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShadowPlatingRecipe extends ChromaticTunnelRecipes {
    public ShadowPlatingRecipe(ProcessingRecipeParams params) {
        super(CFARecipeTypes.SHADOW_PLATING, params, "recipe.assembly.exposing", CFABlocks.SHADOW_STEEL_TUNNEL.get());
    }
}
