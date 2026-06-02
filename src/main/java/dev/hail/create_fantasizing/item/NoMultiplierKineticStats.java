package dev.hail.create_fantasizing.item;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlock;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;

public class NoMultiplierKineticStats implements TooltipModifier {
    protected final Block block;

    public NoMultiplierKineticStats(Block block) {
        this.block = block;
    }

    @Nullable
    public static NoMultiplierKineticStats create(Item item) {
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof IRotate || block instanceof SteamEngineBlock) {
                return new NoMultiplierKineticStats(block);
            }
        }
        return null;
    }

    @Override
    public void modify(ItemTooltipEvent context) {
        List<Component> kineticStats = getNoMultiplierKineticStats(block, context.getEntity());
        if (!kineticStats.isEmpty()) {
            List<Component> tooltip = context.getToolTip();
            tooltip.add(CommonComponents.EMPTY);
            tooltip.addAll(kineticStats);
        }
    }

    public static List<Component> getNoMultiplierKineticStats(Block block, Player player) {
        List<Component> list = new ArrayList<>();

        CKinetics config = AllConfigs.server().kinetics;
        LangBuilder suUnit = CreateLang.translate("generic.unit.stress");

        boolean hasGoggles = GogglesItem.isWearingGoggles(player);

        boolean showStressImpact;
        if (block instanceof IRotate) {
            showStressImpact = !((IRotate) block).hideStressImpact();
        } else {
            showStressImpact = true;
        }

        if (block instanceof ValveHandleBlock)
            block = AllBlocks.COPPER_VALVE_HANDLE.get();

        boolean hasStressImpact =
                IRotate.StressImpact.isEnabled() && showStressImpact && BlockStressValues.getImpact(block) > 0;
        boolean hasStressCapacity = IRotate.StressImpact.isEnabled() && BlockStressValues.getCapacity(block) > 0;

        if (hasStressImpact) {
            CreateLang.translate("tooltip.stressImpact")
                    .style(GRAY)
                    .addTo(list);

            double impact = BlockStressValues.getImpact(block);
            IRotate.StressImpact impactId = impact >= config.highStressImpact.get() ? IRotate.StressImpact.HIGH
                    : (impact >= config.mediumStressImpact.get() ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.LOW);
            LangBuilder builder = CreateLang.builder()
                    .add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
                            .style(impactId.getAbsoluteColor()));

            if (hasGoggles) {
                builder.add(CreateLang.number(impact))
                        .add(suUnit)
                        .addTo(list);
            } else
                builder.translate("tooltip.stressImpact." + Lang.asId(impactId.name()))
                        .addTo(list);
        }

        if (hasStressCapacity) {
            CreateLang.translate("tooltip.capacityProvided")
                    .style(GRAY)
                    .addTo(list);

            double capacity = BlockStressValues.getCapacity(block);
            BlockStressValues.GeneratedRpm generatedRPM = BlockStressValues.RPM.get(block);

            IRotate.StressImpact impactId = capacity >= config.highCapacity.get() ? IRotate.StressImpact.HIGH
                    : (capacity >= config.mediumCapacity.get() ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.LOW);
            IRotate.StressImpact opposite = IRotate.StressImpact.values()[IRotate.StressImpact.values().length - 2 - impactId.ordinal()];
            LangBuilder builder = CreateLang.builder()
                    .add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
                            .style(opposite.getAbsoluteColor()));

            if (hasGoggles) {
                builder.add(CreateLang.number(capacity))
                        .add(suUnit)
                        .addTo(list);

                if (generatedRPM != null) {
                    LangBuilder amount = CreateLang.number(capacity * generatedRPM.value())
                            .add(suUnit);
                    CreateLang.text(" -> ")
                            .add(generatedRPM.mayGenerateLess() ? CreateLang.translate("tooltip.up_to", amount) : amount)
                            .style(DARK_GRAY)
                            .addTo(list);
                }
            } else
                builder.translate("tooltip.capacityProvided." + Lang.asId(impactId.name()))
                        .addTo(list);
        }

        return list;
    }
}
