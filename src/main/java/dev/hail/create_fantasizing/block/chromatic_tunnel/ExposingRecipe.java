package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.data.CFARecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExposingRecipe extends ChromaticTunnelRecipes {
    public ExposingRecipe(ProcessingRecipeParams params) {
        super(CFARecipeTypes.EXPOSING, params, "recipe.assembly.exposing", CFABlocks.REFINED_RADIANCE_TUNNEL.get());
    }
}
