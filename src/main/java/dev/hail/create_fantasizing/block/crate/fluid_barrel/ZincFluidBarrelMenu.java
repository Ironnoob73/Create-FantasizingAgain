package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ZincFluidBarrelMenu extends AbstractFluidBarrelMenu<ZincFluidBarrelEntity> {
    public ZincFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public ZincFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, ZincFluidBarrelEntity be) {
        super(type, id, inv, be);
    }

    public static ZincFluidBarrelMenu create(int id, Inventory inv, ZincFluidBarrelEntity be) {
        return new ZincFluidBarrelMenu(CFAMenus.ZINC_FLUID_BARREL_MENU.get(), id, inv, be);
    }
}
