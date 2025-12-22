package dev.hail.create_fantasizing.block.crate;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.event.CFAPackets;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class AndesiteCrateScreen extends AbstractSimiContainerScreen<AndesiteCrateMenu> {

    protected CFAGuiTextures background;
    private List<Rect2i> extraAreas = Collections.emptyList();

    private final AndesiteCrateEntity be;
    private ScrollInput allowedItems;
    private int lastModification;

    private int textureXShift;
    private int itemYShift;

    private final ItemStack renderedItem = CFABlocks.ANDESITE_CRATE.asStack();
    private final Component storageSpace = Component.translatable("gui.adjustable_crate.storageSpace");

    public AndesiteCrateScreen(AndesiteCrateMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        be = container.be;
        lastModification = -1;
        background = container.doubleCrate ? CFAGuiTextures.DOUBLE_CRATE : CFAGuiTextures.CRATE;
        init();
    }

    @Override
    protected void init() {
        setWindowSize(Math.max(background.getWidth(), PLAYER_INVENTORY.getWidth()), background.getHeight() + 4 + PLAYER_INVENTORY.getHeight());
        setWindowOffset(menu.doubleCrate ? -2 : 0, 0);
        super.init();
        clearWidgets();

        int itemLabelOffset = menu.doubleCrate ? 137 : 65;
        textureXShift = menu.doubleCrate ? 0 : (imageWidth - (background.getWidth() - 8)) / 2;
        itemYShift = menu.doubleCrate ? 0 : -16;

        int x = leftPos + textureXShift;
        int y = topPos;

        Label allowedItemsLabel = new Label(x + itemLabelOffset + 4, y + 108, (Component) Component.EMPTY).colored(0xFFFFFF)
                .withShadow();
        allowedItems = new ScrollInput(x + itemLabelOffset, y + 104, 41, 16).titled(storageSpace.plainCopy())
                .withRange(1, (menu.doubleCrate ? 2049 : 1025))
                .writingTo(allowedItemsLabel)
                .withShiftStep(64)
                .setState(be.allowedAmount)
                .calling(s -> lastModification = 0);
        allowedItems.onChanged();
        addRenderableWidget(allowedItemsLabel);
        addRenderableWidget(allowedItems);

        extraAreas = ImmutableList.of(
                new Rect2i(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, 80, 80)
        );
    }

    @Override
    public void render(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);

        int x = leftPos + textureXShift;
        int y = topPos;

        //drawCenteredString(ms, font, title, x + (background.getWidth() - 8) / 2, y + 3, 0xFFFFFF);

        //String itemCount = String.valueOf(be.itemCount);
        //font.draw(ms, itemCount, x + itemLabelOffset - 13 - font.width(itemCount), y + 108, 0x4B3A22);

        for (int slot = 0; slot < (menu.doubleCrate ? 32 : 16); slot++) {
            if (allowedItems.getState() > slot * 64)
                continue;
            int slotsPerRow = (menu.doubleCrate ? 8 : 4);
            int slotX = x + 22 + (slot % slotsPerRow) * 18;
            int slotY = y + 19 + (slot / slotsPerRow) * 18;
            CFAGuiTextures.CRATE_LOCKED_SLOT.render(ms, slotX, slotY);
        }

        GuiGameElement.of(renderedItem)
                .<GuiGameElement.GuiRenderBuilder>at(x + background.getWidth(), y + background.getHeight() - 56 + itemYShift, -200)
                .scale(5)
                .render(ms);
    }
    @Override
    public void renderBg(@NotNull GuiGraphics ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = topPos + background.getHeight() + 4;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos + textureXShift;
        int y = topPos;

        background.render(ms, x, y);
    }
    @Override
    public void removed() {
        CFAPackets.getChannel().sendToServer(new ConfigureCreatePacket(be.getBlockPos(), allowedItems.getState()));
    }

    @Override
    public void containerTick() {
        super.containerTick();

        if (minecraft != null && minecraft.level != null && !CFABlocks.ANDESITE_CRATE.has(minecraft.level.getBlockState(be.getBlockPos())))
            minecraft.setScreen(null);

        if (lastModification >= 0)
            lastModification++;

        if (lastModification >= 15) {
            lastModification = -1;
            CFAPackets.getChannel().sendToServer(new ConfigureCreatePacket(be.getBlockPos(), allowedItems.getState()));
        }

        if (menu.doubleCrate != be.isDoubleCrate())
            menu.playerInventory.player.closeContainer();
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }
}
