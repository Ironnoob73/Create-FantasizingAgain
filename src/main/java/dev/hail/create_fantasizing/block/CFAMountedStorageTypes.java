package dev.hail.create_fantasizing.block;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.hail.create_fantasizing.block.crate.CrateMountedStorageType;
import dev.hail.create_fantasizing.data.CFATags;

import static dev.hail.create_fantasizing.FantasizingMod.REGISTRATE;

public class CFAMountedStorageTypes {
    public static final RegistryEntry<MountedItemStorageType<?>, CrateMountedStorageType> CRATE =
            REGISTRATE.mountedItemStorage("crate", CrateMountedStorageType::new)
            .associateBlockTag(CFATags.CRATE_MOUNTED_STORAGE)
            .register();

    public static void register() {}
}
