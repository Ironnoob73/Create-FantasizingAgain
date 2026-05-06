package dev.hail.create_fantasizing.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class ExposingAnimation extends AnimatedKinetics {

    PartialModel beltPartial = AllPartialModels.BELT_MIDDLE;

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset + 13, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        // Don't know how to get the animation work...
        SuperByteBuffer beltBuffer = CachedBuffers.partial(beltPartial, AllBlocks.BELT.getDefaultState().setValue(BeltBlock.PART, BeltPart.MIDDLE));
        SpriteShiftEntry spriteShift = BeltRenderer.getSpriteShiftEntry(null, false, false);
        float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
        double scroll = AnimationTickHolder.getRenderTime() / (31.5 * 16);
        scroll = scroll - Math.floor(scroll);
        scroll = scroll * spriteSize * 0.5;
        beltBuffer.shiftUVScrolling(spriteShift, (float) scroll);
        PoseStack localTransforms = new PoseStack();
        VertexConsumer vb = guiGraphics.bufferSource().getBuffer(RenderType.solid());
        beltBuffer.transform(localTransforms).renderInto(guiGraphics.pose(), vb);

        blockElement(beltPartial)
                .rotateBlock(0, 90, 0)
                .atLocal(0, 1, 0)
                .scale(24)
                .render(guiGraphics);
        blockElement(beltPartial)
                .rotateBlock(0, 90, 0)
                .atLocal(-1, 1, 0)
                .scale(24)
                .render(guiGraphics);
        blockElement(beltPartial)
                .rotateBlock(0, 90, 0)
                .atLocal(1, 1, 0)
                .scale(24)
                .render(guiGraphics);

        blockElement(CFABlocks.REFINED_RADIANCE_TUNNEL.getDefaultState())
                .atLocal(0, 0, 0)
                .scale(24)
                .render(guiGraphics);

        matrixStack.popPose();
    }
}
