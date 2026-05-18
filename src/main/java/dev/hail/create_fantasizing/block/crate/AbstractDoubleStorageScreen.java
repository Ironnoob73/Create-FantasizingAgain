package dev.hail.create_fantasizing.block.crate;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.block.crate.fluid_barrel.AbstractFluidBarrelEntity;
import dev.hail.create_fantasizing.block.crate.fluid_barrel.ConfigureFluidBarrelPacket;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractDoubleStorageScreen<T extends AbstractDoubleStorageMenu> extends AbstractSimiContainerScreen<T> {
    protected CFAGuiTextures background;
    protected CFAGuiTextures backgroundSec;
    protected CFAGuiTextures editButton;
    protected List<Rect2i> extraAreas = Collections.emptyList();
    protected EditBox nameBox;
    protected ScrollInput allowedCapacity;
    protected int lastModification;
    protected int capacityLabelOffset;

    protected int YShift;

    protected int textureXShift;
    protected int textureYShift;
    protected int itemYShift;

    protected BlockEntry<? extends AbstractDoubleStorageBlock> blockEntry;
    protected ItemStack renderedItem;
    protected final Component storageSpace = Component.translatable("create_fantasizing.gui.crate.storage_space");

    private final AbstractDoubleStorageEntity storageHolder = (AbstractDoubleStorageEntity) menu.contentHolder;

    public AbstractDoubleStorageScreen(T container, Inventory inv, Component title) {
        super(container, inv, title);
        lastModification = -1;
    }

    protected int nameBoxX(String s, EditBox nameBox) {
        return getGuiLeft() + textureXShift + (background.getWidth() - (Math.min(font.width(s), nameBox.getWidth()) + 10)) / 2;
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
        sendPacketToServer();
        super.removed();
    }

    @Override
    public void containerTick() {
        super.containerTick();

        if (minecraft != null && minecraft.level != null && !blockEntry.has(minecraft.level.getBlockState(storageHolder.getBlockPos())))
            minecraft.setScreen(null);

        if (lastModification >= 0)
            lastModification++;

        if (lastModification >= 15) {
            lastModification = -1;
            sendPacketToServer();
        }

        if (menu.dualBlock != storageHolder.isDoubleCrate())
            menu.playerInventory.player.closeContainer();
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return extraAreas;
    }

    private void sendPacketToServer(){
        if (storageHolder instanceof AbstractCrateEntity)
            CatnipServices.NETWORK.sendToServer(new ConfigureCratePacket(storageHolder.getBlockPos(), allowedCapacity.getState(), nameBox.getValue()));
        else if (storageHolder instanceof AbstractFluidBarrelEntity && allowedCapacity != null)
            CatnipServices.NETWORK.sendToServer(new ConfigureFluidBarrelPacket(storageHolder.getBlockPos(), allowedCapacity.getState(), nameBox.getValue()));
    }
}
