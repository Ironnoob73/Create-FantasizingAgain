package dev.hail.create_fantasizing.block.crate;

import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class AbstractCrateBlock extends AbstractDoubleStorageBlock {

    public AbstractCrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                           BlockHitResult hit) {
        super.useItemOn(stack,state,worldIn,pos,player,handIn,hit);
        withBlockEntityDo(worldIn, pos, crate -> player.openMenu(crate.getMainCrate(), crate.getMainCrate()::sendToMenu));
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof AbstractCrateEntity abstractCrateEntity) {
            ItemStack itemstack = new ItemStack(blockState.getBlock());
            if (abstractCrateEntity.getLevel() != null) {
                abstractCrateEntity.saveToItem(itemstack, abstractCrateEntity.getLevel().registryAccess());
                CompoundTag copiedComp = Objects.requireNonNull(itemstack.get(DataComponents.BLOCK_ENTITY_DATA)).copyTag();
                Tag crate_inv = copiedComp.getCompound("Inventory").get("Items");
                if (crate_inv instanceof ListTag && !((ListTag) crate_inv).isEmpty()) {
                    List<ItemStack> dropList = new ArrayList<>(List.of());
                    Iterator<Tag> iterator = ((ListTag) crate_inv).iterator();
                    while (iterator.hasNext()){
                        ItemStack stack = ItemStack.parseOptional(abstractCrateEntity.getLevel().registryAccess(), (CompoundTag) iterator.next());
                        if (stack.get(DataComponents.BLOCK_ENTITY_DATA) != null ||
                                stack.get(DataComponents.CONTAINER) != null ) {
                            dropList.add(stack);
                            iterator.remove();
                        }
                    }
                    copiedComp.getCompound("Inventory").put("Items", crate_inv);
                    copiedComp.putInt("AllowedAmount", Math.min(abstractCrateEntity.inventory.getSlots() * 64, copiedComp.getInt("AllowedAmount")));
                    if (abstractCrateEntity.hasCustomName())
                        itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable(blockState.getBlock().getDescriptionId()).setStyle(Style.EMPTY.withItalic(false)).append(" - ").append(Objects.requireNonNull(abstractCrateEntity.getCustomName()).copy().withStyle(ChatFormatting.ITALIC)));
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
            CompoundTag crate_inv = copiedComp.getCompound("Inventory");
            CrateInventory crateInventory = new CrateInventory(null, 0);
            crateInventory.deserializeNBT(Objects.requireNonNull(context.registries()), crate_inv);
            crateInventory.allowedAmount = copiedComp.getInt("AllowedAmount");
            tooltipComponents.add(AbstractCrateEntity.barComponent(crateInventory));
            tooltipComponents.addAll(AbstractCrateEntity.contentList(crateInventory));
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof AbstractCrateEntity) {
            AbstractCrateEntity flexCrateBlockEntity = (AbstractCrateEntity) ((AbstractDoubleStorageEntity) be).getMainCrate();
            if (flexCrateBlockEntity.itemCapability.getCapability() != null)
                return ItemHelper.calcRedstoneFromInventory(flexCrateBlockEntity.itemCapability.getCapability());
            return ItemHelper.calcRedstoneFromInventory(flexCrateBlockEntity.inventory);
        }
        return 0;
    }
}
