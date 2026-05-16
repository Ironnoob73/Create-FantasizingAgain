package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFAMenus;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class CopperFluidBarrelMenu extends AbstractDoubleStorageMenu<CopperFluidBarrelEntity> {
    public CopperFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public CopperFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, CopperFluidBarrelEntity be) {
        super(type, id, inv, be);
    }

    public static CopperFluidBarrelMenu create(int id, Inventory inv, CopperFluidBarrelEntity be) {
        return new CopperFluidBarrelMenu(CFAMenus.COPPER_FLUID_BARREL_MENU.get(), id, inv, be);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    protected void addSlots() {
        dualBlock = contentHolder.isDoubleCrate();
        // player Slots
        addPlayerSlots(28, 102);
    }
}
