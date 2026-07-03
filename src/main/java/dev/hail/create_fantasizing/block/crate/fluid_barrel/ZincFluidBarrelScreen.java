package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ZincFluidBarrelScreen extends AbstractFluidBarrelScreen<ZincFluidBarrelMenu> {
    public ZincFluidBarrelScreen(ZincFluidBarrelMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.ZINC_FLUID_BARREL;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.ZINC_FLUID_BARREL;
        editButton = CFAGuiTextures.ZINC_EDIT;
    }
}