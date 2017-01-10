package net.grian.vv.convert;

import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockArray;
import net.grian.vv.core.BlockColor;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;
import net.grian.vv.util.ColorMath;

public class ConverterBlocksToVoxels implements Converter<BlockArray, VoxelArray> {

            /** do not place voxel for non-full blocks */
    public final static int
            FULL_BLOCKS = 1,
            /** make every color either solid or invisible */
            IGNORE_ALPHA = 1 << 1,
            /** use visual occupation as multiplier for alpha channel */
            USE_OCCUPATION = 1 << 2,
            /** display missing entries in the {@link ColorMap} as {@link ColorMath#DEBUG1}*/
            SHOW_MISSING = 1 << 3,
            /** apply {@link ColorMath#DEFAULT_TINT} as tint (always true if block array has no biomes) */
            DEFAULT_TINT = 1 << 4;

    @Override
    public Class<BlockArray> getFrom() {
        return BlockArray.class;
    }

    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }

    @Override
    public VoxelArray invoke(BlockArray blocks, Object... args) {
        Arguments.requireNonnull(blocks, "blocks must not be null");
        Arguments.requireNonnull(args, "args must not be null");
        Arguments.requireMin(args, 2);
        Arguments.requireType(args[0], ColorMap.class);
        Arguments.requireType(args[1], Integer.class);

        return invoke(blocks, (ColorMap) args[0], (int) args[1]);
    }

    public VoxelArray invoke(BlockArray blocks, ColorMap colors, int flags) {
        final boolean
                full_blocks = (flags & FULL_BLOCKS) != 0,
                ignore_alpha = (flags & IGNORE_ALPHA) != 0,
                use_occupation = (flags & USE_OCCUPATION) != 0,
                show_missing = (flags & SHOW_MISSING) != 0,
                default_tint = (flags & DEFAULT_TINT) != 0 || !blocks.hasBiomes();
        final int
                limX = blocks.getSizeX(), limY = blocks.getSizeY(), limZ = blocks.getSizeZ();

        VoxelArray voxels = new VoxelArray(limX, limY, limZ);

        for (int x = 0; x < limX; x++) for (int y = 0; y < limY; y++) for (int z = 0; z < limZ; z++) {
            BlockColor color = colors.get(blocks.getBlock(x, y, z));

            if (color == null) {
                if (show_missing) voxels.setRGB(x, y, z, ColorMath.DEBUG1);
                continue;
            }
            if (color.isInvisible() || full_blocks && color.getOccupation() < 1) continue;

            int rgb = color.getRGB();
            if (ignore_alpha) rgb = ColorMath.isInvisible(rgb)? rgb : rgb | 0xFF_000000;
            if (use_occupation) rgb = ColorMath.fromRGB(
                    ColorMath.red(rgb), ColorMath.green(rgb), ColorMath.blue(rgb),
                    (int) (ColorMath.alpha(rgb) * color.getVisualOccupation()));
            if (color.hasTint()) {
                int tint = default_tint? ColorMath.DEFAULT_TINT : ColorMath.DEFAULT_TINT; //TODO add actual getter
                rgb = ColorMath.fromTintedRGB(rgb, tint);
            }

            voxels.setRGB(x, y, z, rgb);
        }

        return voxels;
    }

}
