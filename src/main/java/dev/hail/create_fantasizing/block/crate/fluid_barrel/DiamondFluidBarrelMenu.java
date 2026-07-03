package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class DiamondFluidBarrelMenu extends AbstractFluidBarrelMenu<AbstractFluidBarrelEntity> {
    public DiamondFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public DiamondFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, AbstractFluidBarrelEntity be) {
        super(type, id, inv, be);
    }

    public static DiamondFluidBarrelMenu create(int id, Inventory inv, AbstractFluidBarrelEntity be) {
        return new DiamondFluidBarrelMenu(CFAMenus.DIAMOND_FLUID_BARREL_MENU.get(), id, inv, be);
    }
}
