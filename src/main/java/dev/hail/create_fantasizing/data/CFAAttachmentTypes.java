package dev.hail.create_fantasizing.data;

import dev.hail.create_fantasizing.FantasizingMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class CFAAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, FantasizingMod.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> FOLD_INTERFACE = ATTACHMENT_TYPES.register(
            "fold_interface", () -> AttachmentType.builder(() -> false).build()
    );

    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}