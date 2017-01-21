package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.object.STLModel;
import net.grian.torrens.object.Vertex3f;
import net.grian.vv.util.Util3D;

public class ConverterVoxelsToSTL implements Converter<VoxelArray, STLModel> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<STLModel> getTo() {
        return STLModel.class;
    }

    @Override
    public STLModel invoke(VoxelArray from, Object... args) {
        return invoke(from);
    }

    public STLModel invoke(VoxelArray from) {
        final STLModel result = new STLModel("voxelvert_stl");

        from.forEach(voxel -> {
            for (Direction dir : Direction.values()) if (voxel.isVisible(dir)) {
                STLModel.STLTriangle[] triangles = toTriangles(voxel, dir);
                result.add(triangles[0]);
                result.add(triangles[1]);
            }
        });

        return result;
    }

    private static STLModel.STLTriangle[] toTriangles(VoxelArray.Voxel voxel, Direction side) {
        Vertex3f normal = Util3D.normalOf(side);
        Vertex3f[] face = faceOf(voxel, side);

        return new STLModel.STLTriangle[] {
                new STLModel.STLTriangle(normal, face[0], face[1], face[2]),
                new STLModel.STLTriangle(normal, face[2], face[3], face[0]),
        };
    }

    private static Vertex3f[] faceOf(VoxelArray.Voxel voxel, Direction dir) {
        final int x = voxel.getX(), y = voxel.getY(), z = voxel.getZ();

        switch (dir) {
            case NEGATIVE_X: return new Vertex3f[] {
                    new Vertex3f(x, y,   z),
                    new Vertex3f(x, y+1, z),
                    new Vertex3f(x, y+1, z+1),
                    new Vertex3f(x, y,   z+1)};
            case NEGATIVE_Y: return new Vertex3f[] {
                    new Vertex3f(x,   y, z),
                    new Vertex3f(x+1, y, z),
                    new Vertex3f(x+1, y, z+1),
                    new Vertex3f(x,   y, z+1)};
            case NEGATIVE_Z: return new Vertex3f[] {
                    new Vertex3f(x,   y,   z),
                    new Vertex3f(x,   y+1, z),
                    new Vertex3f(x+1, y+1, z),
                    new Vertex3f(x+1, y,   z),};
            case POSITIVE_X: return new Vertex3f[] {
                    new Vertex3f(x+1, y,   z),
                    new Vertex3f(x+1, y+1, z),
                    new Vertex3f(x+1, y+1, z+1),
                    new Vertex3f(x+1, y,   z+1)};
            case POSITIVE_Y: return new Vertex3f[] {
                    new Vertex3f(x,   y+1, z),
                    new Vertex3f(x+1, y+1, z),
                    new Vertex3f(x+1, y+1, z+1),
                    new Vertex3f(x,   y+1, z+1)};
            case POSITIVE_Z: return new Vertex3f[] {
                    new Vertex3f(x,   y,   z+1),
                    new Vertex3f(x+1, y,   z+1),
                    new Vertex3f(x+1, y+1, z+1),
                    new Vertex3f(x,   y+1, z+1),};
            default: throw new IllegalArgumentException("unknown direction: "+dir);
        }
    }

}
