package net.grian.vv.convert;

import com.sun.istack.internal.NotNull;
import net.grian.spatium.enums.Axis;
import net.grian.spatium.enums.Direction;
import net.grian.vv.core.Texture;
import net.grian.vv.core.VoxelArray.Voxel;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;
import net.grian.vv.util.ColorMath;

public class ConverterVoxelsToTexture implements Converter<VoxelArray, Texture> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<Texture> getTo() {
        return Texture.class;
    }

    @Override
    public Texture invoke(@NotNull VoxelArray from, @NotNull Object... args) {
        Arguments.requireMin(args, 3);
        Arguments.requireType(args[0], Direction.class);
        Arguments.requireType(args[1], Boolean.class);
        Arguments.requireType(args[2], Boolean.class);

        return invoke(from, (Direction) args[0], (boolean) args[1], (boolean) args[2]);
    }

    public Texture invoke(VoxelArray array, Direction dir, boolean deep, boolean contentCrop) {
        if (contentCrop) array = cropToContent(array);

        int[] whd = getDimensions(array, dir.axis());
        int width = whd[0], height = whd[1], depth = whd[2];

        return render(array, getRemapper(dir, width, height, depth), width, height, depth, deep);
    }

    @FunctionalInterface
    private static interface CoordinateRemapper {
        int[] remap(int x, int y, int z);
    }

    /**
     * Renders the voxel array from a side of choice.
     *
     * @param array the array
     * @param remapper the coordinate remapper (u,v,w -> x,y,z)
     * @param width the width of the array
     * @param height the height of the array
     * @param depth the depth of the array
     * @param deep whether the engine should go deeper if no fully opaque voxel is found on the boundaries of the array
     * @return a render of the voxel array
     */
    private static Texture render(
            VoxelArray array, CoordinateRemapper remapper,
            int width, int height, int depth, boolean deep) {
        Texture texture = new Texture(width, height);

        if (deep) {
            for (int u = 0; u < width; u++) for (int v = 0; v < height; v++) {
                int rgb = 0;
                for (int w = 0; w < depth; w++) {
                    int[] xyz = remapper.remap(u, v, w);
                    int top = array.getRGB(xyz[0], xyz[1], xyz[2]);
                    if (isOpaque(rgb = ColorMath.stack(rgb, top))) break;
                }
                texture.set(u, v, rgb);
            }
        }
        else {
            for (int u = 0; u < width; u++) for (int v = 0; v < height; v++) {
                int[] xyz = remapper.remap(u, v, 0);
                texture.set(u, v, array.getRGB(xyz[0], xyz[1], xyz[2]));
            }
        }

        return texture;
    }

    /**
     * Checks whether a given ARGB integer represents a fully opaque <code>(alpha = 255)</code> color.
     *
     * @param argb the color
     * @return whether the color is fully opaque
     */
    private static boolean isOpaque(int argb) {
        return (argb & 0xFF_00_00_00) == 0xFF_00_00_00;
    }

    /**
     * Returns an int-array of length 3 containing the width, height and depth of the voxel array depending on what
     * {@link Axis} the array is being looked at.
     *
     * @param array the voxel array
     * @param axis the axis
     * @return the width, height, depth of the voxel array
     */
    private static int[] getDimensions(VoxelArray array, Axis axis) {
        switch (axis) {
            case X: return new int[] {
                    array.getSizeZ(),
                    array.getSizeY(),
                    array.getSizeX()
            };
            case Y: return new int[] {
                    array.getSizeZ(),
                    array.getSizeX(),
                    array.getSizeY()
            };
            case Z: return new int[] {
                    array.getSizeX(),
                    array.getSizeY(),
                    array.getSizeZ()
            };
            default: throw new AssertionError(axis);
        }
    }

    private static CoordinateRemapper getRemapper(Direction dir, int width, int height, int depth) {
        final int umax = width-1, vmax = height-1, wmax = depth-1;
        switch (dir) {
            case NEGATIVE_X: return (u,v,w) -> new int[] {w,      vmax-v, u};
            case POSITIVE_X: return (u,v,w) -> new int[] {wmax-w, vmax-v, umax-u};
            case NEGATIVE_Y: return (u,v,w) -> new int[] {v,      w,      u};
            case POSITIVE_Y: return (u,v,w) -> new int[] {vmax-v, wmax-w, u};
            case NEGATIVE_Z: return (u,v,w) -> new int[] {umax-u, vmax-v, w};
            case POSITIVE_Z: return (u,v,w) -> new int[] {u,      vmax-v, wmax-w};
            default: throw new IllegalArgumentException("unknown direction: "+dir);
        }
    }

    /**
     * <p>
     *     Returns a section inside an array that contains voxels. Should the array contain no voxels, a copy of the
     *     given array will be returned.
     * </p>
     * <p>
     *     Essentially, this method crops the array to a section inside it containing relevant information. For example,
     *     if a 10<sup>3</sup> array contains nothing but a 2<sup>3</sup> cube of voxels, a new 2<sup>3</sup> array
     *     containing only this cube will be returned.
     * </p>
     *
     * @param array the voxel array
     * @return a section holding content inside the array
     */
    @SuppressWarnings("ConstantConditions")
    public static VoxelArray cropToContent(VoxelArray array) {
        int
                xmax = 0, ymax = 0, zmax = 0,
                xmin = array.getSizeX()-1, ymin = array.getSizeY()-1, zmin = array.getSizeZ()-1;

        boolean empty = true;
        for (Voxel voxel : array) {
            if (empty) empty = false;
            int x = voxel.getX(), y = voxel.getY(), z = voxel.getZ();
            if (x < xmin) xmin = x;
            if (y < ymin) ymin = y;
            if (z < zmin) zmin = z;
            if (x > xmax) xmax = x;
            if (y > ymax) ymax = y;
            if (z > zmax) zmax = z;
        }

        return empty? array.clone() : array.copy(xmin, ymin, zmin, xmax, ymax, zmax);
    }

}
