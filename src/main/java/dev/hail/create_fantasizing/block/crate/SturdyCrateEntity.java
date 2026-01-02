package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.CFABlocks;
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
public class SturdyCrateEntity extends AbstractCrateEntity implements MenuProvider {
    public SturdyCrateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new CrateInventory(this, 50);
        inventory.allowedAmount = 3200;
        invHandler = ResetableLazy.of(() -> inventory);
    }

    @Override
    public @Nullable SturdyCrateMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return SturdyCrateMenu.create(i, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.sturdy_crate");
    }

    @Override
    public AbstractCrateEntity getOtherCrate(){
        if (!CFABlocks.STURDY_CRATE.has(getBlockState()))
            return null;
        else
            return super.getOtherCrate();
    }
}