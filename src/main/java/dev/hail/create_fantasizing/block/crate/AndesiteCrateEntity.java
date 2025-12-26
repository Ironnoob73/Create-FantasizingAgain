package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AndesiteCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public AndesiteCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        invSize = 16;
        allowedAmount = 1024;
        itemCount = 10;
        inventory = new Inv();
        invHandler = ResetableLazy.of(() -> inventory);
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CFABlocks.ANDESITE_CRATE_ENTITY.get(),
                (be, context) -> be.inventory);
    }
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return AndesiteCrateMenu.create(id, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.andesite_crate");
    }
}
