package eisenwave.vv.clsvert;

import eisenwave.torrens.voxel.QBMatrix;
import eisenwave.torrens.voxel.QBModel;
import eisenwave.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.NotNull;

public class CvVoxelMeshToQB implements Classverter<VoxelMesh, QBModel> {
    
    @Deprecated
    @Override
    public QBModel invoke(@NotNull VoxelMesh from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public QBModel invoke(@NotNull VoxelMesh from) {
        QBModel result = new QBModel();
        
        int i = 0;
        for (VoxelMesh.Element e : from) {
            String name = "matrix" + i;
            result.add(new QBMatrix(name, e.getMinX(), e.getMinY(), e.getMinZ(), e.getArray()));
        }
        
        return result;
    }
    
}
