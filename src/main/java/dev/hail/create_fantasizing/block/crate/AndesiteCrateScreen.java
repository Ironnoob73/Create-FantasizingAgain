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
public class AndesiteCrateScreen extends AbstractCrateScreen<AndesiteCrateMenu> {

    public AndesiteCrateScreen(AndesiteCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.ANDESITE_CRATE;
        renderedItem = blockEntry.asStack();
        background = container.dualBlock ? CFAGuiTextures.ANDESITE_DOUBLE_CRATE : CFAGuiTextures.ANDESITE_CRATE;

        capacityLabelOffset = menu.dualBlock ? 137 : 65;
        textureXShift = menu.dualBlock ? 8 : (imageWidth - (background.getWidth() - 8)) / 2;
        itemYShift = menu.dualBlock ? 0 : -16;

        initWindowSizeHeight = background.getHeight();
        initWindowXOffset = menu.dualBlock ? 4 : 0;
        initYShiftOffset = 11;
        initEditBoxXPos = 23;
        initAllowedItemsLabelXPos = capacityLabelOffset;
        initAllowedItemsLabelYPos = 104;
        initAllowedRange = (menu.dualBlock ? 2049 : 1025);
        initExtraAreaYOffset = 56;

        editIcon = CFAGuiTextures.ANDESITE_EDIT;
        bgInvXOffset = windowXOffset + (menu.dualBlock ? 14 : 0);
        bgInvYOffset = 4;
        bgAddTexXShift = true;
        bgAddTexYShift = false;
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = YShift;

        String itemCount = String.valueOf(menu.contentHolder.inventory.getItemCount() + (menu.dualBlock ? menu.contentHolder.getOtherCrate().inventory.getItemCount() : 0));
        ms.drawString(font, itemCount, x + capacityLabelOffset - 13 - font.width(itemCount), y + 108, 0x4B3A22, false);

        for (int slot = 0; slot < (menu.dualBlock ? 32 : 16); slot++) {
            if (allowedCapacity.getState() > slot * 64)
                continue;
            int slotsPerRow = (menu.dualBlock ? 8 : 4);
            int slotX = x + 22 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.ANDESITE_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
}
