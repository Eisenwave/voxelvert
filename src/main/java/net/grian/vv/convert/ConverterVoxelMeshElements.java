package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.vv.core.*;
import net.grian.vv.util.Arguments;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConverterVoxelMeshElements implements Converter<VoxelMesh, ElementSet> {

    @Override
    public Class<VoxelMesh> getFrom() {
        return VoxelMesh.class;
    }

    @Override
    public Class<ElementSet> getTo() {
        return ElementSet.class;
    }

    @Override
    public ElementSet invoke(VoxelMesh from, Object[] args) {
        Arguments.requireNonnull(from, "from must not be null");
        Arguments.requireNonnull(args, "args must not be null");

        BlockyBox[] set = toPrimElements(from);

        return null;
    }

    private static BlockyBox[] toPrimElements(VoxelMesh mesh) {
        BlockyBox[] result = new BlockyBox[mesh.size()];
        ConverterVoxelsTexture texturer = new ConverterVoxelsTexture();

        Iterator<VoxelMesh.Element> iter = mesh.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            VoxelMesh.Element element = iter.next();

            VoxelArray array = element.getArray();
            BlockSelection bounds = array.getBoundaries().move(element.getX(), element.getY(), element.getZ());
            BlockyBox transElement = new BlockyBox(bounds, null);
            for (Direction dir : Direction.values());
                //transElement.setTexture(dir, texturer.invoke(array, dir, false, false));
            result[i] = transElement;
        }

        return  result;
    }

    private static void optimizeFaces(Collection<BlockyBox> boxes) {
        for (BlockyBox box : boxes) {
            final TexturedBox handle = box.getHandle();
            final BlockSelection shape = box.getShape();

            for (Direction side : Direction.values()) {
                BlockVector[] corners = getCorners(shape, side);

                for (BlockyBox box2 : boxes) {
                    BlockSelection shape2 = box2.getShape();
                    if (shape2.contains(corners[0]) && shape2.contains(corners[1]))
                        handle.disableSide(side);
                }
            }
        }

        Iterator<BlockyBox> iter = boxes.iterator();
        while (iter.hasNext())
            if (!iter.next().getHandle().isVisible())
                iter.remove();
    }

    /**
     * Checks whether a side of one box is covered by another box.
     *
     * @param boxA the box
     * @param side the side of the box
     * @param boxB the second box which may cover the first one's side
     * @return whether boxB covers the side of boxA
     */
    private static boolean isCoveredBy(BlockSelection boxA, Direction side, BlockSelection boxB) {
        BlockVector[] corners = getCorners(boxA, side);
        return false;
    }

    private static BlockVector[] getCorners(BlockSelection box, Direction side) {
        int coord;
        switch (side) {
            case NEGATIVE_X: coord = box.getMinX()-1; break;
            case POSITIVE_X: coord = box.getMaxX()+1; break;
            case NEGATIVE_Y: coord = box.getMinY()-1; break;
            case POSITIVE_Y: coord = box.getMaxY()+1; break;
            case NEGATIVE_Z: coord = box.getMinZ()-1; break;
            case POSITIVE_Z: coord = box.getMaxZ()+1; break;
            default: throw new IllegalArgumentException("direction has no axis");
        }

        switch (side.axis()) {
            case X: return new BlockVector[] {
                    BlockVector.fromXYZ(coord, box.getMinY(), box.getMinZ()),
                    BlockVector.fromXYZ(coord, box.getMaxY(), box.getMaxZ())};
            case Y: return new BlockVector[] {
                    BlockVector.fromXYZ(box.getMinX(), coord, box.getMinZ()),
                    BlockVector.fromXYZ(box.getMaxX(), coord, box.getMaxZ())};
            case Z: return new BlockVector[] {
                    BlockVector.fromXYZ(coord, box.getMinY(), box.getMinZ()),
                    BlockVector.fromXYZ(coord, box.getMaxY(), box.getMaxZ())};
            default: throw new AssertionError(side.axis());
        }
    }

    /**
     * Returns whether a side of one box is touching another box.
     *
     * @param boxA the first box
     * @param side the side of the first box
     * @param boxB the second box
     * @return whether the first box's side touches the second box
     */
    private static boolean isTouching(BlockSelection boxA, Direction side, BlockSelection boxB) {
        switch (side) {
            case NEGATIVE_X: return boxB.getMaxX()+1 >= boxA.getMinX();
            case POSITIVE_X: return boxA.getMaxX()+1 >= boxB.getMinX();
            case NEGATIVE_Y: return boxB.getMaxY()+1 >= boxA.getMinY();
            case POSITIVE_Y: return boxA.getMaxY()+1 >= boxB.getMinY();
            case NEGATIVE_Z: return boxB.getMaxZ()+1 >= boxA.getMinZ();
            case POSITIVE_Z: return boxA.getMaxZ()+1 >= boxB.getMinZ();
            default: throw new AssertionError(side);
        }
    }

    private static class BlockyBox {

        private final BlockSelection shape;
        private final TexturedBox handle;

        public BlockyBox(BlockSelection shape, TexturedBox handle) {
            this.shape = shape;
            this.handle = handle;
        }

        public BlockSelection getShape() {
            return shape;
        }

        public TexturedBox getHandle() {
            return handle;
        }
    }

}
