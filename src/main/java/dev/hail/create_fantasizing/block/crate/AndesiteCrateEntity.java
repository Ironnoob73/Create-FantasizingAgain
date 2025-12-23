package dev.hail.create_fantasizing.block.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AndesiteCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public AndesiteCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        invSize = 32;
        allowedAmount = 1024;
        itemCount = 10;
        inventory = new Inv();
        invHandler = LazyOptional.of(() -> inventory);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return AndesiteCrateMenu.create(id, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.andesite_crate");
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if ( isItemHandlerCap(capability)) {
            AbstractCrateEntity mainCrate = getMainCrate();
            if (mainCrate != null && mainCrate.invHandler != null && mainCrate.invHandler.isPresent())
                return mainCrate.invHandler.cast();
        }
        return super.getCapability(capability, facing);
    }
}
