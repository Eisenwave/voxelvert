package eisenwave.vv.clsvert;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class CvVoxelArrayToVoxelMesh implements Classverter<VoxelArray, VoxelMesh> {
    
    @Nullable
    private final Logger logger;
    
    public CvVoxelArrayToVoxelMesh(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    public CvVoxelArrayToVoxelMesh() {
        this(null);
    }
    
    @Deprecated
    @Override
    public VoxelMesh invoke(@NotNull VoxelArray array, @NotNull Object... args) {
        //System.out.println(args.length);
        BoundingBox6i[] boxes = new CvBitArrayMerger_XYZ(logger).invoke(array, args);
        //System.out.println(Arrays.toString(boxes));
        //System.out.println(boxes.length+" bboxes");
        
        // cut arrays out of array using zones
        //return null;
        return cut(array, boxes);
    }
    
    public VoxelMesh invoke(@NotNull VoxelArray array) {
        return invoke(array, 3);
    }
    
    public VoxelMesh invoke(@NotNull VoxelArray array, int cancel) {
        //System.out.println(args.length);
        BoundingBox6i[] boxes = new CvBitArrayMerger_XYZ(logger).invoke(array, cancel);
        //System.out.println(Arrays.toString(boxes));
        //System.out.println(boxes.length+" bboxes");
        
        // cut arrays out of array using zones
        //return null;
        return cut(array, boxes);
    }
    
    private static VoxelMesh cut(VoxelArray array, BoundingBox6i[] boxes) {
        VoxelMesh result = new VoxelMesh();
        
        for (BoundingBox6i box : boxes) {
            final int x = box.getMinX(), y = box.getMinY(), z = box.getMinZ();
            VoxelArray copy = array.copy(x, y, z, box.getMaxX(), box.getMaxY(), box.getMaxZ());
            result.add(x, y, z, copy);
        }
        
        return result;
    }
    
}
