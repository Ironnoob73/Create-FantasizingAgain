package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.event.CFAPackets;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@OnlyIn(Dist.CLIENT)
public class AndesiteCrateScreen extends AbstractSimiContainerScreen<AndesiteCrateMenu> {

    protected CFAGuiTextures background;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private EditBox nameBox;
    private ScrollInput allowedItems;
    private int lastModification;
    private int itemLabelOffset;

    private int YShift;

    private int textureXShift;
    private int itemYShift;

    private final ItemStack renderedItem = CFABlocks.ANDESITE_CRATE.asStack();
    private final Component storageSpace = Component.translatable("create_fantasizing.gui.crate.storage_space");

    public AndesiteCrateScreen(AndesiteCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        lastModification = -1;
        background = container.doubleCrate ? CFAGuiTextures.ANDESITE_DOUBLE_CRATE : CFAGuiTextures.ANDESITE_CRATE;
    }

    @Override
    protected void init() {
        super.init();
        setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()), background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(menu.doubleCrate ? -2 : 0, 0);
        clearWidgets();

        itemLabelOffset = menu.doubleCrate ? 137 : 65;
        textureXShift = menu.doubleCrate ? -6 : (imageWidth - (background.getWidth() - 8)) / 2;
        itemYShift = menu.doubleCrate ? 0 : -16;
        YShift = topPos - 32;

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
        
        Label allowedItemsLabel = new Label(x + itemLabelOffset + 4, y + 108, Component.empty()).colored(0xFFFFFF).withShadow();
        allowedItems = new ScrollInput(x + itemLabelOffset, y + 104, 41, 16).titled(storageSpace.plainCopy())
                .withRange(0, (menu.doubleCrate ? 2049 : 1025))
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(menu.contentHolder.getOverallAllowedAmount())
                .calling(s -> lastModification = 0);
        allowedItems.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedItems);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, 80, 80)
        );
    }
    
    private int nameBoxX(String s, EditBox nameBox) {
        return getGuiLeft() + textureXShift + (background.getWidth() - (Math.min(font.width(s), nameBox.getWidth()) + 10)) / 2;
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = YShift;

        String itemCount = String.valueOf(menu.contentHolder.inventory.itemCount + (menu.doubleCrate ? menu.contentHolder.getOtherCrate().inventory.itemCount : 0));
        ms.drawString(font, itemCount, x + itemLabelOffset - 13 - font.width(itemCount), y + 108, 0x4B3A22, false);

        for (int slot = 0; slot < (menu.doubleCrate ? 32 : 16); slot++) {
            if (allowedItems.getState() > slot * 64)
                continue;
            int slotsPerRow = (menu.doubleCrate ? 8 : 4);
            int slotX = x + 22 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.ANDESITE_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = YShift + background.getHeight() + 4;
        renderPlayerInventory(ms, invX - 6, invY);

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
            CFAGuiTextures.ANDESITE_EDIT.render(ms, nameBoxX(text, nameBox) + font.width(text) + 5, y + 2);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        boolean hitEnter = getFocused() instanceof EditBox
                && (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER);

        if (hitEnter && nameBox.isFocused()) {
            nameBox.setFocused(false);
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void removed() {
        CFAPackets.getChannel().sendToServer(new ConfigureCreatePacket(menu.contentHolder.getBlockPos(), allowedItems.getState(), nameBox.getValue()));
        super.removed();
    }

    @Override
    public void containerTick() {
        super.containerTick();

        if (minecraft != null && minecraft.level != null && !CFABlocks.ANDESITE_CRATE.has(minecraft.level.getBlockState(menu.contentHolder.getBlockPos())))
            minecraft.setScreen(null);

        if (lastModification >= 0)
            lastModification++;

        if (lastModification >= 15) {
            lastModification = -1;
            CFAPackets.getChannel().sendToServer(new ConfigureCreatePacket(menu.contentHolder.getBlockPos(), allowedItems.getState(), nameBox.getValue()));
        }

        if (menu.doubleCrate != menu.contentHolder.isDoubleCrate())
            menu.playerInventory.player.closeContainer();
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }
}
