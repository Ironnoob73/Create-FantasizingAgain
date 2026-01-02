package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.utility.ResetableLazy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IronCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public IronCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new CrateInventory(this, 20);
        inventory.allowedAmount = 1280;
        invHandler = ResetableLazy.of(() -> inventory);
    }

    @Override
    public @Nullable IronCrateMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return IronCrateMenu.create(i, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.iron_crate");
    }
}
