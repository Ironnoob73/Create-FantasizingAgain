package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageBlock;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractFluidBarrelBlock extends AbstractDoubleStorageBlock{
    public AbstractFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        super.useItemOn(stack,state,level,pos,player,hand,hitResult);

        //if (stack.getItem() instanceof BlockItem && stack.getCapability(Capabilities.FluidHandler.ITEM) == null)
        //    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        return onBlockEntityUseItemOn(level, pos, be -> {
            if (!stack.isEmpty() && be instanceof AbstractFluidBarrelEntity fluidBarrelEntity) {
                ItemInteractionResult tryExchange = tryExchange(level, player, hand, stack, fluidBarrelEntity);
                if (tryExchange.consumesAction())
                    return tryExchange;
            }
            withBlockEntityDo(level, pos, crate -> player.openMenu(crate.getMainCrate(), crate.getMainCrate()::sendToMenu));
            return ItemInteractionResult.SUCCESS;
        });
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return ComparatorUtil.levelOfSmartFluidTank(worldIn, pos);
    }

    @Override
    public void onMerge(AbstractDoubleStorageEntity be, AbstractDoubleStorageEntity other){
        super.onMerge(be, other);
        if (be instanceof AbstractFluidBarrelEntity mainBarrel && other instanceof AbstractFluidBarrelEntity secondaryBarrel){
            other.invalidateCapabilities();
            if (mainBarrel.tankInventory.getFluid().getFluid() == secondaryBarrel.tankInventory.getFluid().getFluid()
                    || mainBarrel.tankInventory.isEmpty() || secondaryBarrel.tankInventory.isEmpty()){
                if (mainBarrel.tankInventory.isEmpty() && !secondaryBarrel.tankInventory.isEmpty())
                    mainBarrel.tankInventory.setFluid(secondaryBarrel.tankInventory.getFluid());
                mainBarrel.tankInventory.getFluid().setAmount(mainBarrel.tankInventory.getFluid().getAmount() + secondaryBarrel.tankInventory.getFluid().getAmount());
            }
        }
    }

    protected ItemInteractionResult tryExchange(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
                                                AbstractFluidBarrelEntity be) {
        if (FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be))
            return ItemInteractionResult.SUCCESS;
        if (GenericItemEmptying.canItemBeEmptied(worldIn, heldItem))
            return ItemInteractionResult.SUCCESS;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
