package dev.hail.create_fantasizing.block;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.block.crate.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CFAMenus {
    public static final MenuEntry<AndesiteCrateMenu> ANDESITE_CRATE = register("andesite_crate", AndesiteCrateMenu::new, () -> AndesiteCrateScreen::new);
    public static final MenuEntry<IronCrateMenu> IRON_CRATE = register("iron_crate", IronCrateMenu::new, () -> IronCrateScreen::new);
    public static final MenuEntry<BrassCrateMenu> BRASS_CRATE = register("brass_crate", BrassCrateMenu::new, () -> BrassCrateScreen::new);
    public static final MenuEntry<SturdyCrateMenu> STURDY_CRATE = register("sturdy_crate", SturdyCrateMenu::new, () -> SturdyCrateScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return FantasizingMod.REGISTRATE.menu(name, factory, screenFactory).register();
    }
    public static void register() {
    }
}
