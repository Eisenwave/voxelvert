package net.grian.vv.convert;

import net.grian.torrens.object.QBMatrix;
import net.grian.torrens.object.QBModel;
import net.grian.vv.core.VoxelMesh;

public class ConverterQBToMesh implements Converter<QBModel, VoxelMesh> {

    @Override
    public Class<QBModel> getFrom() {
        return QBModel.class;
    }

    @Override
    public Class<VoxelMesh> getTo() {
        return VoxelMesh.class;
    }

    @Override
    public VoxelMesh invoke(QBModel from, Object... args) {
        VoxelMesh result = new VoxelMesh();

        for (QBMatrix matrix : from)
            result.add(matrix.getMinX(), matrix.getMinY(), matrix.getMinZ(), matrix.getVoxels());

        return result;
    }

}
