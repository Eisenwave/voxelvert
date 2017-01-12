package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.vv.core.STLModel;
import net.grian.vv.core.Vertex;
import net.grian.vv.core.VoxelArray;

public class ConverterVoxelsToSTL implements Converter<VoxelArray, STLModel> {

    private final static Vertex[] NORMALS = new Vertex[Direction.values().length];

    static {
        for (Direction dir : Direction.values())
            NORMALS[dir.ordinal()] = normalOf(dir);
    }

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
        Vertex normal = NORMALS[side.ordinal()];
        Vertex[] face = faceOf(voxel, side);

        return new STLModel.STLTriangle[] {
                new STLModel.STLTriangle(normal, face[0], face[1], face[2]),
                new STLModel.STLTriangle(normal, face[2], face[3], face[0]),
        };
    }

    private static Vertex[] faceOf(VoxelArray.Voxel voxel, Direction dir) {
        final int x = voxel.getX(), y = voxel.getY(), z = voxel.getZ();

        switch (dir) {
            case NEGATIVE_X: return new Vertex[] {
                    new Vertex(x, y,   z),
                    new Vertex(x, y+1, z),
                    new Vertex(x, y+1, z+1),
                    new Vertex(x, y,   z+1)};
            case NEGATIVE_Y: return new Vertex[] {
                    new Vertex(x,   y, z),
                    new Vertex(x+1, y, z),
                    new Vertex(x+1, y, z+1),
                    new Vertex(x,   y, z+1)};
            case NEGATIVE_Z: return new Vertex[] {
                    new Vertex(x,   y,   z),
                    new Vertex(x,   y+1, z),
                    new Vertex(x+1, y+1, z),
                    new Vertex(x+1, y,   z),};
            case POSITIVE_X: return new Vertex[] {
                    new Vertex(x+1, y,   z),
                    new Vertex(x+1, y+1, z),
                    new Vertex(x+1, y+1, z+1),
                    new Vertex(x+1, y,   z+1)};
            case POSITIVE_Y: return new Vertex[] {
                    new Vertex(x,   y+1, z),
                    new Vertex(x+1, y+1, z),
                    new Vertex(x+1, y+1, z+1),
                    new Vertex(x,   y+1, z+1)};
            case POSITIVE_Z: return new Vertex[] {
                    new Vertex(x,   y,   z+1),
                    new Vertex(x+1, y,   z+1),
                    new Vertex(x+1, y+1, z+1),
                    new Vertex(x,   y+1, z+1),};
            default: throw new IllegalArgumentException("unknown direction: "+dir);
        }
    }

    private static Vertex normalOf(Direction dir) {
        switch (dir) {
            case NEGATIVE_X: return new Vertex(-1, 0, 0);
            case POSITIVE_X: return new Vertex( 1, 0, 0);
            case NEGATIVE_Y: return new Vertex(0, -1, 0);
            case POSITIVE_Y: return new Vertex(0,  1, 0);
            case NEGATIVE_Z: return new Vertex(0, 0, -1);
            case POSITIVE_Z: return new Vertex(0, 0,  1);
            default: throw new IllegalArgumentException("unknown direction: "+dir);
        }
    }

}
