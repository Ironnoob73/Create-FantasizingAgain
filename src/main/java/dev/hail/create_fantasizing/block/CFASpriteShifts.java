package dev.hail.create_fantasizing.block;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import dev.hail.create_fantasizing.FantasizingMod;

public class CFASpriteShifts {
    public static final CTSpriteShiftEntry STURDY_GIRDER_POLE = vertical("sturdy_girder_pole_side");
    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }
    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, FantasizingMod.resourceLocation("block/" + blockTextureName),
                FantasizingMod.resourceLocation("block/" + connectedTextureName + "_connected"));
    }
    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }
    public static void init() {
    }
}
