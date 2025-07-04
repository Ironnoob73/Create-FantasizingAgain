package dev.hail.create_fantasizing.item;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.saw.TreeCutter;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.AbstractBlockBreakQueue;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TreeCutterItem extends AxeItem {
    protected BlockPos breakingPos;
    protected Level level;
    protected BlockPos worldPosition;
    public TreeCutterItem(Properties properties) {
        super(STURDY,0,-3,properties);
    }
    public static final Tier STURDY = new Tier() {
        @Override public int getUses() {return 2048;}
        @Override public float getSpeed() {return 9;}
        @Override public float getAttackDamageBonus() {return 9;}
        @Override public int getLevel() { return 4;}
        @Override public int getEnchantmentValue() {return 15;}
        @Override public Ingredient getRepairIngredient() {return Ingredient.of(AllItems.STURDY_SHEET);}
    };
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new TreeCutterItemRenderer()));
    }
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if(state.is(BlockTags.LOGS) && !miningEntity.isCrouching()){
            this.level = level;
            this.breakingPos = pos;
            this.worldPosition = pos;
            Optional<AbstractBlockBreakQueue> dynamicTree = TreeCutter.findDynamicTree(state.getBlock(), breakingPos);
            if (dynamicTree.isPresent()) {
                dynamicTree.get().destroyBlocks(level, null, this::dropItemFromCutTree);
                return true;
            }
            TreeCutter.findTree(level, breakingPos, state).destroyBlocks(level, null, this::dropItemFromCutTree);
        }
        return super.mineBlock(stack,level,state,pos,miningEntity);
    }
    public void dropItemFromCutTree(BlockPos pos, ItemStack stack) {
        float distance = (float) Math.sqrt(pos.distSqr(breakingPos));
        Vec3 dropPos = VecHelper.getCenterOf(pos);
        ItemEntity entity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, stack);
        entity.setDeltaMovement(Vec3.atLowerCornerOf(breakingPos.subtract(this.worldPosition))
                .scale(distance / 20f));
        level.addFreshEntity(entity);
    }
}
