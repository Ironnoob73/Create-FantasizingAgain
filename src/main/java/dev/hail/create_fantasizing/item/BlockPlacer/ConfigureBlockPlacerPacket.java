package dev.hail.create_fantasizing.item.BlockPlacer;

import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import dev.hail.create_fantasizing.event.CFAPackets;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ConfigureBlockPlacerPacket extends ConfigureZapperPacket {
    public static final StreamCodec<ByteBuf, ConfigureBlockPlacerPacket> STREAM_CODEC = CatnipLargerStreamCodecs.composite(
            CatnipStreamCodecs.HAND, packet -> packet.hand,
            PlacementPatterns.STREAM_CODEC, packet -> packet.pattern,
            BlockPlacerBrushes.STREAM_CODEC, packet -> packet.brush,
            ByteBufCodecs.VAR_INT, packet -> packet.brushParamX,
            ByteBufCodecs.VAR_INT, packet -> packet.brushParamY,
            ByteBufCodecs.VAR_INT, packet -> packet.brushParamZ,
            BlockPlacerTools.STREAM_CODEC, packet -> packet.tool,
            PlacementOptions.STREAM_CODEC, packet -> packet.placement,
            ConfigureBlockPlacerPacket::new
    );

    private final BlockPlacerBrushes brush;
    private final int brushParamX;
    private final int brushParamY;
    private final int brushParamZ;
    private final BlockPlacerTools tool;
    private final PlacementOptions placement;

    public ConfigureBlockPlacerPacket(InteractionHand hand, PlacementPatterns pattern, BlockPlacerBrushes brush,
                                      int brushParamX, int brushParamY, int brushParamZ,
                                      BlockPlacerTools tool, PlacementOptions placement) {
        super(hand, pattern);
        this.brush = brush;
        this.brushParamX = brushParamX;
        this.brushParamY = brushParamY;
        this.brushParamZ = brushParamZ;
        this.tool = tool;
        this.placement = placement;
    }

    @Override
    public void configureZapper(ItemStack stack) {
        BlockPlacerItem.configureSettings(stack, pattern, brush, brushParamX, brushParamY, brushParamZ, tool, placement);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CFAPackets.CONFIGURE_BLOCK_PLACER;
    }
}
