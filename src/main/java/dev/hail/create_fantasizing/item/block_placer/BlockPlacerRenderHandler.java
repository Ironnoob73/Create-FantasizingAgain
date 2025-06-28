package dev.hail.create_fantasizing.item.block_placer;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
        boolean zapperInMain = CFAItems.BLOCK_PLACER.isIn(heldMain);
        boolean zapperInOff = CFAItems.BLOCK_PLACER.isIn(heldOff);

        if (zapperInMain) {
            CompoundTag tag = heldMain.getOrCreateTag();
            if (!tag.contains("_Swap") || !zapperInOff) {
                createBrushOutline(tag, player, heldMain);
                return;
            }
        }

        if (zapperInOff) {
            CompoundTag tag = heldOff.getOrCreateTag();
            createBrushOutline(tag, player, heldOff);
            return;
        }

        renderedPositions = null;
    }

    public static void createBrushOutline(CompoundTag tag, LocalPlayer player, ItemStack zapper) {
        if (!tag.contains("BrushParams")) {
            renderedPositions = null;
            return;
        }

        Brush brush = NBTHelper.readEnum(tag, "Brush", TerrainBrushes.class).get();
        PlacementOptions placement = NBTHelper.readEnum(tag, "Placement", PlacementOptions.class);
        TerrainTools tool = NBTHelper.readEnum(tag, "Tool", TerrainTools.class);
        BlockPos params = NbtUtils.readBlockPos(tag.getCompound("BrushParams"));
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
