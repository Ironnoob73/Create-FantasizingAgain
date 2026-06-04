package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractFoldAbleCrateScreen <T extends AbstractDoubleStorageMenu> extends AbstractDoubleStorageScreen<T>{

    protected IconButton foldButton;
    protected IconButton pageUpButton;
    protected IconButton pageDownButton;

    public AbstractFoldAbleCrateScreen(T container, Inventory inv, Component title) {
        super(container, inv, title);
    }
}
