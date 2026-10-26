package dev.hail.create_fantasizing.integration.jei;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import com.simibubi.create.content.logistics.FlapStuffs;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

// Make animation worked by Deepseek V4.1 Flash 1017
public class ChromaticTunnelRecipeAnimation extends AnimatedKinetics {

    /**
     * Speed of the displayed belts, in RPM. There is no block entity driving the belts in a recipe
     * screen, so a fixed speed is used to give them the same run speed as a belt powered by a
     * typical Create network.
     */
    private static final float BELT_SPEED = 16;

    private final BlockState blockState;
    private boolean assembly = false;

    public ChromaticTunnelRecipeAnimation(BlockState blockState) {
        super();
        this.blockState = blockState;
    }

    public ChromaticTunnelRecipeAnimation(BlockState blockState, boolean assembly) {
        super();
        this.assembly = assembly;
        this.blockState = blockState;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset + 13, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        if (!assembly) {
            renderRunningBelt(guiGraphics, 0,
                    AllBlocks.BELT.getDefaultState().setValue(BeltBlock.PART, BeltPart.MIDDLE)
                            .setValue(BeltBlock.HORIZONTAL_FACING, Direction.EAST));
            renderRunningBelt(guiGraphics, -1,
                    AllBlocks.BELT.getDefaultState().setValue(BeltBlock.PART, BeltPart.START)
                            .setValue(BeltBlock.HORIZONTAL_FACING, Direction.EAST));
            renderRunningBelt(guiGraphics, 1,
                    AllBlocks.BELT.getDefaultState().setValue(BeltBlock.PART, BeltPart.END)
                            .setValue(BeltBlock.HORIZONTAL_FACING, Direction.EAST));
        }

        blockElement(blockState)
                .atLocal(0, 0, 0)
                .scale(24)
                .render(guiGraphics);

        renderFlap(guiGraphics, blockState);

        matrixStack.popPose();
    }

    /**
     * Renders one middle belt segment using Create's own belt animation, i.e. the same scrolling
     * sprite shift that {@link BeltRenderer} applies to belts in the world.
     * <p>
     * {@code GuiGameElement} cannot shift UVs, so the segment is drawn from a
     * {@link SuperByteBuffer} instead. The transform chain below mirrors what
     * {@code blockElement(AllPartialModels.BELT_MIDDLE).rotateBlock(0, 90, 0).atLocal(x, 1, 0).scale(24)}
     * would do internally.
     */
    private void renderRunningBelt(GuiGraphics guiGraphics, double xOffset, BlockState beltState) {
        if (!(beltState.getBlock() instanceof BeltBlock))
            return;

        PartialModel upBeltModel = AllPartialModels.BELT_MIDDLE;
        PartialModel downBeltModel = AllPartialModels.BELT_MIDDLE_BOTTOM;
        switch (beltState.getValue(BeltBlock.PART)){
            case BeltPart.START -> {
                upBeltModel = AllPartialModels.BELT_START;
                downBeltModel = AllPartialModels.BELT_START_BOTTOM;
            }
            case BeltPart.END -> {
                upBeltModel = AllPartialModels.BELT_END;
                downBeltModel = AllPartialModels.BELT_END_BOTTOM;
            }
        }

        PoseStack ms = guiGraphics.pose();
        ms.pushPose();
        ms.scale(24, 24, 24);
        ms.translate(xOffset, 1, 0);
        UIRenderHelper.flipForGuiRender(ms);
        ms.translate(.5, .5, .5);
        ms.mulPose(Axis.YP.rotationDegrees(90));
        ms.translate(-.5, -.5, -.5);

        // Same GUI state as GuiGameElement prepares for the other block elements of this animation
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        DEFAULT_LIGHTING.applyLighting();
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        SpriteShiftEntry spriteShift = BeltRenderer.getSpriteShiftEntry(null, false, false);

        VertexConsumer vb = guiGraphics.bufferSource().getBuffer(Sheets.cutoutBlockSheet());
        SuperByteBuffer upBeltBuffer = CachedBuffers.partial(upBeltModel, beltState);
        upBeltBuffer.light(LightTexture.FULL_BRIGHT);
        upBeltBuffer.shiftUVScrolling(spriteShift, getBeltScroll(spriteShift, beltState));
        upBeltBuffer.renderInto(ms, vb);
        SuperByteBuffer downBeltBuffer = CachedBuffers.partial(downBeltModel, beltState);
        downBeltBuffer.light(LightTexture.FULL_BRIGHT);
        downBeltBuffer.shiftUVScrolling(spriteShift, getBeltScroll(spriteShift, beltState));
        downBeltBuffer.renderInto(ms, vb);

        if (beltState.getValue(BeltBlock.PART) != BeltPart.MIDDLE) {
            SuperByteBuffer pulleyBuffer = CachedBuffers.partial(AllPartialModels.BELT_PULLEY, blockState);
            pulleyBuffer.light(LightTexture.FULL_BRIGHT);
            pulleyBuffer.rotateCenteredDegrees(90, Direction.Axis.Z);
            pulleyBuffer.rotateCenteredDegrees(-getCurrentAngle(), Direction.Axis.Y);
            pulleyBuffer.renderInto(ms, vb);
        }

        guiGraphics.bufferSource().endBatch();

        Lighting.setupFor3DItems();
        ms.popPose();
    }

    /**
     * Scroll offset for the belt sprites, calculated exactly like {@link BeltRenderer} does it, but
     * driven by {@link #BELT_SPEED} instead of a block entity's speed.
     */
    private float getBeltScroll(SpriteShiftEntry spriteShift, BlockState beltState) {
        float renderTick = AnimationTickHolder.getRenderTime();
        float time = renderTick * beltState.getValue(BeltBlock.HORIZONTAL_FACING)
                .getAxisDirection()
                .getStep();
        float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
        double scroll = BELT_SPEED * time / (31.5 * 16);
        scroll -= Math.floor(scroll);
        return (float) (scroll * spriteSize * 0.5f);
    }

    private void renderFlap(GuiGraphics guiGraphics, BlockState state) {
        VertexConsumer vb = guiGraphics.bufferSource().getBuffer(Sheets.cutoutBlockSheet());
        PoseStack ms = guiGraphics.pose();
        ms.pushPose();
        ms.scale(24, 24, 24);
        ms.translate(0, 0, 0);
        UIRenderHelper.flipForGuiRender(ms);
        ms.translate(.5, .5, .5);
        ms.mulPose(Axis.YP.rotationDegrees(90));
        ms.translate(-.5, -.5, -.5);

        SuperByteBuffer flapBuffer = CachedBuffers.partial(AllPartialModels.BELT_TUNNEL_FLAP, state);
        FlapStuffs.renderFlaps(ms, vb, flapBuffer, FlapStuffs.TUNNEL_PIVOT, Direction.NORTH,
                -(AnimationTickHolder.getRenderTime() * 0.05f % 1), 0, LightTexture.FULL_BRIGHT);

        guiGraphics.bufferSource().endBatch();

        Lighting.setupFor3DItems();
        ms.popPose();
    }
}
