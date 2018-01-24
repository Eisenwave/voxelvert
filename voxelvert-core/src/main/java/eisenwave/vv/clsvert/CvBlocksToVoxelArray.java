package eisenwave.vv.clsvert;

import eisenwave.vv.rp.BlockColor;
import eisenwave.torrens.schematic.BlockStructure;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import eisenwave.vv.rp.BlockColorTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CvBlocksToVoxelArray implements Classverter<BlockStructure, VoxelArray> {
    
    public final static BlockColor DEFAULT_COLOR = new BlockColor(0xFFFFFFFF);
    
    /** do not place voxel for non-full blocks */
    public final static int
        FULL_BLOCKS = 1,
    /** make every color either solid or invisible */
    IGNORE_ALPHA = 1 << 1,
    /** use visual occupation as multiplier for alpha channel */
    USE_OCCUPATION = 1 << 2,
    /** display missing entries in the {@link BlockColorTable} as {@link ColorMath#DEBUG1} */
    SHOW_MISSING = 1 << 3;
    /* apply {@link ColorMath#DEFAULT_TINT} as tint (always true if block array has no biomes) *
    DEFAULT_TINT = 1 << 4;*/
    
    @Override
    public Class<BlockStructure> getFrom() {
        return BlockStructure.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Override
    public VoxelArray invoke(@NotNull BlockStructure blocks, @NotNull Object... args) {
        Arguments.requireMin(args, 2);
        Arguments.requireType(args[0], BlockColorTable.class);
        Arguments.requireType(args[1], Integer.class);
        
        return invoke(blocks, (BlockColorTable) args[0], (int) args[1]);
    }
    
    public VoxelArray invoke(BlockStructure blocks, @Nullable BlockColorTable colors, int flags) {
        final boolean
            full_blocks = (flags & FULL_BLOCKS) != 0,
            ignore_alpha = (flags & IGNORE_ALPHA) != 0,
            use_occupation = (flags & USE_OCCUPATION) != 0,
            show_missing = (flags & SHOW_MISSING) != 0;
        //default_tint = true /*(flags & DEFAULT_TINT) != 0 || !blocks.hasBiomes()
        final int
            limX = blocks.getSizeX(), limY = blocks.getSizeY(), limZ = blocks.getSizeZ();
        
        VoxelArray voxels = new VoxelArray(limX, limY, limZ);
        
        for (int x = 0; x < limX; x++)
            for (int y = 0; y < limY; y++)
                for (int z = 0; z < limZ; z++) {
                    
                    final BlockColor color;
                    final BlockKey block = blocks.getBlock(x, y, z);
                    if (colors == null) {
                        if (block.getId() == 0) continue;
                        color = DEFAULT_COLOR;
                    }
                    else {
                        color = colors.get(block);
                        
                        if (color == null) {
                            if (show_missing) voxels.setRGB(x, y, z, ColorMath.DEBUG1);
                            continue;
                        }
                        if (color.isInvisible() || full_blocks && color.getRelativeVolume() < 1) continue;
                    }
                    
                    int rgb = color.getRGB();
                    if (ignore_alpha) rgb = ColorMath.isInvisible(rgb)? rgb : rgb | 0xFF_000000;
                    if (use_occupation) rgb = ColorMath.fromRGB(
                        ColorMath.red(rgb), ColorMath.green(rgb), ColorMath.blue(rgb),
                        (int) (ColorMath.alpha(rgb) * color.getPerceivedVolume()));
                    /*
                    if (color.getTint() != BlockColor.TINT_NONE) {
                        int tint = default_tint? ColorMath.DEFAULT_TINT : ColorMath.DEFAULT_TINT;
                        rgb = ColorMath.fromTintedRGB(rgb, tint);
                    }
                    */
                    
                    voxels.setRGB(x, y, z, rgb);
                }
        
        return voxels;
    }
    
}
