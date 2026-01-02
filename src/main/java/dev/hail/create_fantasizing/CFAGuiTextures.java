package dev.hail.create_fantasizing;

import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public enum CFAGuiTextures implements ScreenElement, TextureSheetSegment {
    BLOCK_PLACER("block_placer", 234, 103),

    ANDESITE_CRATE("andesite_crate", 124, 127),
    ANDESITE_DOUBLE_CRATE("andesite_crate", 0, 127, 196, 127),
    ANDESITE_EDIT("andesite_crate", 124, 0, 9, 9),
    ANDESITE_CRATE_LOCKED_SLOT("andesite_crate", 125, 109, 18, 18),

    IRON_CRATE("iron_crate", 124, 127),
    IRON_DOUBLE_CRATE("iron_crate", 0, 127, 214, 127),
    IRON_EDIT("iron_crate", 124, 0, 9, 9),
    IRON_CRATE_LOCKED_SLOT("iron_crate", 125, 109, 18, 18),

    BRASS_CRATE_UPSIDE("brass_crate", 188, 91),
    BRASS_CRATE_DOWNSIDE("brass_crate", 0, 91, 196, 108),
    BRASS_EDIT("brass_crate", 196, 0, 9, 9),
    BRASS_CRATE_LOCKED_SLOT("brass_crate", 197, 181, 18, 18),

    STURDY_CRATE_UPSIDE("sturdy_crate", 206, 109),
    STURDY_CRATE_DOWNSIDE("sturdy_crate", 0, 109, 214, 126),
    STURDY_EDIT("sturdy_crate", 214, 0, 9, 9),
    STURDY_CRATE_LOCKED_SLOT("sturdy_crate", 215, 217, 18, 18),


    CRATE_INTERFACE_UNFOLD("brass_crate", 208, 0, 16, 16),
    CRATE_INTERFACE_FOLD("brass_crate", 224, 0, 16, 16),
    CRATE_PAGE_UP("brass_crate", 208, 16, 16, 16),
    CRATE_PAGE_DOWN("brass_crate", 224, 16, 16, 16);

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
