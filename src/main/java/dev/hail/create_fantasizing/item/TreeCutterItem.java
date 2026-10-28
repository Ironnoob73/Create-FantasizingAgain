package dev.hail.create_fantasizing.item;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import dev.hail.create_fantasizing.data.CFATriggers;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TreeCutterItem extends AxeItem {
    protected BlockPos breakingPos;
    protected Level level;
    protected BlockPos worldPosition;
    protected int logsDropped;
    public TreeCutterItem(Properties properties) {
        super(STURDY,properties);
    }
    public static final Tier STURDY = new Tier() {
        @Override public int getUses() {return 2048;}
        @Override public float getSpeed() {return 9;}
        @Override public float getAttackDamageBonus() {return 9;}
        @Override public TagKey<Block> getIncorrectBlocksForDrops() {return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;}
        @Override public int getEnchantmentValue() {return 15;}
        @Override public Ingredient getRepairIngredient() {return Ingredient.of(AllItems.STURDY_SHEET);}
    };
    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("removal")
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new TreeCutterItemRenderer()));
    }
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if(state.is(BlockTags.LOGS) && !miningEntity.isCrouching()){
            this.level = level;
            this.breakingPos = pos;
            this.worldPosition = pos;
            this.logsDropped = 0;

            boolean felledTree = false;
            Optional<AbstractBlockBreakQueue> dynamicTree = TreeCutter.findDynamicTree(state.getBlock(), breakingPos);
            if (dynamicTree.isPresent()) {
                felledTree = true;
                dynamicTree.get().destroyBlocks(level, null, this::dropItemFromCutTree);
            } else {
                TreeCutter.findTree(level, breakingPos, state).destroyBlocks(level, null, this::dropItemFromCutTree);
                felledTree = logsDropped > 0;
            }

            if (felledTree && !level.isClientSide && miningEntity instanceof ServerPlayer player) {
                // The initially broken log itself is handled by vanilla and is not part
                // of the TreeCutter drop callback, so count it as well.
                int totalLogs = logsDropped + 1;
                CFATriggers.TREE_CUTTER_FELLED_TREE.get().trigger(player);
                if (totalLogs > 128)
                    CFATriggers.TREE_CUTTER_FELLED_LARGE_TREE.get().trigger(player);
            }

            this.logsDropped = 0;
            if (dynamicTree.isPresent())
                return true;
        }
        return super.mineBlock(stack,level,state,pos,miningEntity);
    }
    public void dropItemFromCutTree(BlockPos pos, ItemStack stack) {
        if (level != null && !level.isClientSide && stack.is(ItemTags.LOGS))
            logsDropped += stack.getCount();
        float distance = (float) Math.sqrt(pos.distSqr(breakingPos));
        Vec3 dropPos = VecHelper.getCenterOf(pos);
        ItemEntity entity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, stack);
        entity.setDeltaMovement(Vec3.atLowerCornerOf(breakingPos.subtract(this.worldPosition))
                .scale(distance / 20f));
        level.addFreshEntity(entity);
    }
}
