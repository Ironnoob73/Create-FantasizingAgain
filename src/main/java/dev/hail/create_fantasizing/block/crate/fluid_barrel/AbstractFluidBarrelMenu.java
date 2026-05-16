package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractFluidBarrelMenu<T extends AbstractFluidBarrelEntity> extends AbstractDoubleStorageMenu<T> {

    public int menuAllowedCapacity;

    public AbstractFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AbstractFluidBarrelMenu(MenuType<?> type, int id, Inventory inv, T be) {
        super(type, id, inv, be);
    }
}
