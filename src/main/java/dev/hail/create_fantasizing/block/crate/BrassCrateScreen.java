package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@OnlyIn(Dist.CLIENT)
public class BrassCrateScreen extends AbstractSimiContainerScreen<BrassCrateMenu> {

    protected CFAGuiTextures background0 = CFAGuiTextures.BRASS_CRATE_UPSIDE;
    protected CFAGuiTextures background1 = CFAGuiTextures.BRASS_CRATE_DOWNSIDE;
    private List<Rect2i> extraAreas = Collections.emptyList();
    private EditBox nameBox;
    private ScrollInput allowedItems;
    private int lastModification;
    private int itemLabelOffset;

    private int YShift;

    private int textureXShift;
    private int itemYShift;

    private final ItemStack renderedItem = CFABlocks.BRASS_CRATE.asStack();
    private final Component storageSpace = Component.translatable("create_fantasizing.gui.crate.storage_space");

    public BrassCrateScreen(BrassCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        lastModification = -1;
    }

    @Override
    protected void init() {
        super.init();
        setWindowSize(Math.max(background0.getWidth(), PLAYER_INVENTORY.getWidth()), background0.getHeight() + 40 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(menu.doubleCrate ? -2 : 0, 0);
        clearWidgets();

        itemLabelOffset = menu.doubleCrate ? 137 : 65;
        textureXShift = menu.doubleCrate ? 0 : (imageWidth - (background0.getWidth() - 8)) / 2;
        itemYShift = menu.doubleCrate ? 0 : -16;
        YShift = topPos - 32;

        int x = leftPos + textureXShift;
        int y = YShift;

        Consumer<String> onTextChanged;
        onTextChanged = s -> nameBox.setX(nameBoxX(s, nameBox));
        nameBox = new EditBox(new NoShadowFontWrapper(font), x + 23, y + 3, background0.getWidth(), 10,
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
                .withRange(0, (menu.doubleCrate ? 4609 : 2305))
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(menu.contentHolder.getOverallAllowedAmount())
                .calling(s -> lastModification = 0);
        allowedItems.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedItems);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background0.getWidth(), y + background0.getHeight() - 20 + itemYShift, 80, 80)
        );
    }

    private int nameBoxX(String s, EditBox nameBox) {
        return getGuiLeft() + textureXShift + (background0.getWidth() - (Math.min(font.width(s), nameBox.getWidth()) + 10)) / 2;
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
            CFAGuiTextures.BRASS_CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + background0.getWidth(), y + background0.getHeight() - 20 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = YShift + background0.getHeight() + 40;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos + textureXShift;
        int y = YShift;

        background0.render(ms, x, y);
        background1.render(ms, x, y + 19);

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
        CatnipServices.NETWORK.sendToServer(new ConfigureCreatePacket(menu.contentHolder.getBlockPos(), allowedItems.getState(), nameBox.getValue()));
        super.removed();
    }

    @Override
    public void containerTick() {
        super.containerTick();

        if (minecraft != null && minecraft.level != null && !CFABlocks.BRASS_CRATE.has(minecraft.level.getBlockState(menu.contentHolder.getBlockPos())))
            minecraft.setScreen(null);

        if (lastModification >= 0)
            lastModification++;

        if (lastModification >= 15) {
            lastModification = -1;
            CatnipServices.NETWORK.sendToServer(new ConfigureCreatePacket(menu.contentHolder.getBlockPos(), allowedItems.getState(), nameBox.getValue()));
        }

        if (menu.doubleCrate != menu.contentHolder.isDoubleCrate())
            menu.playerInventory.player.closeContainer();
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }
}
