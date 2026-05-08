package dev.hail.create_fantasizing.integration.jei;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ShadowPlatingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ShadowPlatingCategory extends CreateRecipeCategory<ShadowPlatingRecipe> {
    private final ChromaticTunnelRecipeAnimation animation = new ChromaticTunnelRecipeAnimation(CFABlocks.SHADOW_STEEL_TUNNEL.getDefaultState());

    public ShadowPlatingCategory(Info<ShadowPlatingRecipe> info) {
        super(info);
    }

    @Override
    public @NotNull Component getTitle()
    {
        return Component.translatable("recipe.create_fantasizing.shadow_plating.title");
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, ShadowPlatingRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().getFirst());

        List<ProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    protected void draw(ShadowPlatingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(gui, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(gui, 52, 54);

        animation.draw(gui, getBackground().getWidth() / 2 - 17, 22);
    }
}
