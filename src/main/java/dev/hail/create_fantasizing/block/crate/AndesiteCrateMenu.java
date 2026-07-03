package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFAMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * THIS CLASS WAS MODIFIED BY DEEPSEEK V4
 * AndesiteCrateEntity -> AbstractCrateEntity
 */
public class AndesiteCrateMenu extends AbstractCrateMenu {
    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public AndesiteCrateMenu(MenuType<?> type, int id, Inventory inv, AbstractCrateEntity be) {
        super(type, id, inv, be);
    }
    public static AndesiteCrateMenu create(int id, Inventory inv, AbstractCrateEntity be) {
        return new AndesiteCrateMenu(CFAMenus.ANDESITE_CRATE.get(), id, inv, be);
    }

    @Override
    protected void addSlots() {
        dualBlock = contentHolder.isDoubleCrate();
        int x = dualBlock ? 31 : 53;
        int maxCol = dualBlock ? 8 : 4;
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < maxCol; ++col) {
                this.addSlot(
                        new SlotItemHandler(col + row * maxCol < 16 ? contentHolder.inventory : contentHolder.getOtherCrate().inventory,
                                col + row * maxCol - (col + row * maxCol < 16 ? 0 : 16), x + col * 18, row * 18 + 31));
            }
        }
        // player Slots
        addPlayerSlots(dualBlock ? 28 : 8, 160);
    }
}
