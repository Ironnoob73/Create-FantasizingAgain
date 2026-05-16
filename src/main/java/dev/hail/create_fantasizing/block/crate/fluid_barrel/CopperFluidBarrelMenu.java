package dev.hail.create_fantasizing.block.crate.fluid_barrel;

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

    @Override
    protected void addSlots() {

    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }
}
