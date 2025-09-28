package dev.hail.create_fantasizing;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public enum CFAGuiTextures implements ScreenElement, TextureSheetSegment {
    BLOCK_PLACER("block_placer", 234, 103)
    ;
    public final ResourceLocation location;
    private final int width;
    private final int height;
    private final int startX;
    private final int startY;
    CFAGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }
    CFAGuiTextures(String location, int startX, int startY, int width, int height) {
        this(FantasizingMod.MOD_ID, location, startX, startY, width, height);
    }
    CFAGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }
    @Override
    public @NotNull ResourceLocation getLocation() {
        return location;
    }
    @Override
    public int getStartX() {
        return startX;
    }
    @Override
    public int getStartY() {
        return startY;
    }
    @Override
    public int getWidth() {
        return width;
    }
    @Override
    public int getHeight() {
        return height;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }
}
