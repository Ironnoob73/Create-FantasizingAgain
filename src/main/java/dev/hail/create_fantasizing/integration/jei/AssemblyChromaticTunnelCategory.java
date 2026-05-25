package dev.hail.create_fantasizing.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyChromaticTunnelCategory extends SequencedAssemblySubCategory {

    ChromaticTunnelRecipeAnimation tunnel;

    public AssemblyChromaticTunnelCategory(BlockState blockState) {
        super(25);
        tunnel = new ChromaticTunnelRecipeAnimation(blockState);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0, 47f, 0);
        ms.scale(.6f, .6f, .6f);
        tunnel.draw(graphics, getWidth() / 2 - 7, 30);
        ms.popPose();
    }

    public static class AssemblyExposing extends AssemblyChromaticTunnelCategory{
        public AssemblyExposing(){
            super(CFABlocks.REFINED_RADIANCE_TUNNEL.getDefaultState());
        }
    }
    public static class AssemblyShadowPlating extends AssemblyChromaticTunnelCategory{
        public AssemblyShadowPlating(){
            super(CFABlocks.SHADOW_STEEL_TUNNEL.getDefaultState());
        }
    }
}