package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.data.CFARecipeTypes;
import dev.hail.create_fantasizing.integration.jei.AssemblyChromaticTunnelCategory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class ExposingRecipe extends ChromaticTunnelRecipe {
    public ExposingRecipe(ProcessingRecipeParams params) {
        super(CFARecipeTypes.EXPOSING, params, "recipe.exposing", CFABlocks.REFINED_RADIANCE_TUNNEL.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyChromaticTunnelCategory.AssemblyExposing::new;
    }
}
