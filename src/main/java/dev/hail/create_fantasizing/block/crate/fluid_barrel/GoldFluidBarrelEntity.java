package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class GoldFluidBarrelEntity extends AbstractFluidBarrelEntity implements MenuProvider {
    public GoldFluidBarrelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        singleTankCapacity = 48000;
        tankInventory.setCapacity(48000);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CFABlocks.GOLD_FLUID_BARREL_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.itemCapability == null)
                        return null;
                    return be.itemCapability.getCapability();
                });
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CFABlocks.GOLD_FLUID_BARREL_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.fluidCapability == null)
                        return null;
                    return be.fluidCapability.getCapability();
                });
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return GoldFluidBarrelMenu.create(i, inventory, this);
    }
}