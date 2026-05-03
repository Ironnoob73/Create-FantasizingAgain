package dev.hail.create_fantasizing.integration.encased;

import net.neoforged.fml.loading.LoadingModList;

public class EncasedHelper {
    public static final String MOD_ID = "createcasing";
    public static final boolean IS_LOADED = LoadingModList.get().getModFileById(MOD_ID) != null;
}
