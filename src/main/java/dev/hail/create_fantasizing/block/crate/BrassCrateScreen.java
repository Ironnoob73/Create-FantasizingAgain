package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@OnlyIn(Dist.CLIENT)
public class BrassCrateScreen extends AbstractDoubleStorageScreen<BrassCrateMenu> {

    protected IconButton foldButton;
    protected IconButton pageUpButton;
    protected IconButton pageDownButton;

    public BrassCrateScreen(BrassCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.BRASS_CRATE;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.BRASS_CRATE_UPSIDE;
        backgroundSec = CFAGuiTextures.BRASS_CRATE_DOWNSIDE;
    }

    @Override
    protected void init() {
        /*if (!menu.player.hasData(CFAAttachmentTypes.FOLD_INTERFACE) || menu.player.getData(CFAAttachmentTypes.FOLD_INTERFACE) != menu.isFold){
            menu.setPlayerInterfaceFold(CFAConfig.foldInterface);
            menu.refreshMenu();
        }*/

        setWindowSize(Math.max(backgroundSec.getWidth(), PLAYER_INVENTORY.getWidth()), (menu.isFullInterface() ? 199 : 127) + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(-4, - 7);
        super.init();
        clearWidgets();

        itemLabelOffset = menu.isFullInterface() ? 72 : 0;
        textureXShift = 8;
        textureYShift = menu.isFullInterface() ? -36 : 0;
        itemYShift = menu.isFullInterface() ? 72 : 0;
        YShift = topPos + (menu.isFullInterface() ? 43 : 7);

        int x = leftPos + textureXShift;
        int y = YShift + textureYShift;

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

        Label allowedItemsLabel = new Label(x + 133 + textureXShift, y + 108 + itemLabelOffset, Component.empty()).colored(0xFFFFFF).withShadow();
        allowedItems = new ScrollInput(x + 131, y + 104 + itemLabelOffset, 41, 16).titled(storageSpace.plainCopy())
                .withRange(0, (menu.dualBlock ? 4609 : 2305))
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(menu.contentHolder.getOverallAllowedAmount())
                .calling(s -> lastModification = 0);
        allowedItems.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedItems);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background.getWidth(), y + background.getHeight() - 20 + itemYShift, 80, 80)
        );
        
        // Page
        if (menu.dualBlock){
            removeWidgets(foldButton, pageUpButton, pageDownButton);
            int appropriateHeight = Minecraft.getInstance()
                    .getWindow()
                    .getGuiScaledHeight() - 10;
            if(menu.isFold) {
                foldButton = new IconButton(x + 7, y + 102, CFAGuiTextures.CRATE_INTERFACE_UNFOLD);
                pageUpButton = new IconButton(x + 7, y + 132, AllIcons.I_MTD_LEFT);
            } else if (appropriateHeight < 300){
                foldButton = new IconButton(x + 7, y + 174, CFAGuiTextures.CRATE_INTERFACE_FOLD);
                /*foldButton.withCallback(() -> {
                    CFAConfig.foldInterface = !menu.isFold;
                    menu.setPlayerInterfaceFold(!menu.isFold);
                    menu.refreshMenu();
                });*/
                addRenderableWidget(foldButton);
            }
        }
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = YShift + textureYShift;

        String itemCount = String.valueOf(menu.contentHolder.inventory.itemCount + (menu.dualBlock ? menu.contentHolder.getOtherCrate().inventory.itemCount : 0));
        ms.drawString(font, itemCount, x + 125 - font.width(itemCount), y + 108 + itemLabelOffset, 0x4B3A22, false);

        for (int slot = 0; slot < (menu.isFullInterface() ? 72 : 36); slot++) {
            if (allowedItems.getState() > slot * 64)
                continue;
            int slotsPerRow = 9;
            int slotX = x + 13 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.BRASS_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        if (menu.dualBlock && foldButton != null && foldButton.isHovered()){
            ms.renderComponentTooltip(font,
                    List.of(Component.translatable("create_fantasizing.gui.crate.no_fold").withStyle(ChatFormatting.RED),
                            Component.translatable("create_fantasizing.gui.crate.no_fold.0").withStyle(ChatFormatting.GRAY),
                            Component.translatable("create_fantasizing.gui.crate.no_fold.1").withStyle(ChatFormatting.GRAY)),
                    mouseX, mouseY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + backgroundSec.getWidth(), y + background.getHeight() - 20 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth()) + textureXShift - 2;
        int invY = YShift + background.getHeight() + (menu.isFullInterface() ? 76 : 40);
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos + textureXShift;
        int y = YShift + textureYShift;

        background.render(ms, x, y);
        backgroundSec.render(ms, x, y + (menu.isFullInterface() ? 91 : 19));

        String text = nameBox.getValue();
        if (!nameBox.isFocused()) {
            if (nameBox.getValue()
                    .isEmpty()) {
                text = renderedItem.getHoverName()
                        .getString();
                ms.drawString(font, text, nameBoxX(text, nameBox), y + 3, 0x3D3C48, false);
            }
            CFAGuiTextures.BRASS_EDIT.render(ms, nameBoxX(text, nameBox) + font.width(text) + 5, y + 2);
        }
    }
}
