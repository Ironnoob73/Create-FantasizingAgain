package dev.hail.create_fantasizing.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;

public class MainSecondRelationshipScene {
    public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("main_second_relationship", "Main-second Relationship");
        scene.showBasePlate();
        ElementLink<WorldSectionElement> south_storage =
                scene.world().showIndependentSection(util.select().layer(1), Direction.SOUTH);
        ElementLink<WorldSectionElement> north_storage =
                scene.world().showIndependentSection(util.select().layer(2), Direction.NORTH);
        scene.world().moveSection(north_storage, util.vector().of(0, -1, 0), 0);
        scene.idle(15);
        scene.world().hideIndependentSection(south_storage, Direction.DOWN);
        scene.world().hideIndependentSection(north_storage, Direction.DOWN);
        ElementLink<WorldSectionElement> double_storage =
                scene.world().showIndependentSection(util.select().layer(3), Direction.UP);
        scene.world().moveSection(double_storage, util.vector().of(0, -2, 0), 0);
        scene.idle(15);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("1");
        scene.idle(60);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(3, 0, 3)))
                .placeNearTarget()
                .text("2");
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(1, 0, 1)))
                .placeNearTarget()
                .text("3");
        scene.idle(60);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("4");
        scene.overlay().showControls(util.vector().topOf(1, 0, 2), Pointing.DOWN, 30).withItem(AllItems.WRENCH.asStack());
        scene.overlay().showControls(util.vector().topOf(3, 0, 2), Pointing.UP, 30).withItem(AllItems.WRENCH.asStack());
        scene.world().hideIndependentSection(double_storage, Direction.DOWN);
        ElementLink<WorldSectionElement> alt_double_storage =
                scene.world().showIndependentSection(util.select().layer(4), Direction.UP);
        scene.world().moveSection(alt_double_storage, util.vector().of(0, -3, 0), 0);
        scene.idle(60);
        scene.markAsFinished();
    }
}
