package eisenwave.vv.clsvert;

import eisenwave.torrens.voxel.QBMatrix;
import eisenwave.torrens.voxel.QBModel;
import eisenwave.torrens.voxel.VoxelArray;
import org.jetbrains.annotations.NotNull;

public class CvVoxelArrayToQB implements Classverter<VoxelArray, QBModel> {
    
    @Deprecated
    @Override
    public QBModel invoke(@NotNull VoxelArray array, @NotNull Object... args) {
        return invoke(array);
    }
    
    public static QBModel invoke(@NotNull VoxelArray array) {
        QBModel model = new QBModel();
        model.add(new QBMatrix(VoxelArray.class.getName(), 0, 0, 0, array));
        
        return model;
    }
    
}
