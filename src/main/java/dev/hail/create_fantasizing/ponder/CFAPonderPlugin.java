package dev.hail.create_fantasizing.ponder;

import dev.hail.create_fantasizing.FantasizingMod;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CFAPonderPlugin implements PonderPlugin {
    @Override
    public @NotNull String getModId() {
        return FantasizingMod.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CFAPonderScenes.register(helper);
    }
}
