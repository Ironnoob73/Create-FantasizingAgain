package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageBlock;
import dev.hail.create_fantasizing.block.crate.AbstractDoubleStorageEntity;
import net.createmod.catnip.annotations.ClientOnly;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.Mth.ceil;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractFluidBarrelBlock extends AbstractDoubleStorageBlock{
    SoundEvent soundEvent;

    public AbstractFluidBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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
    public void onMerge(AbstractDoubleStorageEntity be, AbstractDoubleStorageEntity other){
        super.onMerge(be, other);
        if (be instanceof AbstractFluidBarrelEntity mainBarrel && other instanceof AbstractFluidBarrelEntity secondaryBarrel){
            be.invalidateCapabilities();
            other.invalidateCapabilities();
            if (mainBarrel.tankInventory.getFluid().getFluid() != secondaryBarrel.tankInventory.getFluid().getFluid()
                    && !mainBarrel.tankInventory.isEmpty() && !secondaryBarrel.tankInventory.isEmpty()) {
                Level level = be.getLevel();
                if (level != null) {
                    level.setBlock(be.getBlockPos(), be.getBlockState().setValue(CRATE_TYPE, CrateType.SINGLE), 3);
                    level.setBlock(other.getBlockPos(), other.getBlockState().setValue(CRATE_TYPE, CrateType.SINGLE), 3);
                }
                return;
            }
            if (mainBarrel.tankInventory.getFluid().getFluid() == secondaryBarrel.tankInventory.getFluid().getFluid()
                    || mainBarrel.tankInventory.isEmpty() || secondaryBarrel.tankInventory.isEmpty()){
                if (mainBarrel.tankInventory.isEmpty() && !secondaryBarrel.tankInventory.isEmpty()){
                    mainBarrel.tankInventory.setFluid(secondaryBarrel.tankInventory.getFluid());
                }
                mainBarrel.tankInventory.getFluid().setAmount(mainBarrel.tankInventory.getFluid().getAmount() + secondaryBarrel.tankInventory.getFluid().getAmount());
                mainBarrel.tankInventory.setCapacity(mainBarrel.tankInventory.getCapacity() + secondaryBarrel.tankInventory.getCapacity());
            }
            for (int i = 0; i <= 1; i++)
                if (!secondaryBarrel.bucketSlots.getStackInSlot(i).isEmpty())
                    if (!mainBarrel.bucketSlots.getStackInSlot(i).isEmpty() && secondaryBarrel.getLevel() != null)
                        popResource(secondaryBarrel.getLevel(), secondaryBarrel.getBlockPos(), secondaryBarrel.bucketSlots.getStackInSlot(i));
                    else
                        mainBarrel.bucketSlots.setStackInSlot(i, secondaryBarrel.bucketSlots.getStackInSlot(i));
            be.notifyUpdate();
            other.notifyUpdate();
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof AbstractFluidBarrelEntity abstractFluidBarrelEntity) {
            ItemStack itemstack = new ItemStack(blockState.getBlock());
            if (abstractFluidBarrelEntity.getLevel() != null) {
                abstractFluidBarrelEntity.saveToItem(itemstack, abstractFluidBarrelEntity.getLevel().registryAccess());
                CompoundTag copiedComp = Objects.requireNonNull(itemstack.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag();
                Tag bucket_slots = copiedComp.getCompound("Buckets").get("Items");
                if (!abstractFluidBarrelEntity.tankInventory.isEmpty() || !abstractFluidBarrelEntity.bucketSlots.isEmpty()) {
                    if (abstractFluidBarrelEntity.isSecondaryCrate()){
                        copiedComp.getCompound("Tank").putInt("amount", Math.max(0, copiedComp.getCompound("Tank").getInt("amount") - abstractFluidBarrelEntity.singleTankCapacity));
                        copiedComp.putInt("Capacity", Math.max(0, copiedComp.getInt("Capacity") - abstractFluidBarrelEntity.singleTankCapacity));
                    }
                    else{
                        copiedComp.getCompound("Tank").putInt("amount", Math.min(abstractFluidBarrelEntity.singleTankCapacity, copiedComp.getCompound("Tank").getInt("amount")));
                        copiedComp.putInt("Capacity", Math.min(abstractFluidBarrelEntity.singleTankCapacity, copiedComp.getInt("Capacity")));
                    }
                    copiedComp.putInt("MaxCapacity", abstractFluidBarrelEntity.singleTankCapacity);
                    List<ItemStack> dropList = new ArrayList<>(List.of());
                    if (bucket_slots instanceof ListTag && !((ListTag) bucket_slots).isEmpty()){
                        Iterator<Tag> iterator = ((ListTag) bucket_slots).iterator();
                        while (iterator.hasNext()){
                            ItemStack stack = ItemStack.parseOptional(abstractFluidBarrelEntity.getLevel().registryAccess(), (CompoundTag) iterator.next());
                            if (abstractFluidBarrelEntity.getOtherCrate() == null){
                                dropList.add(stack);
                            }
                            iterator.remove();
                        }
                    }
                    if (abstractFluidBarrelEntity.hasCustomName())
                        itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable(blockState.getBlock().getDescriptionId()).setStyle(Style.EMPTY.withItalic(false)).append(" - ").append(Objects.requireNonNull(abstractFluidBarrelEntity.getCustomName()).copy().withStyle(ChatFormatting.ITALIC)));
                    else
                        itemstack.remove(DataComponents.CUSTOM_NAME);
                    itemstack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(copiedComp));
                    dropList.add(itemstack);
                    return dropList;
                }
            }
        }
        return super.getDrops(blockState, builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            CompoundTag copiedComp = Objects.requireNonNull(stack.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag();
            CompoundTag tank_inv = copiedComp.getCompound("Tank");
            SmartFluidTank tankInventory = new SmartFluidTank(0, (fluidStack -> {}));
            tankInventory.setCapacity(copiedComp.getInt("Capacity"));
            tankInventory.setFluid(FluidStack.parseOptional(Objects.requireNonNull(context.registries()), tank_inv));
            int maxCapacity = copiedComp.getInt("MaxCapacity");
            tooltipComponents.add(AbstractFluidBarrelEntity.barComponent(tankInventory, maxCapacity));
            tooltipComponents.addAll(AbstractFluidBarrelEntity.contentList(tankInventory, tooltipFlag.hasShiftDown()));
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof AbstractFluidBarrelEntity) {
            AbstractFluidBarrelEntity flexCrateBlockEntity = (AbstractFluidBarrelEntity) ((AbstractDoubleStorageEntity) be).getMainCrate();
            if (!flexCrateBlockEntity.tankInventory.isEmpty())
                return ceil(flexCrateBlockEntity.tankInventory.getFluidAmount() * 15f / (flexCrateBlockEntity.singleTankCapacity * ((flexCrateBlockEntity.isDoubleCrate() ? 2 : 1))));
        }
        return 0;
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
