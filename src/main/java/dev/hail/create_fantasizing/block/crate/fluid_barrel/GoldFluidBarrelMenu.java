package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class GoldFluidBarrelMenu extends AbstractFluidBarrelMenu<GoldFluidBarrelEntity> {
    public GoldFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public GoldFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, GoldFluidBarrelEntity be) {
        super(type, id, inv, be);
    }

    public static GoldFluidBarrelMenu create(int id, Inventory inv, GoldFluidBarrelEntity be) {
        return new GoldFluidBarrelMenu(CFAMenus.GOLD_FLUID_BARREL_MENU.get(), id, inv, be);
    }
}
