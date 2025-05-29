package dev.hail.create_fantasizing.item.block_placer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.equipment.zapper.ZapperItemRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.hail.create_fantasizing.FantasizingMod;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import static java.lang.Math.max;

public class BlockPlacerItemRenderer extends ZapperItemRenderer {
    protected static final PartialModel ROD = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_rod"));
    protected static final PartialModel CORE = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_core"));
    protected static final PartialModel CORE_GLOW = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_core_glow"));
    protected static final PartialModel ACCELERATOR = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_accelerator"));
    protected static final PartialModel RING_0 = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_ring_0"));
    protected static final PartialModel RING_1 = PartialModel.of(FantasizingMod.resourceLocation("item/block_placer_ring_1"));

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType,
                          PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.render(stack, model, renderer, transformType, ms, buffer, light, overlay);

        float pt = AnimationTickHolder.getPartialTicks();
        float worldTime = AnimationTickHolder.getRenderTime() / 20;

        renderer.renderSolid(model.getOriginalModel(), light);
        int normal_glow = LightTexture.pack(15,15);
        renderer.renderSolid(ROD.get(), normal_glow);

        LocalPlayer player = Minecraft.getInstance().player;
        boolean leftHanded = player.getMainArm() == HumanoidArm.LEFT;
        boolean mainHand = player.getMainHandItem() == stack;
        boolean offHand = player.getOffhandItem() == stack;
        float animation = getAnimationProgress(pt, leftHanded, mainHand);

        // Core glows
        float multiplier;
        if (mainHand || offHand)
            multiplier = animation;
        else
            multiplier = Mth.sin(worldTime * 5);

        int lightItensity = (int) (15 * Mth.clamp(multiplier, 0, 1));
        int glowLight = LightTexture.pack(lightItensity, max(lightItensity, 4));
        renderer.renderSolidGlowing(CORE.get(), glowLight);
        renderer.renderGlowing(CORE_GLOW.get(), glowLight);

        // Accelerator spins
        float angle = worldTime * -25;
        if (mainHand || offHand)
            angle += 360 * animation;

        angle %= 360;
        ms.translate(0, -0.125, 0.35);
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        renderer.renderSolid(ACCELERATOR.get(), normal_glow);
        ms.mulPose(Axis.ZP.rotationDegrees(angle));
        renderer.renderSolidGlowing(RING_0.get(), normal_glow);
        ms.mulPose(Axis.XP.rotationDegrees(angle));
        renderer.renderSolidGlowing(RING_1.get(), normal_glow);
    }

}
