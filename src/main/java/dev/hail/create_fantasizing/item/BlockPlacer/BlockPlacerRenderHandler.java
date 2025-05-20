package dev.hail.create_fantasizing.item.BlockPlacer;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class BlockPlacerRenderHandler {
    private static Supplier<Collection<BlockPos>> renderedPositions;

    public static void tick() {
        gatherSelectedBlocks();
        if (renderedPositions == null)
            return;

        Outliner.getInstance().showCluster("blockPlacer", renderedPositions.get())
                .colored(0xbfbfbf)
                .disableLineNormals()
                .lineWidth(1 / 32f)
                .withFaceTexture(AllSpecialTextures.CHECKERED);
    }

    protected static void gatherSelectedBlocks() {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;
        ItemStack heldMain = player.getMainHandItem();
        ItemStack heldOff = player.getOffhandItem();
        boolean zapperInMain = CFAItems.BLOCK_PLACER.get() == heldMain.getItem();
        boolean zapperInOff = CFAItems.BLOCK_PLACER.get() == heldOff.getItem();

        if (zapperInMain) {
            if (!heldMain.has(AllDataComponents.SHAPER_SWAP) || !zapperInOff) {
                createBrushOutline(player, heldMain);
                return;
            }
        }

        if (zapperInOff) {
            createBrushOutline(player, heldOff);
            return;
        }

        renderedPositions = null;
    }

    public static void createBrushOutline(LocalPlayer player, ItemStack zapper) {
        if (!zapper.has(AllDataComponents.SHAPER_BRUSH_PARAMS)) {
            renderedPositions = null;
            return;
        }

        Brush brush = zapper.getOrDefault(AllDataComponents.SHAPER_BRUSH, TerrainBrushes.Cuboid).get();
        PlacementOptions placement = zapper.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, PlacementOptions.Merged);
        TerrainTools tool = zapper.getOrDefault(AllDataComponents.SHAPER_TOOL, TerrainTools.Fill);
        BlockPos params = zapper.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
        brush.set(params.getX(), params.getY(), params.getZ());

        Vec3 start = player.position()
                .add(0, player.getEyeHeight(), 0);
        Vec3 range = player.getLookAngle()
                .scale(128);
        BlockHitResult raytrace = player.level()
                .clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (raytrace == null || raytrace.getType() == HitResult.Type.MISS) {
            renderedPositions = null;
            return;
        }

        BlockPos pos = raytrace.getBlockPos()
                .offset(brush.getOffset(player.getLookAngle(), raytrace.getDirection(), placement));
        renderedPositions =
                () -> brush.addToGlobalPositions(player.level(), pos, raytrace.getDirection(), new ArrayList<>(), tool);
    }

}
