package dev.hail.create_fantasizing.item.block_placer;

import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.ZapperScreen;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.*;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.hail.create_fantasizing.CFAGuiTextures;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.data.CFADataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockPlacerScreen extends ZapperScreen {

    protected CFAGuiTextures background;
    protected final Component placementSection = CreateLang.translateDirect("gui.terrainzapper.placement");
    protected final Component toolSection = CreateLang.translateDirect("gui.terrainzapper.tool");
    protected Integer loreIndex = 0;
    protected final List<Component> brushOptions =
            CreateLang.translatedOptions("gui.terrainzapper.brush", "cuboid", "sphere", "cylinder", "surface", "cluster");

    protected List<IconButton> toolButtons;
    protected List<IconButton> destroyButtons;
    protected List<IconButton> placementButtons;

    protected ScrollInput brushInput;
    protected Label brushLabel;
    protected List<ScrollInput> brushParams = new ArrayList<>(3);
    protected List<Label> brushParamLabels = new ArrayList<>(3);
    protected IconButton followDiagonals;
    protected IconButton acrossMaterials;
    protected Indicator followDiagonalsIndicator;
    protected Indicator acrossMaterialsIndicator;

    protected BlockPlacerBrushes currentBrush;
    protected int[] currentBrushParams = new int[]{1, 1, 1};
    protected boolean currentFollowDiagonals;
    protected boolean currentAcrossMaterials;
    protected BlockPlacerTools currentTool;
    protected boolean currentDestroyMode;
    protected PlacementOptions currentPlacement;

    private static final VarHandle HANDLE = acquireVarHandle();

    private static VarHandle acquireVarHandle() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup promotedLookup = MethodHandles.privateLookupIn(ZapperScreen.class, lookup);
            return promotedLookup.findVarHandle(
                ZapperScreen.class,
                "confirmButton",
                IconButton.class
            );
        } catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }


    public BlockPlacerScreen(ItemStack zapper, InteractionHand hand) {
        super(AllGuiTextures.TERRAINZAPPER, zapper, hand);
        this.background = CFAGuiTextures.BLOCK_PLACER;
        fontColor = 0x767676;
        title = zapper.getHoverName();

        CompoundTag nbt = zapper.getOrCreateTag();
        currentBrush = NBTHelper.readEnum(nbt, "Brush", BlockPlacerBrushes.class);
        if (nbt.contains("BrushParams", Tag.TAG_COMPOUND)) {
            BlockPos paramsData = NbtUtils.readBlockPos(nbt.getCompound("BrushParams"));
            currentBrushParams[0] = paramsData.getX();
            currentBrushParams[1] = paramsData.getY();
            currentBrushParams[2] = paramsData.getZ();
            if (currentBrushParams[1] == 0) {
                currentFollowDiagonals = true;
            }
            if (currentBrushParams[2] == 0) {
                currentAcrossMaterials = true;
            }
        }
        currentTool = NBTHelper.readEnum(nbt, "Tool", BlockPlacerTools.class);
        currentPlacement = NBTHelper.readEnum(nbt, "Placement", PlacementOptions.class);
    }

    @Override
    protected void init() {
        loreIndex = new Random().nextInt(5);

        super.init();

        int x = guiLeft;
        int y = guiTop;

        //removeWidgets((IconButton)HANDLE.get());

        brushLabel = new Label(x + 61, y + 25, CommonComponents.EMPTY).withShadow();
        brushInput = new SelectionScrollInput(x + 56, y + 20, 77, 18).forOptions(brushOptions)
                .titled(CreateLang.translateDirect("gui.terrainzapper.brush"))
                .writingTo(brushLabel)
                .calling(brushIndex -> {
                    currentBrush = BlockPlacerBrushes.values()[brushIndex];
                    initBrushParams(x, y);
                });

        brushInput.setState(currentBrush.ordinal());

        addRenderableWidget(brushLabel);
        addRenderableWidget(brushInput);

        initBrushParams(x, y);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
        drawOnBackground(graphics, x, y);

        renderBlock(graphics, x, y);
        renderZapper(graphics, x, y);
    }

    protected void initBrushParams(int x, int y) {
        BPBrush currentBrush = this.currentBrush.get();

        // Brush Params

        removeWidgets(brushParamLabels);
        removeWidgets(brushParams);

        brushParamLabels.clear();
        brushParams.clear();

        for (int index = 0; index < 3; index++) {
            Label label = new Label(x + 65 + 20 * index, y + 45, CommonComponents.EMPTY).withShadow();

            final int finalIndex = index;
            ScrollInput input = new ScrollInput(x + 56 + 20 * index, y + 40, 18, 18)
                    .withRange(currentBrush.getMin(index), currentBrush.getMax(index) + 1)
                    .writingTo(label)
                    .titled(currentBrush.getParamLabel(index)
                            .plainCopy())
                    .calling(state -> {
                        currentBrushParams[finalIndex] = state;
                        label.setX(x + 65 + 20 * finalIndex - font.width(label.text) / 2);
                    });
            input.setState(currentBrushParams[index]);
            input.onChanged();

            if (index >= currentBrush.amtParams) {
                input.visible = false;
                label.visible = false;
                input.active = false;
            }

            brushParamLabels.add(label);
            brushParams.add(input);
        }

        addRenderableWidgets(brushParamLabels);
        addRenderableWidgets(brushParams);

        // Connectivity Options

        if (followDiagonals != null) {
            removeWidget(followDiagonals);
            removeWidget(followDiagonalsIndicator);
            removeWidget(acrossMaterials);
            removeWidget(acrossMaterialsIndicator);
            followDiagonals = null;
            followDiagonalsIndicator = null;
            acrossMaterials = null;
            acrossMaterialsIndicator = null;
        }

        if (currentBrush.hasConnectivityOptions()) {
            int x1 = x + 7 + 4 * 18;
            int y1 = y + 79;
            followDiagonalsIndicator = new Indicator(x1, y1 - 6, CommonComponents.EMPTY);
            followDiagonals = new IconButton(x1, y1, AllIcons.I_FOLLOW_DIAGONAL);
            x1 += 18;
            acrossMaterialsIndicator = new Indicator(x1, y1 - 6, CommonComponents.EMPTY);
            acrossMaterials = new IconButton(x1, y1, AllIcons.I_FOLLOW_MATERIAL);

            followDiagonals.withCallback(() -> {
                followDiagonalsIndicator.state = followDiagonalsIndicator.state == Indicator.State.OFF ? Indicator.State.ON : Indicator.State.OFF;
                currentFollowDiagonals = !currentFollowDiagonals;
            });
            followDiagonals.setToolTip(CreateLang.translateDirect("gui.terrainzapper.searchDiagonal"));
            acrossMaterials.withCallback(() -> {
                acrossMaterialsIndicator.state = acrossMaterialsIndicator.state == Indicator.State.OFF ? Indicator.State.ON : Indicator.State.OFF;
                currentAcrossMaterials = !currentAcrossMaterials;
            });
            acrossMaterials.setToolTip(CreateLang.translateDirect("gui.terrainzapper.searchFuzzy"));
            addRenderableWidget(followDiagonals);
            addRenderableWidget(followDiagonalsIndicator);
            addRenderableWidget(acrossMaterials);
            addRenderableWidget(acrossMaterialsIndicator);
            if (currentFollowDiagonals)
                followDiagonalsIndicator.state = Indicator.State.ON;
            if (currentAcrossMaterials)
                acrossMaterialsIndicator.state = Indicator.State.ON;
        }

        // Tools

        if (toolButtons != null)
            removeWidgets(toolButtons);

        BlockPlacerTools[] toolValues = currentBrush.getSupportedTools();
        toolButtons = new ArrayList<>(toolValues.length);
        for (int id = 0; id < toolValues.length; id++) {
            BlockPlacerTools tool = toolValues[id];
            IconButton toolButton = new IconButton(x + 7 + id * 18, y + 79, tool.icon);
            toolButton.withCallback(() -> {
                toolButtons.forEach(b -> b.green = false);
                toolButton.green = true;
                currentTool = tool;
            });
            toolButton.setToolTip(CreateLang.translateDirect("gui.terrainzapper.tool." + tool.translationKey));
            toolButtons.add(toolButton);
        }

        int toolIndex = -1;
        for (int i = 0; i < toolValues.length; i++)
            if (currentTool == toolValues[i])
                toolIndex = i;
        if (toolIndex == -1) {
            currentTool = toolValues[0];
            toolIndex = 0;
        }
        toolButtons.get(toolIndex).green = true;

        addRenderableWidgets(toolButtons);

        // Destroy Mode

        if (destroyButtons != null)
            removeWidgets(destroyButtons);

        destroyButtons = new ArrayList<>(1);

        IconButton dropButton = new IconButton(x + 7, y + 117, CFAGuiTextures.DROP_BUTTON);
        dropButton.withCallback(() -> {
            destroyButtons.forEach(b -> b.green = false);
            dropButton.green = true;
            currentDestroyMode = false;
        });
        dropButton.setToolTip(Component.translatable(FantasizingMod.MOD_ID + ".gui.block_placer.destroy_mode.drop"));
        destroyButtons.add(dropButton);

        IconButton voidButton = new IconButton(x + 25, y + 117, CFAGuiTextures.VOID_BUTTON);
        voidButton.withCallback(() -> {
            destroyButtons.forEach(b -> b.green = false);
            voidButton.green = true;
            currentDestroyMode = true;
        });
        voidButton.setToolTip(Component.translatable(FantasizingMod.MOD_ID + ".gui.block_placer.destroy_mode.void"));
        destroyButtons.add(voidButton);

        destroyButtons.get(currentDestroyMode ? 0 : 1).green = true;

        addRenderableWidgets(destroyButtons);

        // Placement Options

        if (placementButtons != null)
            removeWidgets(placementButtons);

        if (currentBrush.hasPlacementOptions()) {
            PlacementOptions[] placementValues = PlacementOptions.values();
            placementButtons = new ArrayList<>(placementValues.length);
            for (int id = 0; id < placementValues.length; id++) {
                PlacementOptions option = placementValues[id];
                IconButton placementButton = new IconButton(x + 136 + id * 18, y + 79, option.icon);
                placementButton.withCallback(() -> {
                    placementButtons.forEach(b -> b.green = false);
                    placementButton.green = true;
                    currentPlacement = option;
                });
                placementButton.setToolTip(CreateLang.translateDirect("gui.terrainzapper.placement." + option.translationKey));
                placementButtons.add(placementButton);
            }

            placementButtons.get(currentPlacement.ordinal()).green = true;

            addRenderableWidgets(placementButtons);
        }
    }

    @Override
    protected void drawOnBackground(GuiGraphics graphics, int x, int y) {
        super.drawOnBackground(graphics, x, y);

        BPBrush currentBrush = this.currentBrush.get();
        for (int index = 2; index >= currentBrush.amtParams; index--)
            AllGuiTextures.TERRAINZAPPER_INACTIVE_PARAM.render(graphics, x + 56 + 20 * index, y + 40);

        graphics.drawString(font, toolSection, x + 7, y + 69, fontColor, false);
        graphics.drawString(font,
                Component.translatable(FantasizingMod.MOD_ID + ".gui.block_placer.destroy_mode"),
                x + 7, y + 107, fontColor, false);
        if (currentBrush.hasPlacementOptions())
            graphics.drawString(font, placementSection, x + 136, y + 69, fontColor, false);
        for (int i = 0; i <= 2; i++){
            graphics.drawString(font,
                    Component.translatable(FantasizingMod.MOD_ID + ".gui.block_placer.lore." + loreIndex + "." + i).withStyle(ChatFormatting.ITALIC),
                    x + 80, y + 107 + ( i * 10 ), fontColor, false);
        }
    }

    @Override
    protected ConfigureZapperPacket getConfigurationPacket() {
        int brushParamX = currentBrushParams[0];
        int brushParamY = followDiagonalsIndicator != null ? followDiagonalsIndicator.state == Indicator.State.ON ? 0 : 1
                : currentBrushParams[1];
        int brushParamZ = acrossMaterialsIndicator != null ? acrossMaterialsIndicator.state == Indicator.State.ON ? 0 : 1
                : currentBrushParams[2];
        return new ConfigureBlockPlacerPacket(hand, currentPattern, currentBrush, brushParamX, brushParamY, brushParamZ, currentTool, currentPlacement);
    }

    @Override
    public void removed() {
        ConfigureZapperPacket packet = getConfigurationPacket();
        packet.configureZapper(zapper);
        CFAPackets.getChannel().sendToServer(packet);
    }
}
