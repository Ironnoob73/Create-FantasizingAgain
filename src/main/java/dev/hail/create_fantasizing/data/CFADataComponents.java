package dev.hail.create_fantasizing.data;

import com.mojang.serialization.Codec;
import dev.hail.create_fantasizing.FantasizingMod;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerBrushes;
import dev.hail.create_fantasizing.item.block_placer.BlockPlacerTools;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

public class CFADataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FantasizingMod.MOD_ID);

    public static final DataComponentType<BlockPlacerBrushes> SHAPER_BRUSH = register(
            "shaper_brush", builder -> builder.persistent(BlockPlacerBrushes.CODEC).networkSynchronized(BlockPlacerBrushes.STREAM_CODEC));
    public static final DataComponentType<BlockPlacerTools> SHAPER_TOOL = register(
            "shaper_tool", builder -> builder.persistent(BlockPlacerTools.CODEC).networkSynchronized(BlockPlacerTools.STREAM_CODEC));
    public static final DataComponentType<Integer> BLOCK_AMOUNT = register(
            "block_amount", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DataComponentType<Integer> PLACE_SIZE = register(
            "place_size", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }
    @ApiStatus.Internal
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
