package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class IronCrateScreen extends AbstractCrateScreen<IronCrateMenu> {

    public IronCrateScreen(IronCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.IRON_CRATE;
        renderedItem = blockEntry.asStack();
        background = container.dualBlock ? CFAGuiTextures.IRON_DOUBLE_CRATE : CFAGuiTextures.IRON_CRATE;

        capacityLabelOffset = menu.dualBlock ? 155 : 65;
        textureXShift = menu.dualBlock ? 9 : (imageWidth - (background.getWidth() - 8)) / 2;
        itemYShift = menu.dualBlock ? 0 : -16;

        initWindowSizeHeight = background.getHeight();
        initWindowXOffset = menu.dualBlock ? -5 : 0;
        initYShiftOffset = 11;
        initEditBoxXPos = 23;
        initAllowedItemsLabelXPos = capacityLabelOffset;
        initAllowedItemsLabelYPos = 104;
        initAllowedRange = (menu.dualBlock ? 2561 : 1281);
        initExtraAreaYOffset = 56;

        editIcon = CFAGuiTextures.IRON_EDIT;
        bgInvXOffset = (menu.dualBlock ? 14 : 0);
        bgInvYOffset = 4;
        bgAddTexXShift = true;
        bgAddTexYShift = false;
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = YShift;

        String itemCount = String.valueOf(menu.contentHolder.inventory.itemCount + (menu.dualBlock ? menu.contentHolder.getOtherCrate().inventory.itemCount : 0));
        ms.drawString(font, itemCount, x + capacityLabelOffset - 13 - font.width(itemCount), y + 108, 0x4B3A22, false);

        for (int slot = 0; slot < (menu.dualBlock ? 40 : 20); slot++) {
            if (allowedCapacity.getState() > slot * 64)
                continue;
            int slotsPerRow = (menu.dualBlock ? 10 : 5);
            int slotX = x + 13 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.IRON_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
}