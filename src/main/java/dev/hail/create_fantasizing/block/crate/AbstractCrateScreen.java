package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public abstract class AbstractCrateScreen<T extends AbstractCrateMenu> extends AbstractDoubleStorageScreen<T> {
    protected int initWindowSizeHeight;
    protected int initWindowXOffset;

    protected int capacityLabelOffset;
    protected int textureXShift;
    protected int itemYShift;
    protected int YShift;

    protected int initEditBoxXPos;
    protected int initAllowedItemsLabelXPos;
    protected boolean initIsBrassCrate = false;
    protected int initAllowedItemsLabelYPos;
    protected int initAllowedRange;
    protected int initExtraAreaYOffset;

    protected CFAGuiTextures editIcon;
    protected int bgInvXOffset;
    protected int bgInvYOffset;
    protected boolean bgAddTexXShift;
    protected boolean bgAddTexYShift;

    // For Brass Crate & Sturdy Crate
    protected int secBgOffset;
    protected IconButton foldButton;
    protected IconButton pageUpButton;
    protected IconButton pageDownButton;

    public AbstractCrateScreen(T container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    /*@Override
    protected void init() {
        setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()), initWindowSizeHeight + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(initWindowXOffset, - 7);
        super.init();
        clearWidgets();

        int x = leftPos + textureXShift;
        int y = YShift;

        Consumer<String> onTextChanged;
        onTextChanged = s -> nameBox.setX(nameBoxX(s, nameBox));
        nameBox = new EditBox(new NoShadowFontWrapper(font), x + initEditBoxXPos, y + 3, background.getWidth(), 10,
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
        if (menu.contentHolder.isMountedProxy())
            nameBox.setEditable(false);
        addRenderableWidget(nameBox);

        Label allowedItemsLabel = new Label(x + initAllowedItemsLabelXPos + 4 - (initIsBrassCrate ? 2 : 0), y + initAllowedItemsLabelYPos + 4, Component.empty()).colored(0xFFFFFF).withShadow();
        allowedCapacity = new ScrollInput(x + initAllowedItemsLabelXPos, y + initAllowedItemsLabelYPos, 41, 16).titled(storageSpace.plainCopy())
                .withRange(0, initAllowedRange)
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(menu.contentHolder.getOverallAllowedAmount())
                .calling(s -> lastModification = 0);
        allowedCapacity.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedCapacity);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background.getWidth(), y + background.getHeight() - initExtraAreaYOffset + itemYShift, 80, 80)
        );
    }*/

    /*@Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth()) + bgInvXOffset;
        int invY = YShift + background.getHeight() + bgInvYOffset;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos + (bgAddTexXShift ? textureXShift : 0);
        int y = YShift + (bgAddTexYShift ? textureYShift : 0);

        background.render(ms, x, y);
        if (backgroundSec != null)
            backgroundSec.render(ms, x, y + (menu.isFullInterface() ? secBgOffset : 19));

        String text = nameBox.getValue();
        if (!nameBox.isFocused()) {
            if (nameBox.getValue()
                    .isEmpty()) {
                text = renderedItem.getHoverName()
                        .getString();
                ms.drawString(font, text, nameBoxX(text, nameBox), y + 3, 0x3D3C48, false);
            }
            if (!menu.contentHolder.isMountedProxy())
                editIcon.render(ms, nameBoxX(text, nameBox) + font.width(text) + 5, y + 2);
        } else if (menu.contentHolder.isMountedProxy()) {
            nameBox.setFocused(false);
        }
    }*/
}
