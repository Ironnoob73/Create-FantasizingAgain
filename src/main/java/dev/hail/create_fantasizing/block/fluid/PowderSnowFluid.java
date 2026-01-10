package dev.hail.create_fantasizing.block.fluid;

import com.simibubi.create.AllFluids;
import com.tterrag.registrate.builders.FluidBuilder;
import net.createmod.catnip.theme.Color;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class PowderSnowFluid extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return CFAFluids.FLOWING_POWDER_SNOW.get();
    }
    @Override
    public Fluid getSource() {
        return CFAFluids.POWDER_SNOW.get();
    }
    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }
    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockentity = blockState.hasBlockEntity() ? levelAccessor.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, levelAccessor, blockPos, blockentity);
    }
    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 0;
    }
    @Override
    protected int getDropOff(LevelReader levelReader) {
        return 0;
    }
    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }
    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 0;
    }
    @Override
    protected float getExplosionResistance() {
        return 0;
    }
    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return Blocks.POWDER_SNOW.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return fluidState.isSource();
    }
    @Override
    public Item getBucket() {
        return Items.POWDER_SNOW_BUCKET;
    }

    @Override
    public void tick(Level level, BlockPos pos, FluidState state) {
        if (!level.isClientSide){
            if(state.isSource())
                level.setBlock(pos, Blocks.POWDER_SNOW.defaultBlockState(), 11);
            else
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        }
    }

    @Override
    public FluidType getFluidType() {
        return CFAFluids.POWDER_SNOW_TYPE.get();
    }

    public static class Flowing extends PowderSnowFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
    public static class Source extends PowderSnowFluid {
        public int getAmount(FluidState state) {
            return 8;
        }
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class PowderSnowFluidType extends AllFluids.TintedFluidType {

        private Vector3f fogColor;
        private Supplier<Float> fogDistance;

        public static FluidBuilder.FluidTypeFactory create(int fogColor, Supplier<Float> fogDistance) {
            return (p, s, f) -> {
                PowderSnowFluidType fluidType = new PowderSnowFluidType(p, s, f);
                fluidType.fogColor = new Color(fogColor, false).asVectorF();
                fluidType.fogDistance = fogDistance;
                return fluidType;
            };
        }

        public PowderSnowFluidType(Properties properties, ResourceLocation stillTexture,
                                                ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        }

        @Override
        protected int getTintColor(FluidStack stack) {
            return NO_TINT;
        }

        @Override
        public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
            return 0x00ffffff;
        }

        @Override
        protected Vector3f getCustomFogColor() {
            return fogColor;
        }

        @Override
        protected float getFogDistanceModifier() {
            return fogDistance.get();
        }

    }
}
