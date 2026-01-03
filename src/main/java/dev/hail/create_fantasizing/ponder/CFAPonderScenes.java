package dev.hail.create_fantasizing.ponder;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CFAPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND)
                .addStoryBoard("alternative_chromatic_compound",AlternativeChromaticCompoundScene::convert);
    }
}
