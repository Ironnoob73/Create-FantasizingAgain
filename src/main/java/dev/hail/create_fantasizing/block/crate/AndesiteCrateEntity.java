package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.utility.ResetableLazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AndesiteCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public AndesiteCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new CrateInventory(this, 16);
        inventory.allowedAmount = 1024;
        invHandler = ResetableLazy.of(() -> inventory);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (isItemHandlerCap(cap)) {
            initCapability();
            if (!itemCapability.isPresent())
                return LazyOptional.empty();
            return itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.@NotNull Inventory inventory, @NotNull Player player) {
        return AndesiteCrateMenu.create(i, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.andesite_crate");
    }

    @Override
    public AbstractCrateEntity getOtherCrate(){
        if (!CFABlocks.ANDESITE_CRATE.has(getBlockState()))
            return null;
        else
            return super.getOtherCrate();
    }
}
