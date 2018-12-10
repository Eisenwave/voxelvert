package eisenwave.vv.clsvert;

import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.legacy.StructureBlock;
import eisenwave.vv.rp.BlockColor;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CvBlockStreamToVoxelArray implements Classverter<BlockStructureStream, VoxelArray> {
    
    public final static BlockColor DEFAULT_COLOR = new BlockColor(0xFFFFFFFF, 0, true);
    
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
    public Class<BlockStructureStream> getFrom() {
        return BlockStructureStream.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Override
    public VoxelArray invoke(@NotNull BlockStructureStream blocks, @NotNull Object... args) {
        Arguments.requireMin(args, 2);
        Arguments.requireType(args[0], BlockColorTable.class);
        Arguments.requireType(args[1], Integer.class);
        
        return invoke(blocks, (BlockColorTable) args[0], (int) args[1]);
    }
    
    public VoxelArray invoke(BlockStructureStream blocks, @Nullable BlockColorTable colors, int flags) {
        final boolean
            full_blocks = (flags & FULL_BLOCKS) != 0,
            ignore_alpha = (flags & IGNORE_ALPHA) != 0,
            use_occupation = (flags & USE_OCCUPATION) != 0,
            show_missing = (flags & SHOW_MISSING) != 0;
        //default_tint = true /*(flags & DEFAULT_TINT) != 0 || !blocks.hasBiomes()
        final int
            limX = blocks.getSizeX(), limY = blocks.getSizeY(), limZ = blocks.getSizeZ();
        
        //colors.forEach((key, val) -> System.err.println("BCT: " + key + " -> " + val));
        
        VoxelArray voxels = new VoxelArray(limX, limY, limZ);
        
        for (StructureBlock block : blocks) {
            int x = block.getX(), y = block.getY(), z = block.getZ();
            
            final BlockColor color;
            if (colors == null) {
                if (block.isAir()) continue;
                color = DEFAULT_COLOR;
            }
            else {
                color = colors.get(block.getKey());
                //System.out.println(block.getKey() + " -> " + color);
                
                if (color == null) {
                    if (show_missing) voxels.setRGB(x, y, z, ColorMath.DEBUG1);
                    continue;
                }
                if (color.isInvisible() || full_blocks && color.getRelativeVolume() < 1) continue;
            }
            
            int rgb = color.getRGB();
            if (ignore_alpha)
                rgb = ColorMath.isInvisible(rgb)? rgb : rgb | 0xFF_000000;
            if (use_occupation)
                rgb = ColorMath.fromRGB(
                    ColorMath.red(rgb), ColorMath.green(rgb), ColorMath.blue(rgb),
                    (int) (ColorMath.alpha(rgb) * color.getPerceivedVolume()));
            
            voxels.setRGB(x, y, z, rgb);
        }
        
        return voxels;
    }
    
}
