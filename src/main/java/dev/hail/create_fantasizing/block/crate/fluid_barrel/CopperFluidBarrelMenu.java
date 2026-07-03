package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class CopperFluidBarrelMenu extends AbstractFluidBarrelMenu<AbstractFluidBarrelEntity> {
    public CopperFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CopperFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, AbstractFluidBarrelEntity be) {
        super(type, id, inv, be);
    }

    public static CopperFluidBarrelMenu create(int id, Inventory inv, AbstractFluidBarrelEntity be) {
        return new CopperFluidBarrelMenu(CFAMenus.COPPER_FLUID_BARREL_MENU.get(), id, inv, be);
    }
}
