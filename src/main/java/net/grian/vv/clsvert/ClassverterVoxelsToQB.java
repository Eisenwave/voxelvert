package net.grian.vv.clsvert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.QBMatrix;
import net.grian.torrens.qbcl.QBModel;

public class ClassverterVoxelsToQB implements Classverter<VoxelArray, QBModel> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<QBModel> getTo() {
        return QBModel.class;
    }

    @Override
    public QBModel invoke(VoxelArray array, Object... args) {
        QBModel model = new QBModel();
        model.add(new QBMatrix(VoxelArray.class.getName(), 0, 0, 0, array));

        return model;
    }

}
