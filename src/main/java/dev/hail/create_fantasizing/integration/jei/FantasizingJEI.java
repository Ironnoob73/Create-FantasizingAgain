package dev.hail.create_fantasizing.integration.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ToolboxColoringRecipeMaker;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ExposingRecipe;
import dev.hail.create_fantasizing.block.chromatic_tunnel.ShadowPlatingRecipe;
import dev.hail.create_fantasizing.data.CFARecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FantasizingJEI implements IModPlugin {
    private static final ResourceLocation ID = FantasizingMod.resourceLocation("jei_plugin");

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();

    private void loadCategories() {
        allCategories.clear();

        CreateRecipeCategory<?>
                shadow_plating = builder(ShadowPlatingRecipe.class)
                .addTypedRecipes(CFARecipeTypes.SHADOW_PLATING)
                .catalyst(CFABlocks.SHADOW_STEEL_TUNNEL::get)
                .doubleItemIcon(CFABlocks.SHADOW_STEEL_TUNNEL.get(), AllItems.SHADOW_STEEL.get())
                .emptyBackground(177, 70)
                .build("shadow_plating", ShadowPlatingCategory::new),
                exposing = builder(ExposingRecipe.class)
                .addTypedRecipes(CFARecipeTypes.EXPOSING)
                .catalyst(CFABlocks.REFINED_RADIANCE_TUNNEL::get)
                .doubleItemIcon(CFABlocks.REFINED_RADIANCE_TUNNEL.get(), AllItems.REFINED_RADIANCE.get())
                .emptyBackground(177, 70)
                .build("exposing", ExposingCategory::new);
    }

    private <T extends Recipe<? extends RecipeInput>> FantasizingJEI.CategoryBuilder<T> builder(Class<T> recipeClass) {
        return new FantasizingJEI.CategoryBuilder<>(recipeClass);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        allCategories.forEach(c -> c.registerRecipes(registration));
        registration.addRecipes(RecipeTypes.CRAFTING, ToolboxColoringRecipeMaker.createRecipes().toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    private class CategoryBuilder<T extends Recipe<?>> extends CreateRecipeCategory.Builder<T> {
        public CategoryBuilder(Class<? extends T> recipeClass) {
            super(recipeClass);
        }

        @Override
        public CreateRecipeCategory<T> build(ResourceLocation id, CreateRecipeCategory.Factory<T> factory) {
            CreateRecipeCategory<T> category = super.build(id, factory);
            allCategories.add(category);
            return category;
        }
        @Override
        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            return build(FantasizingMod.resourceLocation(name), factory);
        }
    }
}
