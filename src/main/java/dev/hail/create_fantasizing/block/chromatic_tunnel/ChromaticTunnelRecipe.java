package dev.hail.create_fantasizing.block.chromatic_tunnel;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.hail.create_fantasizing.FantasizingMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public abstract class ChromaticTunnelRecipe extends StandardProcessingRecipe<SingleRecipeInput> implements IAssemblyRecipe {
    String assemblyDescription;
    Block processMachine;

    public ChromaticTunnelRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params, String assemblyDescription, Block processMachine) {
        super(typeInfo, params);
        this.assemblyDescription = assemblyDescription;
        this.processMachine = processMachine;
    }

    @Override
    public boolean matches(SingleRecipeInput inv, @NotNull Level worldIn) {
        if (inv.isEmpty())
            return false;
        return ingredients.getFirst()
                .test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }
    @Override
    protected int getMaxOutputCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return Component.translatable(FantasizingMod.MOD_ID + "." + assemblyDescription);
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(processMachine);
    }
}
