package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

@OnlyIn(Dist.CLIENT)
public class CopperFluidBarrelScreen extends AbstractDoubleStorageScreen<CopperFluidBarrelMenu> {
    public CopperFluidBarrelScreen(CopperFluidBarrelMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        blockEntry = CFABlocks.COPPER_FLUID_BARREL;
        renderedItem = blockEntry.asStack();
        background = CFAGuiTextures.COPPER_FLUID_BARREL;
    }

    @Override
    protected void init() {
        setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()), background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(-4, - 7);
        super.init();
        clearWidgets();

        capacityLabelOffset = 137;
        textureXShift = 8;
        itemYShift = 0;
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

        Label allowedCapacityLabel = new Label(x + capacityLabelOffset + 4, y + 54, Component.empty()).colored(0xFFFFFF).withShadow();
        allowedCapacity = new ScrollInput(x + capacityLabelOffset, y + 50, 41, 16).titled(storageSpace.plainCopy())
                .withRange(0, (menu.dualBlock ? 16001 : 8001))
                .writingTo(allowedCapacityLabel)
                .withShiftStep(1000)
                .setState(menu.contentHolder.allowedCapacity)
                .calling(s -> lastModification = 0);
        allowedCapacity.onChanged();
        addRenderableWidget(allowedCapacityLabel);
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

        if (!menu.contentHolder.tankInventory.isEmpty()){
            GuiGameElement.of(menu.contentHolder.tankInventory.getFluid().getFluid().defaultFluidState().createLegacyBlock())
                    .<GuiGameElement.GuiRenderBuilder>at(x + 65, y + 36)
                    .scale(16)
                    .render(ms);
        }
        if (mouseX >= x + 65 && mouseX < x + 65 + 16 && mouseY >= y + 20 && mouseY < y + 36) {
            ms.renderComponentTooltip(font,
                    List.of(CreateLang
                            .text(menu.contentHolder.tankInventory.isEmpty() ? FluidStack.EMPTY.getHoverName().getString() : menu.contentHolder.tankInventory.getFluid().getHoverName().getString())
                            .color(menu.contentHolder.tankInventory.isEmpty() ? AbstractSimiWidget.COLOR_DISABLED.get(true) : AbstractSimiWidget.COLOR_HOVER.get(true))
                            .component()),
                    mouseX, mouseY);
        }

        String itemCount = String.valueOf(((AbstractFluidBarrelEntity) menu.contentHolder.getMainCrate()).tankInventory.getFluid().getAmount());
        ms.drawString(font, itemCount, x + capacityLabelOffset - 13 - font.width(itemCount), y + 54, 0x4B3A22, false);

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
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth()) + windowXOffset + 10;
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
            CFAGuiTextures.COPPER_EDIT.render(ms, nameBoxX(text, nameBox) + font.width(text) + 5, y + 2);
        }
    }
}
