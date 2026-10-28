package dev.hail.create_fantasizing.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;

public class AlternativeChromaticCompoundScene {

    public static void convert(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("acc_convert", "Convert the Alternative Chromatic Compound to other materials.");
        scene.showBasePlate();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        ElementLink<EntityElement> compound0 =
            scene.world().createItemEntity(util.vector().centerOf(util.grid().at(4, 2, 3)), util.vector().of(0, -1, 0.5),
                    new ItemStack(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND.asItem()));
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(4, 0, 4)))
                .placeNearTarget()
                .text("1");
        scene.idle(60);

        ElementLink<WorldSectionElement> moreRods =
                scene.world().showIndependentSection(util.select().layer(2), Direction.UP);
        scene.world().moveSection(moreRods, util.vector().of(0, -1, 0), 0);
        scene.idle(10);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(4, 1, 4)))
                .placeNearTarget()
                .text("2");
        scene.idle(60);

        scene.world().hideSection(util.select().layer(1), Direction.DOWN);
        scene.world().hideIndependentSection(moreRods, Direction.DOWN);
        scene.world().modifyEntity(compound0, Entity::discard);

        scene.addKeyframe();
        ElementLink<WorldSectionElement> beacon =
                scene.world().showIndependentSection(util.select().layers(3, 2), Direction.UP);
        scene.world().moveSection(beacon, util.vector().of(0, -2, 0), 0);
        scene.idle(20);

        // By Deepseek V4.1 Flash 1017:
        // Make the beacon display its activated beam while the compound is thrown in.
        // Beacon level data is recomputed by vanilla's ticker, which Ponder does not run,
        // so the level and one beam section are supplied explicitly for the animation.
        scene.world().modifyBlockEntity(util.grid().at(4, 4, 4), BeaconBlockEntity.class, beaconBlockEntity -> {
            beaconBlockEntity.levels = 1;
            beaconBlockEntity.getBeamSections().clear();
            beaconBlockEntity.getBeamSections().add(new BeaconBlockEntity.BeaconBeamSection(0xFFFFFF));
        });
        scene.idle(10);

        ElementLink<EntityElement> compound1 =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(5, 4, 3)), util.vector().of(-0.5, -0.2, 0.5),
                        new ItemStack(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND.asItem()));
        scene.idle(5);

        // By Deepseek V4.1 Flash 1017:
        // Animation-only conversion: replace the thrown item with Refined Radiance
        // and let it rise through the beacon beam.
        scene.world().modifyEntity(compound1, entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setItem(AllItems.REFINED_RADIANCE.asStack());
                itemEntity.setNoGravity(true);
                itemEntity.setDeltaMovement(-0.1, 0.2, 0.1);
            }
        });
        scene.effects().emitParticles(util.vector().centerOf(4, 3, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.FLASH, Vec3.ZERO), 1, 1);
        scene.effects().emitParticles(util.vector().centerOf(4, 3, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.WITCH, VecHelper.offsetRandomly(new Vec3(0, 0.1, 0), RandomSource.create(), 0.1F)), 20, 0);
        scene.effects().emitParticles(util.vector().centerOf(4, 3, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.END_ROD, VecHelper.offsetRandomly(new Vec3(0, 0.1, 0), RandomSource.create(), 0.1F)), 20, 0);
        scene.effects().emitParticles(util.vector().centerOf(4, 3, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.END_ROD, Vec3.ZERO), 2, 40);
        scene.idle(10);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(4, 3, 4)))
                .placeNearTarget()
                .text("3");
        scene.idle(60);
        scene.world().hideSection(util.select().layer(0), Direction.DOWN);
        scene.world().hideIndependentSection(beacon, Direction.DOWN);
        scene.world().modifyEntity(compound1, Entity::discard);
        scene.idle(20);

        ElementLink<WorldSectionElement> bedrockLayer =
                scene.world().showIndependentSection(util.select().layer(5), Direction.UP);
        scene.world().moveSection(bedrockLayer, util.vector().of(0, -5, 0), 0);
        scene.addKeyframe();
        scene.world().setBlocks(util.select().fromTo(3, 0, 3, 5, 4, 5), Blocks.AIR.defaultBlockState(), false);
        scene.world().setBlocks(util.select().fromTo(3, 0, 3, 5, 0, 5), Blocks.BEDROCK.defaultBlockState(), false);

        ElementLink<EntityElement> compound2 =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(4, 2, 4)), util.vector().of(0, -2, 0),
                        new ItemStack(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND.asItem()));
        scene.idle(20);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(4, 0, 4)))
                .placeNearTarget()
                .text("4");
        scene.idle(60);

        // By Deepseek V4.1 Flash 1017:
        // Open a 3x3 hole in the bedrock floor so the compound can visibly fall below ground.
        scene.world().setBlocks(util.select().fromTo(3, 0, 3, 5, 0, 5), Blocks.AIR.defaultBlockState(), true);
        scene.world().setBlocks(util.select().fromTo(3, 0, 3, 5, 5, 5), Blocks.AIR.defaultBlockState(), false);
        scene.idle(5);
        scene.world().modifyEntity(compound2, entity -> {
            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.setItem(AllItems.SHADOW_STEEL.asStack());
                itemEntity.setNoGravity(true);
                itemEntity.setDeltaMovement(0, 0.1, 0);
            }
        });
        scene.effects().emitParticles(util.vector().centerOf(4, 0, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.FLASH, Vec3.ZERO), 1, 1);
        scene.effects().emitParticles(util.vector().centerOf(4, 0, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.WITCH, VecHelper.offsetRandomly(new Vec3(0, 0.1, 0), RandomSource.create(), 0.1F)), 20, 0);
        scene.effects().emitParticles(util.vector().centerOf(4, 0, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.END_ROD, VecHelper.offsetRandomly(new Vec3(0, 0.1, 0), RandomSource.create(), 0.1F)), 20, 0);
        scene.effects().emitParticles(util.vector().centerOf(4, 0, 4),
                scene.effects().simpleParticleEmitter(ParticleTypes.END_ROD, Vec3.ZERO), 2, 40);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(4, 0, 4)))
                .placeNearTarget()
                .text("5");
        scene.idle(60);
        scene.markAsFinished();
    }
}
