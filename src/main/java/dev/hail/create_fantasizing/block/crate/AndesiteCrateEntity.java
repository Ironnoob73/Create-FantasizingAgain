package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryWrapper;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.CFABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                (be, context) -> {
                    be.initCapability();
                    if (be.itemCapability == null)
                        return null;
                    return be.itemCapability.getCapability();
                });
    }
    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return AndesiteCrateMenu.create(i, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.create_fantasizing.andesite_crate");
    }

    @Override
    public void initCapability() {
        super.initCapability();
        if (isController()) return;
        if (isDoubleCrate() && level != null){
            IItemHandlerModifiable[] invs = new IItemHandlerModifiable[2];
            AndesiteCrateEntity crate0At = ConnectivityHandler.partAt(CFABlocks.ANDESITE_CRATE_ENTITY.get(), level, getMainCrate().getBlockPos());
            invs[0] = crate0At != null ? crate0At.inventory : new ItemStackHandler();
            AndesiteCrateEntity crate1At = ConnectivityHandler.partAt(CFABlocks.ANDESITE_CRATE_ENTITY.get(), level, getOtherCrate().getBlockPos());
            invs[1] = crate1At != null ? crate1At.inventory : new ItemStackHandler();
            itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper(new CombinedInvWrapper(invs)));
        }else{
            itemCapability = ICapabilityProvider.of(new VersionedInventoryWrapper(new CombinedInvWrapper(inventory)));
        }
    }

    @Override
    public boolean isCrate(BlockState state) {
        return CFABlocks.ANDESITE_CRATE.has(state);
    }
}
