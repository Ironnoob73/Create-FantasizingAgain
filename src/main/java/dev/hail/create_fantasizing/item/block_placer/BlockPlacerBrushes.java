package dev.hail.create_fantasizing.item.block_placer;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.lang.Lang;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BlockPlacerBrushes {
    Cuboid(new BPCuboidBrush()),
    Sphere(new BPSphereBrush()),
    Cylinder(new BPCylinderBrush()),
    Surface(new BPDynamicBrush(true)),
    Cluster(new BPDynamicBrush(false));

    private BPBrush brush;

    BlockPlacerBrushes(BPBrush brush) {
        this.brush = brush;
    }

    public BPBrush get() {
        return brush;
    }

}
