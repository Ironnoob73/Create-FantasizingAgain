package dev.hail.create_fantasizing.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.hail.create_fantasizing.block.CFABlocks;
import dev.hail.create_fantasizing.item.CFAItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CrateScene {

    public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("crate_usage", "The Adjustable Crate is back!");
        scene.showBasePlate();
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("1");
        scene.idle(60);

        scene.addKeyframe();
        ElementLink<WorldSectionElement> more_crates =
                scene.world().showIndependentSection(util.select().layers(2, 2), Direction.DOWN);
        scene.world().moveSection(more_crates, util.vector().of(0, -1, 0), 0);
        scene.idle(10);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("2");
        scene.idle(60);
        scene.world().hideIndependentSection(more_crates, Direction.UP);

        scene.addKeyframe();
        scene.idle(10);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("3");
        scene.idle(60);

        scene.addKeyframe();
        scene.idle(10);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("4");
        scene.idle(30);
        scene.world().destroyBlock(BlockPos.containing(util.vector().of(2, 1, 2)));
        ElementLink<EntityElement> crate0 =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(2, 1, 2)), util.vector().of(0, 0.1, 0),
                        new ItemStack(CFABlocks.ANDESITE_CRATE.asItem()));
        scene.idle(30);
        scene.world().hideSection(util.select().layer(1), Direction.DOWN);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("5");
        scene.idle(60);
        scene.world().modifyEntity(crate0, Entity::discard);
        scene.world().setBlock(BlockPos.containing(util.vector().of(2, 1, 2)), CFABlocks.ANDESITE_CRATE.getDefaultState(), false);
        scene.idle(20);

        scene.addKeyframe();
        scene.world().showSection(util.select().layer(1), Direction.UP);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("6");
        scene.overlay().showControls(util.vector().topOf(3, 0, 2), Pointing.RIGHT, 30).withItem(Items.SHULKER_BOX.getDefaultInstance());
        scene.idle(60);
        scene.world().destroyBlock(BlockPos.containing(util.vector().of(2, 1, 2)));
        ElementLink<EntityElement> crate1 =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(2, 1, 2)), util.vector().of(0, 0.1, 0),
                        new ItemStack(CFABlocks.ANDESITE_CRATE.asItem()));
        ElementLink<EntityElement> shulkerBox =
                scene.world().createItemEntity(util.vector().centerOf(util.grid().at(2, 1, 2)), util.vector().of(0.05, 0.1, -0.05),
                        new ItemStack(Items.SHULKER_BOX.asItem()));
        scene.idle(30);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("7");
        scene.world().hideSection(util.select().layer(1), Direction.DOWN);
        scene.idle(60);
        scene.world().modifyEntity(crate1, Entity::discard);
        scene.world().modifyEntity(shulkerBox, Entity::discard);
        scene.idle(20);

        scene.addKeyframe();
        scene.world().setBlock(BlockPos.containing(util.vector().of(2, 1, 2)),
                AllBlocks.MECHANICAL_BEARING.getDefaultState().setValue(DirectionalKineticBlock.FACING, Direction.UP), true);
        scene.world().showSection(util.select().layer(1), Direction.UP);
        ElementLink<WorldSectionElement> rotate_crates =
                scene.world().showIndependentSection(util.select().layer(4), Direction.DOWN);
        scene.world().moveSection(rotate_crates, util.vector().of(0, -2, 0), 0);
        BlockPos bearing = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(bearing), Direction.UP);
        scene.world().configureCenterOfRotation(rotate_crates, util.vector().centerOf(bearing));
        scene.idle(20);
        scene.world().rotateBearing(bearing, 360, 70);
        scene.world().rotateSection(rotate_crates, 0, 360, 0, 70);
        scene.idle(10);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 1, 2)))
                .placeNearTarget()
                .text("8");
        scene.idle(60);
        scene.world().hideSection(util.select().layer(1), Direction.DOWN);
        scene.world().hideIndependentSection(rotate_crates, Direction.UP);
        scene.idle(20);

        scene.addKeyframe();
        ElementLink<WorldSectionElement> other_crates =
                scene.world().showIndependentSection(util.select().layer(5), Direction.DOWN);
        scene.world().moveSection(other_crates, util.vector().of(0, -4, 0), 0);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(util.grid().at(2, 0, 2)))
                .placeNearTarget()
                .text("9");
        scene.idle(60);
        scene.markAsFinished();
    }
}
