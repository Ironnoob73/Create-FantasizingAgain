package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DiamondFluidBarrelScreen extends AbstractFluidBarrelScreen<DiamondFluidBarrelMenu> {
    public DiamondFluidBarrelScreen(DiamondFluidBarrelMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.DIAMOND_FLUID_BARREL;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.COPPER_FLUID_BARREL;
        editButton = CFAGuiTextures.COPPER_EDIT;
    }
}
