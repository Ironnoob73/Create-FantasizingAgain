package dev.hail.create_fantasizing.data;

import dev.hail.create_fantasizing.FantasizingMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CFATags {
    public static final TagKey<Block> RENEWABLE_BLOCKS = TagKey.create(Registries.BLOCK,
            FantasizingMod.resourceLocation("renewable_blocks"));

    public static final TagKey<Block> CRATE_MOUNTED_STORAGE = TagKey.create(Registries.BLOCK,
            FantasizingMod.resourceLocation("crate_mounted_storage"));

    public static final TagKey<Fluid> POWDER_SNOW = TagKey.create(Registries.FLUID,
            FantasizingMod.resourceLocation("powder_snow"));
}
