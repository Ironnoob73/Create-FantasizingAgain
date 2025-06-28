package dev.hail.create_fantasizing.item.block_placer;

import com.simibubi.create.content.equipment.zapper.ConfigureZapperPacket;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ConfigureBlockPlacerPacket extends ConfigureZapperPacket {
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
}
