package eisenwave.vv.clsvert;

import eisenwave.torrens.voxel.QBMatrix;
import eisenwave.torrens.voxel.QBModel;
import eisenwave.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.NotNull;

public class CvQBToVoxelMesh implements Classverter<QBModel, VoxelMesh> {
    
    @Deprecated
    @Override
    public VoxelMesh invoke(@NotNull QBModel from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public static VoxelMesh invoke(@NotNull QBModel from) {
        VoxelMesh result = new VoxelMesh();
        
        for (QBMatrix matrix : from)
            result.add(matrix.getMinX(), matrix.getMinY(), matrix.getMinZ(), matrix.getVoxels());
        
        return result;
    }
    
}
