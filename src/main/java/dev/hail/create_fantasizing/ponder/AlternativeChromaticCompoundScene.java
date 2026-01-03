package dev.hail.create_fantasizing.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

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

        ElementLink<WorldSectionElement> more_rods =
                scene.world().showIndependentSection(util.select().layer(2), Direction.UP);
        scene.world().moveSection(more_rods, util.vector().of(0, -1, 0), 0);
        scene.idle(10);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(4, 1, 4)))
                .placeNearTarget()
                .text("2");
        scene.idle(60);

        scene.world().hideSection(util.select().layer(1), Direction.DOWN);
        scene.world().hideIndependentSection(more_rods, Direction.DOWN);
        scene.world().modifyEntity(compound0, Entity::discard);

        scene.addKeyframe();
        ElementLink<WorldSectionElement> beacon =
                scene.world().showIndependentSection(util.select().layers(3, 2), Direction.UP);
        scene.world().moveSection(beacon, util.vector().of(0, -2, 0), 0);
        scene.idle(20);
        ElementLink<EntityElement> compound1 =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(4, 4, 4)), util.vector().of(0, -1.5, 0),
                        new ItemStack(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND.asItem()));
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(4, 3, 4)))
                .placeNearTarget()
                .text("3");
        scene.idle(60);
        scene.world().hideSection(util.select().layer(0), Direction.DOWN);
        scene.world().hideIndependentSection(beacon, Direction.DOWN);
        scene.world().modifyEntity(compound1, Entity::discard);
        scene.idle(20);

        ElementLink<WorldSectionElement> bedrock_layer =
                scene.world().showIndependentSection(util.select().layer(5), Direction.UP);
        scene.world().moveSection(bedrock_layer, util.vector().of(0, -5, 0), 0);
        scene.addKeyframe();
        scene.idle(20);
        scene.world().createItemEntity(util.vector().centerOf(util.grid().at(4, 1, 4)), util.vector().of(0, -0.5, 0),
                        new ItemStack(CFAItems.ALTERNATIVE_CHROMATIC_COMPOUND.asItem()));
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(4, 0, 4)))
                .placeNearTarget()
                .text("4");
        scene.idle(60);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(4, 0, 4)))
                .placeNearTarget()
                .text("5");
        scene.idle(60);
        scene.markAsFinished();
    }
}
