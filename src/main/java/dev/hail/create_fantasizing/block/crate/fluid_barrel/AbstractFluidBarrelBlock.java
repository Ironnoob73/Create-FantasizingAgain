package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageBlock;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageEntity;
import net.createmod.catnip.annotations.ClientOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class AbstractFluidBarrelBlock extends AbstractDoubleStorageBlock{
    SoundEvent soundEvent;

    public AbstractFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        super.useItemOn(stack,state,level,pos,player,hand,hitResult);

        return onBlockEntityUseItemOn(level, pos, be -> {
            if (!stack.isEmpty() && be instanceof AbstractFluidBarrelEntity fluidBarrelEntity) {
                ItemInteractionResult tryExchange = tryExchange(level, player, hand, stack, fluidBarrelEntity);
                if (tryExchange.consumesAction()){
                    if (soundEvent != null && level.isClientSide())
                        playSound(soundEvent, level, fluidBarrelEntity);
                    soundEvent = null;
                    return tryExchange;
                }
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
                if (mainBarrel.tankInventory.isEmpty() && !secondaryBarrel.tankInventory.isEmpty()){
                    mainBarrel.tankInventory.setFluid(secondaryBarrel.tankInventory.getFluid());
                    secondaryBarrel.tankInventory.setFluid(FluidStack.EMPTY);
                }
                mainBarrel.tankInventory.getFluid().setAmount(mainBarrel.tankInventory.getFluid().getAmount() + secondaryBarrel.tankInventory.getFluid().getAmount());
                mainBarrel.allowedCapacity = (mainBarrel.tankInventory.isEmpty() ? mainBarrel.singleTankCapacity : mainBarrel.allowedCapacity)
                        + (secondaryBarrel.tankInventory.isEmpty() ? secondaryBarrel.singleTankCapacity : secondaryBarrel.allowedCapacity);
            }
        }
    }

    protected ItemInteractionResult tryExchange(Level worldIn, Player player, InteractionHand handIn, ItemStack heldItem,
                                                AbstractFluidBarrelEntity be) {
        if (FluidHelper.tryEmptyItemIntoBE(worldIn, player, handIn, heldItem, be)){
            soundEvent = FluidHelper.getEmptySound(Objects.requireNonNull(be.fluidCapability.getCapability()).getFluidInTank(0));
            return ItemInteractionResult.SUCCESS;
        }
        else if (FluidHelper.tryFillItemFromBE(worldIn, player, handIn, heldItem, be)){
            soundEvent = FluidHelper.getFillSound(Objects.requireNonNull(be.fluidCapability.getCapability()).getFluidInTank(0));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @ClientOnly
    protected void playSound(SoundEvent soundevent, Level level, AbstractFluidBarrelEntity be){
        float pitch = Mth
                .clamp(1 - (1f * Objects.requireNonNull(be.fluidCapability.getCapability()).getFluidInTank(0).getAmount() / (FluidTankBlockEntity.getCapacityMultiplier() * 16)), 0, 1);
            pitch /= 1.5f;
            pitch += .5f;
            pitch += (level.random.nextFloat() - .5f) / 4f;
            BlockPos blockPos = be.getBlockPos();
            level.playLocalSound(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, soundevent, SoundSource.BLOCKS, .5f, pitch, true);
    }
}
