package dev.hail.create_fantasizing.item.block_placer;

public enum BlockPlacerBrushes {
    Cuboid(new BPCuboidBrush()),
    Sphere(new BPSphereBrush()),
    Cylinder(new BPCylinderBrush()),
    Surface(new BPDynamicBrush(true)),
    Cluster(new BPDynamicBrush(false));

    private BPBrush brush;

    BlockPlacerBrushes(BPBrush brush) {
        this.brush = brush;
    }

    public BPBrush get() {
        return brush;
    }

}
