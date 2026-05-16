package dev.hail.create_fantasizing.block.crate;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SturdyCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public SturdyCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new CrateInventory(this, 50);
        inventory.allowedAmount = 3200;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CFABlocks.STURDY_CRATE_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.itemCapability == null)
                        return null;
                    return be.itemCapability.getCapability();
                });
    }
    @Override
    public @Nullable SturdyCrateMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return SturdyCrateMenu.create(i, inventory, this);
    }
}