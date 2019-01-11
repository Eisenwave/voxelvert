package eisenwave.vv.clsvert;

import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.legacy.ArrayBlockStructure;
import eisenwave.torrens.schematic.legacy.LegacyBlockKey;
import eisenwave.torrens.schematic.legacy.LegacyBlockStructure;
import eisenwave.torrens.schematic.legacy.MicroLegacyUtil;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class CvVoxelArrayToBlocks implements Classverter<VoxelArray, LegacyBlockStructure> {
    
    @Deprecated
    @Override
    public LegacyBlockStructure invoke(@NotNull VoxelArray from, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
        
        return invoke(from, Arguments.requireType(args[0], BlockColorTable.class));
    }
    
    public static LegacyBlockStructure invoke(VoxelArray from, BlockColorTable colorTable) {
        LegacyBlockStructure result = new ArrayBlockStructure(from.getSizeX(), from.getSizeY(), from.getSizeZ());
        
        int lastRGB = 0;
        BlockKey lastKey = null;
        
        for (VoxelArray.Voxel voxel : from) {
            int rgb = voxel.getRGB();
            if (rgb != lastRGB) {
                lastKey = colorTable.get(rgb, true);
                lastRGB = rgb;
            }
            //if (lastKey == null) lastKey = new BlockKey(152);
            if (lastKey == null) continue;
            LegacyBlockKey key = MicroLegacyUtil.getByMinecraftKey13(lastKey);
            if (key != null)
                result.setBlock(voxel.getPosition(), key);
        }
        
        return result;
    }
    
}
