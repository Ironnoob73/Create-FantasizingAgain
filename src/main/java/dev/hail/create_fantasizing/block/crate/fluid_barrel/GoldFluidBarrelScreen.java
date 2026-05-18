package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldFluidBarrelScreen extends AbstractFluidBarrelScreen<GoldFluidBarrelMenu> {
    public GoldFluidBarrelScreen(GoldFluidBarrelMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.GOLD_FLUID_BARREL;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.GOLD_FLUID_BARREL;
        editButton = CFAGuiTextures.GOLD_EDIT;
    }
}
