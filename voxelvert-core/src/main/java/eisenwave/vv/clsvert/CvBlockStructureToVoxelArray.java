package eisenwave.vv.clsvert;

import eisenwave.torrens.schematic.legacy.LegacyBlockStructure;
import eisenwave.torrens.schematic.legacy.LegacyBlockStructureStream;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import eisenwave.vv.rp.BlockColorTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CvBlockStructureToVoxelArray implements Classverter<LegacyBlockStructure, VoxelArray> {
    
    @Deprecated
    @Override
    public VoxelArray invoke(@NotNull LegacyBlockStructure blocks, @NotNull Object... args) {
        Arguments.requireMin(args, 2);
        Arguments.requireType(args[0], BlockColorTable.class);
        Arguments.requireType(args[1], Integer.class);
        
        return invoke(blocks, (BlockColorTable) args[0], (int) args[1]);
    }
    
    public static VoxelArray invoke(LegacyBlockStructure blocks, @Nullable BlockColorTable colors, int flags) {
        return CvBlockStreamToVoxelArray.invoke(new LegacyBlockStructureStream(blocks), colors, flags);
    }
    
}
