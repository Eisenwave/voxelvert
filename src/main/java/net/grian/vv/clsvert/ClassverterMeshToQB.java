package net.grian.vv.clsvert;


import net.grian.torrens.qbcl.QBMatrix;
import net.grian.torrens.qbcl.QBModel;
import net.grian.vv.core.VoxelMesh;

public class ClassverterMeshToQB implements Classverter<VoxelMesh, QBModel> {

    @Override
    public Class<VoxelMesh> getFrom() {
        return VoxelMesh.class;
    }

    @Override
    public Class<QBModel> getTo() {
        return QBModel.class;
    }

    @Override
    public QBModel invoke(VoxelMesh from, Object... args) {
        QBModel result = new QBModel();

        int i = 0;
        for (VoxelMesh.Element e : from) {
            String name = "matrix"+i;
            result.add(new QBMatrix(name, e.getMinX(), e.getMinY(), e.getMinZ(), e.getArray()));
        }

        return result;
    }

}
