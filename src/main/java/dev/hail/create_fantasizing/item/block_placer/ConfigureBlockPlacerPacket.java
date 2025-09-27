package dev.hail.create_fantasizing.item.block_placer;

import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ConfigureBlockPlacerPacket extends ConfigureZapperPacket {
    private final BlockPlacerBrushes brush;
    private final int brushParamX;
    private final int brushParamY;
    private final int brushParamZ;
    private final BlockPlacerTools tool;
    private final PlacementOptions placement;
    private final boolean destroyMode;

    public ConfigureBlockPlacerPacket(InteractionHand hand, PlacementPatterns pattern, BlockPlacerBrushes brush,
                                      int brushParamX, int brushParamY, int brushParamZ,
                                      BlockPlacerTools tool, PlacementOptions placement, boolean currentDestroyMode) {
        super(hand, pattern);
        this.brush = brush;
        this.brushParamX = brushParamX;
        this.brushParamY = brushParamY;
        this.brushParamZ = brushParamZ;
        this.tool = tool;
        this.placement = placement;
        this.destroyMode = currentDestroyMode;
    }

    public ConfigureBlockPlacerPacket(FriendlyByteBuf buffer) {
        super(buffer);
        brush = buffer.readEnum(BlockPlacerBrushes.class);
        brushParamX = buffer.readVarInt();
        brushParamY = buffer.readVarInt();
        brushParamZ = buffer.readVarInt();
        tool = buffer.readEnum(BlockPlacerTools.class);
        placement = buffer.readEnum(PlacementOptions.class);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        super.write(buffer);
        buffer.writeEnum(brush);
        buffer.writeVarInt(brushParamX);
        buffer.writeVarInt(brushParamY);
        buffer.writeVarInt(brushParamZ);
        buffer.writeEnum(tool);
        buffer.writeEnum(placement);
    }

    @Override
    public void configureZapper(ItemStack stack) {
        BlockPlacerItem.configureSettings(stack, pattern, brush, brushParamX, brushParamY, brushParamZ, tool, placement, destroyMode);
    }
}
