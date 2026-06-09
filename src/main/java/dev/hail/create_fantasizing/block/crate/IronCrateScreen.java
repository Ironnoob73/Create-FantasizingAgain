package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@OnlyIn(Dist.CLIENT)
public class IronCrateScreen extends AbstractCrateScreen<IronCrateMenu> {

    public IronCrateScreen(IronCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.IRON_CRATE;
        renderedItem = blockEntry.asStack();
        background = container.dualBlock ? CFAGuiTextures.IRON_DOUBLE_CRATE : CFAGuiTextures.IRON_CRATE;

        initWindowSizeHeight = background.getHeight();
        initWindowXOffset = menu.dualBlock ? -5 : 0;
        //capacityLabelOffset = menu.dualBlock ? 155 : 65;
        //textureXShift = menu.dualBlock ? 9 : (imageWidth - (background.getWidth() - 8)) / 2;
        //itemYShift = menu.dualBlock ? 0 : -16;
        //YShift = topPos + 7;
        initEditBoxXPos = 23;
        initAllowedItemsLabelXPos = capacityLabelOffset;
        initAllowedItemsLabelYPos = 104;
        initAllowedRange = (menu.dualBlock ? 2561 : 1281);
        initExtraAreaYOffset = 56;

        editIcon = CFAGuiTextures.IRON_EDIT;
        bgInvXOffset = windowXOffset + (menu.dualBlock ? 14 : 0);
        bgInvYOffset = 4;
        bgAddTexXShift = true;
        bgAddTexYShift = false;
    }

    @Override
    protected void init() {
        setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()), background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(menu.dualBlock ? -5 : 0,  - 7);
        super.init();
        clearWidgets();

        capacityLabelOffset = menu.dualBlock ? 155 : 65;
        textureXShift = menu.dualBlock ? 9 : (imageWidth - (background.getWidth() - 8)) / 2;
        itemYShift = menu.dualBlock ? 0 : -16;
        YShift = topPos + 7;

        int x = leftPos + textureXShift;
        int y = YShift;

        Consumer<String> onTextChanged;
        onTextChanged = s -> nameBox.setX(nameBoxX(s, nameBox));
        nameBox = new EditBox(new NoShadowFontWrapper(font), x + 23, y + 3, background.getWidth(), 10,
                Component.empty());
        nameBox.setBordered(false);
        nameBox.setMaxLength(25);
        nameBox.setTextColor(0x3D3C48);
        if (menu.contentHolder != null) {
            nameBox.setValue(menu.contentHolder.customName);
        }
        nameBox.setFocused(false);
        nameBox.mouseClicked(0, 0, 0);
        nameBox.setResponder(onTextChanged);
        nameBox.setX(nameBoxX(nameBox.getValue(), nameBox));
        addRenderableWidget(nameBox);

        Label allowedItemsLabel = new Label(x + capacityLabelOffset + 4, y + 108, Component.empty()).colored(0xFFFFFF).withShadow();
        allowedCapacity = new ScrollInput(x + capacityLabelOffset, y + 104, 41, 16).titled(storageSpace.plainCopy())
                .withRange(0, (menu.dualBlock ? 2561 : 1281))
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(menu.contentHolder.getOverallAllowedAmount())
                .calling(s -> lastModification = 0);
        allowedCapacity.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedCapacity);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, 80, 80)
        );
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
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth()) + (menu.dualBlock ? 14 : 0);
        int invY = YShift + background.getHeight() + 4;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos + textureXShift;
        int y = YShift;

        background.render(ms, x, y);

        String text = nameBox.getValue();
        if (!nameBox.isFocused()) {
            if (nameBox.getValue()
                    .isEmpty()) {
                text = renderedItem.getHoverName()
                        .getString();
                ms.drawString(font, text, nameBoxX(text, nameBox), y + 3, 0x3D3C48, false);
            }
            CFAGuiTextures.IRON_EDIT.render(ms, nameBoxX(text, nameBox) + font.width(text) + 5, y + 2);
        }
    }
}