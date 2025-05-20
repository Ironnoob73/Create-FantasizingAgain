package dev.hail.create_fantasizing.item.BlockPlacer;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BlockPlacerBrushes implements StringRepresentable {
    Cuboid(new BPCuboidBrush()),
    Sphere(new BPSphereBrush()),
    Cylinder(new BPCylinderBrush()),
    Surface(new BPDynamicBrush(true)),
    Cluster(new BPDynamicBrush(false));

    public static final Codec<BlockPlacerBrushes> CODEC = StringRepresentable.fromValues(BlockPlacerBrushes::values);
    public static final StreamCodec<ByteBuf, BlockPlacerBrushes> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(BlockPlacerBrushes.class);

    private BPBrush brush;

    BlockPlacerBrushes(BPBrush brush) {
        this.brush = brush;
    }

    public BPBrush get() {
        return brush;
    }

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }
}
