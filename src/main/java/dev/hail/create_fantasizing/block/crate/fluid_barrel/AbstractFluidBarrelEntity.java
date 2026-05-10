package dev.hail.create_fantasizing.block.crate.fluid_barrel;

import com.simibubi.create.foundation.ICapabilityProvider;
import com.simibubi.create.foundation.utility.ResetableLazy;
import dev.hail.create_fantasizing.block.crate.AbstractCrateEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class AbstractFluidBarrelEntity extends AbstractCrateEntity {
    protected ICapabilityProvider<IFluidHandler> fluidCapability = null;
    protected ResetableLazy<IFluidHandler> tankHandler;
    public AbstractFluidBarrelEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
