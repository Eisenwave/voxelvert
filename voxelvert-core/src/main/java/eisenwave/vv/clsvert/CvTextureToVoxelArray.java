package eisenwave.vv.clsvert;

import eisenwave.spatium.enums.Axis;
import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class CvTextureToVoxelArray implements Classverter<Texture, VoxelArray> {
    
    @Override
    public Class<Texture> getFrom() {
        return Texture.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Override
    public VoxelArray invoke(@NotNull Texture texture, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args[0], Direction.class);
        
        return invoke(texture, (Direction) args[0]);
    }
    
    public VoxelArray invoke(Texture texture, Direction direction) {
        int[] xyz = getDimensions(texture, direction.axis());
        VoxelArray array = new VoxelArray(xyz[0], xyz[1], xyz[2]);
        CoordinateRemapper remapper = getRemapper(direction, xyz[0], xyz[1], xyz[2]);
        fill(array, texture, remapper);
        
        return array;
    }
    
    private static void fill(VoxelArray array, Texture texture, CoordinateRemapper remapper) {
        final int width = texture.getWidth(), height = texture.getHeight();
        
        for (int u = 0; u < width; u++)
            for (int v = 0; v < height; v++) {
                int[] coords = remapper.remap(u, v);
                array.setRGB(coords[0], coords[1], coords[2], texture.get(u, v));
            }
    }
    
    private static int[] getDimensions(Texture texture, Axis axis) {
        switch (axis) {
            case X: return new int[] {1, texture.getHeight(), texture.getWidth()};
            case Y: return new int[] {texture.getHeight(), 1, texture.getWidth()};
            case Z: return new int[] {texture.getWidth(), texture.getHeight(), 1};
            default: throw new IllegalArgumentException("unknown axis: " + axis);
        }
    }
    
    /**
     * Returns a {@link CoordinateRemapper} for a given direction for given boundaries.
     *
     * @param direction the direction
     * @param x the x-boundaries
     * @param y the y-boundaries
     * @param z the z-boundaries
     * @return a new coordinate remapper
     */
    private static CoordinateRemapper getRemapper(Direction direction, int x, int y, int z) {
        final int xmax = x - 1, ymax = y - 1, zmax = z - 1;
        switch (direction) {
            case NEGATIVE_X: return (u, v) -> new int[] {0, ymax - v, u};
            case POSITIVE_X: return (u, v) -> new int[] {xmax, ymax - v, zmax - u};
            case NEGATIVE_Y: return (u, v) -> new int[] {v, 0, u};
            case POSITIVE_Y: return (u, v) -> new int[] {xmax - v, ymax, u};
            case NEGATIVE_Z: return (u, v) -> new int[] {xmax - u, ymax - v, 0};
            case POSITIVE_Z: return (u, v) -> new int[] {u, ymax - v, zmax};
            default: throw new IllegalArgumentException("unknown direction: " + direction);
        }
    }
    
    @FunctionalInterface
    private static interface CoordinateRemapper {
        int[] remap(int u, int v);
    }
    
}
