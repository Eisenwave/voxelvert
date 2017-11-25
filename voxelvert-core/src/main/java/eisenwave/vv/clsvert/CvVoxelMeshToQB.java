package eisenwave.vv.clsvert;


import net.grian.torrens.voxel.QBMatrix;
import net.grian.torrens.voxel.QBModel;
import net.grian.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.NotNull;

public class CvVoxelMeshToQB implements Classverter<VoxelMesh, QBModel> {
    
    @Override
    public Class<VoxelMesh> getFrom() {
        return VoxelMesh.class;
    }
    
    @Override
    public Class<QBModel> getTo() {
        return QBModel.class;
    }
    
    @Override
    public QBModel invoke(@NotNull VoxelMesh from, @NotNull Object... args) {
        QBModel result = new QBModel();
        
        int i = 0;
        for (VoxelMesh.Element e : from) {
            String name = "matrix" + i;
            result.add(new QBMatrix(name, e.getMinX(), e.getMinY(), e.getMinZ(), e.getArray()));
        }
        
        return result;
    }
    
}