package net.grian.vv.convert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.object.QBMatrix;
import net.grian.torrens.object.QBModel;

public class ConverterVoxelsToQB implements Converter<VoxelArray, QBModel> {

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
