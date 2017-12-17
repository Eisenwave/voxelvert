package eisenwave.vv.clsvert;

import eisenwave.torrens.schematic.ArrayBlockStructure;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.BlockStructure;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class CvVoxelArrayToBlocks implements Classverter<VoxelArray, BlockStructure> {
    
    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }
    
    @Override
    public Class<BlockStructure> getTo() {
        return BlockStructure.class;
    }
    
    @Override
    public BlockStructure invoke(@NotNull VoxelArray from, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
        
        return invoke(from, Arguments.requireType(args[0], BlockColorTable.class));
    }
    
    public BlockStructure invoke(VoxelArray from, BlockColorTable colorTable) {
        BlockStructure result = new ArrayBlockStructure(from.getSizeX(), from.getSizeY(), from.getSizeZ());
        
        int lastRGB = 0;
        BlockKey lastKey = null;
        
        for (VoxelArray.Voxel voxel : from) {
            int rgb = voxel.getRGB();
            if (rgb != lastRGB) {
                lastKey = colorTable.get(rgb);
                lastRGB = rgb;
            }
            //if (lastKey == null) lastKey = new BlockKey(152);
            if (lastKey == null) continue;
            result.setBlock(voxel.getPosition(), lastKey);
        }
        
        return result;
    }
    
}
