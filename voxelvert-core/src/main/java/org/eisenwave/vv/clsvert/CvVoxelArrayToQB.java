package org.eisenwave.vv.clsvert;

import net.grian.torrens.voxel.QBMatrix;
import net.grian.torrens.voxel.QBModel;
import net.grian.torrens.voxel.VoxelArray;
import org.jetbrains.annotations.NotNull;

public class CvVoxelArrayToQB implements Classverter<VoxelArray, QBModel> {
    
    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }
    
    @Override
    public Class<QBModel> getTo() {
        return QBModel.class;
    }
    
    @Override
    public QBModel invoke(@NotNull VoxelArray array, @NotNull Object... args) {
        QBModel model = new QBModel();
        model.add(new QBMatrix(VoxelArray.class.getName(), 0, 0, 0, array));
        
        return model;
    }
    
}
