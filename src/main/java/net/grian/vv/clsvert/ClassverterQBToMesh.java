package net.grian.vv.clsvert;

import net.grian.torrens.qbcl.QBMatrix;
import net.grian.torrens.qbcl.QBModel;
import net.grian.vv.core.VoxelMesh;

public class ClassverterQBToMesh implements Classverter<QBModel, VoxelMesh> {

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
