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

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CopperFluidBarrelEntity extends AbstractFluidBarrelEntity implements MenuProvider {
    public CopperFluidBarrelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        singleTankCapacity = 8000;
        allowedCapacity = 8000;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CFABlocks.COPPER_FLUID_BARREL_ENTITY.get(),
                (be, context) -> {
                    be.initCapability();
                    if (be.fluidCapability == null)
                        return null;
                    return be.fluidCapability.getCapability();
                });
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return CopperFluidBarrelMenu.create(i, inventory, this);
    }
}
