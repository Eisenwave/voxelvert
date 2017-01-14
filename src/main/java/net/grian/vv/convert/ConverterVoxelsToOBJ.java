package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.vv.core.OBJFace;
import net.grian.vv.core.OBJModel;
import net.grian.vv.core.Vertex3f;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;

public class ConverterVoxelsToOBJ implements Converter<VoxelArray, OBJModel> {

    private final static OBJPreset[] PRESETS = new OBJPreset[1<<6];

    private static boolean isVisible(byte visibility, Direction face) {
        return (visibility >> face.ordinal() & 1) != 0;
    }

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<OBJModel> getTo() {
        return OBJModel.class;
    }

    @Override
    public OBJModel invoke(VoxelArray from, Object... args) {
        Arguments.requireNonnull(from, args);

        OBJModel model = new OBJModel();

        return model;
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

    private static class OBJPreset {

        private final byte vertices;

        private final OBJFace[] faces;

        public OBJPreset(byte vertices, OBJFace[] faces) {
            this.vertices = vertices;
            this.faces = faces;
        }

        public byte getVertices() {
            return vertices;
        }

        public OBJFace[] getFaces() {
            return faces;
        }

    }

}
