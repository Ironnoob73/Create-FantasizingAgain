package dev.hail.create_fantasizing.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hail.create_fantasizing.FantasizingMod;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

/**
 * By Deepseek V4.1 Flash 1017:
 * Advancement criterion triggers added by Create: Fantasizing Again.
 */
public final class CFATriggers {

    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, FantasizingMod.MOD_ID);

    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> TRANSPORTER_ITEM_TRANSPORTED =
            register("transporter_item_transported");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> TREE_CUTTER_FELLED_TREE =
            register("tree_cutter_felled_tree");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> TREE_CUTTER_FELLED_LARGE_TREE =
            register("tree_cutter_felled_large_tree");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> PHANTOM_TRANSMISSION =
            register("phantom_transmission");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> CAPTURE_WARDEN =
            register("capture_warden");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> REFINED_RADIANCE_TUNNEL_PROCESSED =
            register("refined_radiance_tunnel_processed");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> SHADOW_STEEL_TUNNEL_PROCESSED =
            register("shadow_steel_tunnel_processed");
    public static final DeferredHolder<CriterionTrigger<?>, SimpleTrigger> CHROMATIC_TUNNEL_PUT_ON_DEPOT =
            register("chromatic_tunnel_put_on_depot");

    private CFATriggers() {
    }

    private static DeferredHolder<CriterionTrigger<?>, SimpleTrigger> register(String id) {
        return TRIGGERS.register(id, SimpleTrigger::new);
    }

    public static void register(IEventBus modEventBus) {
        TRIGGERS.register(modEventBus);
    }

    /**
     * Triggers the given trigger for every player near the supplied position.
     * Machines such as the Transporter and the Chromatic Tunnels have no owning
     * player, so nearby players are the intended audience for their advancements.
     */
    public static void triggerNearby(Level level, Vec3 center, double range, SimpleTrigger trigger) {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        double rangeSquared = range * range;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(center) <= rangeSquared)
                trigger.trigger(player);
        }
    }

    public static class SimpleTrigger extends SimpleCriterionTrigger<SimpleTrigger.TriggerInstance> {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player")
                                .forGetter(TriggerInstance::player))
                .apply(instance, TriggerInstance::new));

        @Override
        public Codec<TriggerInstance> codec() {
            return CODEC;
        }

        public void trigger(ServerPlayer player) {
            trigger(player, instance -> true);
        }

        public record TriggerInstance(Optional<ContextAwarePredicate> player)
                implements SimpleCriterionTrigger.SimpleInstance {
        }
    }
}
