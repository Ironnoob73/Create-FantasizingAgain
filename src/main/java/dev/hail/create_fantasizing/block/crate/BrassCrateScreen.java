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
public class BrassCrateScreen extends AbstractCrateScreen<BrassCrateMenu> {

    public BrassCrateScreen(BrassCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.BRASS_CRATE;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.BRASS_CRATE_UPSIDE;
        backgroundSec = CFAGuiTextures.BRASS_CRATE_DOWNSIDE;

        capacityLabelOffset = menu.isFullInterface() ? 72 : 0;
        textureXShift = 8;
        textureYShift = menu.isFullInterface() ? -36 : 0;
        itemYShift = menu.isFullInterface() ? 72 : 0;

        initWindowSizeHeight = (menu.isFullInterface() ? 199 : 127);
        initWindowXOffset = -4;
        initYShiftOffset = (menu.isFullInterface() ? 47 : 11);
        initEditBoxXPos = 23;
        initAllowedItemsLabelXPos = 133;
        initIsBrassCrate = true;
        initAllowedItemsLabelYPos = (menu.isFullInterface() ? 176 : 104);
        initAllowedRange = (menu.dualBlock ? 4609 : 2305);
        initExtraAreaYOffset = 20;
        initFoldable = true;

        editIcon = CFAGuiTextures.BRASS_EDIT;
        bgInvXOffset = textureXShift + 2;
        bgInvYOffset = (menu.isFullInterface() ? 72 : 40);
        bgAddTexXShift = true;
        bgAddTexYShift = true;
        secBgOffset = 91;
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = YShift + textureYShift;

        String itemCount = String.valueOf(menu.contentHolder.inventory.getItemCount() + (menu.dualBlock ? menu.contentHolder.getOtherCrate().inventory.getItemCount() : 0));
        ms.drawString(font, itemCount, x + 125 - font.width(itemCount), y + 108 + capacityLabelOffset, 0x4B3A22, false);

        for (int slot = 0; slot < (menu.isFullInterface() ? 72 : 36); slot++) {
            if (allowedCapacity.getState() - (menu.page == 1 && menu.isFold ? 36*64 : 0) > slot * 64)
                continue;
            int slotsPerRow = 9;
            int slotX = x + 13 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.BRASS_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        if (menu.isFold && menu.contentHolder.isDoubleCrate())
            ms.drawString(font, Component.translatable("create_fantasizing.gui.crate.page", menu.page + 1), x + 32 + (menu.contentHolder.isMountedProxy() ? 0 : 18), y + 108 + capacityLabelOffset, 0x4B3A22, false);

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + backgroundSec.getWidth(), y + background.getHeight() - 20 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
}
