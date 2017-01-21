package net.grian.vv.convert;


import net.grian.torrens.object.QBMatrix;
import net.grian.torrens.object.QBModel;
import net.grian.vv.core.VoxelMesh;

public class ConverterMeshToQB implements Converter<VoxelMesh, QBModel> {

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
