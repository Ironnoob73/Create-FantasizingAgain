package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.data.CFARecipeTypes;
import dev.hail.create_fantasizing.integration.jei.AssemblyChromaticTunnelCategory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class ShadowPlatingRecipe extends ChromaticTunnelRecipe {
    public ShadowPlatingRecipe(ProcessingRecipeParams params) {
        super(CFARecipeTypes.SHADOW_PLATING, params, "recipe.shadow_plating", CFABlocks.SHADOW_STEEL_TUNNEL.get());
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> AssemblyChromaticTunnelCategory.AssemblyShadowPlating::new;
    }
}
