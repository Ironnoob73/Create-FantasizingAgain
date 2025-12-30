package dev.hail.create_fantasizing.block;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.hail.create_fantasizing.FantasizingMod;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.Map;

public class CFAPartialModels {
    public static final PartialModel COMPACT_HYDRAULIC_ENGINE_HEART = block("compact_hydraulic_engine_heart");
    public static final PartialModel COMPACT_WIND_ENGINE_CORE = block("compact_wind_engine_core");

    public static final PartialModel PHANTOM_SHAFT = block("phantom_shaft");

    public static final Map<Direction, PartialModel> STURDY_METAL_GIRDER_BRACKETS = new EnumMap<>(Direction.class);
    static {
        for (Direction d : Iterate.horizontalDirections) {
            STURDY_METAL_GIRDER_BRACKETS.put(d, block("sturdy_girder/bracket_" + Lang.asId(d.name())));
        }
    }
    private static PartialModel block(String path) {
        return PartialModel.of(FantasizingMod.resourceLocation("block/" + path));
    }
    public static void init() {}
}
