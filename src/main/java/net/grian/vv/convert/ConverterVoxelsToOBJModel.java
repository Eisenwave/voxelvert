package net.grian.vv.convert;

import net.grian.spatium.array.IntArray3;
import net.grian.spatium.enums.Direction;
import net.grian.spatium.util.Flags;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.object.*;
import net.grian.vv.util.Arguments;
import net.grian.vv.util.Util3D;

public class ConverterVoxelsToOBJModel implements Converter<VoxelArray, OBJModel> {

    /** Pseudo 3D-array containing all vertices of a cube. */
    private final static Vertex3i[] CUBE_VERTICES = new Vertex3i[8];
    static {
        CUBE_VERTICES[0] = Vertex3i.ZERO;
        CUBE_VERTICES[1] = new Vertex3i(1, 0, 0);
        CUBE_VERTICES[2] = new Vertex3i(0, 1, 0);
        CUBE_VERTICES[3] = new Vertex3i(1, 1, 0);
        CUBE_VERTICES[4] = new Vertex3i(0, 0, 1);
        CUBE_VERTICES[5] = new Vertex3i(1, 0, 1);
        CUBE_VERTICES[6] = new Vertex3i(0, 1, 1);
        CUBE_VERTICES[7] = new Vertex3i(1, 1, 1);
    }

    /**
     * Looks up the index of a cube vertex using three given coordinates (range 0-1).
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the index as defined in {@link #CUBE_VERTICES}
     */
    private static int cubeIndex(int x, int y, int z) {
        int index = x + y*2 + z*4;
        if (index < 0 || index >= CUBE_VERTICES.length)
            throw new IllegalArgumentException(x+","+y+","+z+" is not a cube vertex");

        return index;
    }

    /**
     * Looks up the index of a cube vertex using three given coordinates (range 0-1).
     *
     * @param vertex the coordinates
     * @return the index as defined in {@link #CUBE_VERTICES}
     */
    private static int cubeIndex(Vertex3i vertex) {
        return cubeIndex(vertex.getX(), vertex.getY(), vertex.getZ());
    }

    private final static int[][] FACE_TABLE = new int[Direction.values().length][4];
    static {
        FACE_TABLE[Direction.NEGATIVE_X.ordinal()] = new int[] {
                cubeIndex(0, 0, 0),
                cubeIndex(0, 0, 1),
                cubeIndex(0, 1, 1),
                cubeIndex(0, 1, 0)};
        FACE_TABLE[Direction.NEGATIVE_Y.ordinal()] = new int[] {
                cubeIndex(0, 0, 0),
                cubeIndex(1, 0, 0),
                cubeIndex(1, 0, 1),
                cubeIndex(0, 0, 1)};
        FACE_TABLE[Direction.NEGATIVE_Z.ordinal()] = new int[] {
                cubeIndex(0, 0, 0),
                cubeIndex(0, 1, 0),
                cubeIndex(1, 1, 0),
                cubeIndex(1, 0, 0)};
        FACE_TABLE[Direction.POSITIVE_X.ordinal()] = new int[] {
                cubeIndex(1, 0, 0),
                cubeIndex(1, 1, 0),
                cubeIndex(1, 1, 1),
                cubeIndex(1, 0, 1)};
        FACE_TABLE[Direction.POSITIVE_Y.ordinal()] = new int[] {
                cubeIndex(0, 1, 0),
                cubeIndex(0, 1, 1),
                cubeIndex(1, 1, 1),
                cubeIndex(1, 1, 0)};
        FACE_TABLE[Direction.POSITIVE_Z.ordinal()] = new int[] {
                cubeIndex(0, 0, 1),
                cubeIndex(1, 0, 1),
                cubeIndex(1, 1, 1),
                cubeIndex(0, 1, 1)};
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

    /**
     * <p>
     *     Returns the relative face of a cube of a certain direction.
     * </p>
     * <p>
     *     The result is an int[] of length 4. Each element is an index in {@link #CUBE_VERTICES}.
     * </p>
     *
     * @param dir the direction of the face
     * @return 4 indexes of {@link #CUBE_VERTICES}
     */
    private static int[] faceOf(Direction dir) {
        return FACE_TABLE[dir.ordinal()];
    }

    /**
     * <p>
     *     Lookup table for what vertices have to be placed for each possible visibility map of a voxel.
     * </p>
     * <p>
     *     The index of the array is equivalent to a visibility map, which is obtainable by setting the bits of the
     *     index to whether a {@link Direction} is visible with:
     *     <blockquote>
     *         <code>bit index = {@link Direction#ordinal()}</code>
     *     </blockquote>
     * </p>
     * <p>
     *     The byte in the array is a bit field as well with each bit representing whether a cube vertex is required
     *     or not with:
     *     <blockquote>
     *         <code>bit index = index of {@link #CUBE_VERTICES}</code>
     *     </blockquote>
     * </p>
     */
    private final static byte[] VERTEX_TABLE = new byte[1<<6];
    static {
        Direction[] directions = Direction.values();

        for (int i = 0; i<VERTEX_TABLE.length; i++) {
            for (int d = 0; d<directions.length; d++) {
                //for this index, directions[d] is visible
                if (Flags.get(i, d)) {
                    int[] face = faceOf(directions[d]);
                    for (int vertex : face)
                        VERTEX_TABLE[i] = Flags.enable(VERTEX_TABLE[i], vertex);
                }
            }
        }
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
    public OBJModel invoke(VoxelArray voxels, Object... args) {
        Arguments.requireNonnull(voxels, args);

        IntArray3 vertexGrid = new IntArray3(voxels.getSizeX()+1, voxels.getSizeY()+1, voxels.getSizeZ()+1);
        //vertexGrid.forEachIndex(((x, y, z) -> vertexGrid.set(x, y, z, -1)));

        OBJModel model = new OBJModel();
        addCubeNormals(model);

        voxels.forEach(voxel -> addVoxel(voxel, model, vertexGrid));

        return model;
    }

    private static void addCubeNormals(OBJModel model) {
        for (Direction direction : Direction.values())
            model.addNormal(Util3D.normalOf(direction));
    }

    private static void addVoxel(VoxelArray.Voxel voxel, OBJModel model, IntArray3 vertexGrid) {
        final byte visibility = voxel.getVisibilityMap();
        final int x = voxel.getX(), y = voxel.getY(), z = voxel.getZ();

        //ensures that vertices exist in order to create faces
        initVertices(model, vertexGrid, x, y, z, visibility);

        Direction[] directions = Direction.values();
        for (int d = 0; d<directions.length; d++) {

            //for this index, directions[d] is visible
            if (Flags.get(visibility, d)) {
                int[] face = faceOf(directions[d]);

                //vertex texture
                int[] vt = new int[face.length]; //TODO add textures
                for (int i = 0; i<face.length; i++)
                    vt[i] = -1;

                //vertex
                int[] v = new int[face.length];
                for (int i = 0; i<face.length; i++) {
                    Vertex3i vertex = CUBE_VERTICES[face[i]];
                    v[i] = vertexGrid.get(x+vertex.getX(), y+vertex.getY(), z+vertex.getZ());
                }

                //face
                OBJTriplet[] triplets = new OBJTriplet[face.length];
                for (int i = 0; i<face.length; i++) {
                    //note that the direction ordinal +1 is equal to the normal index in the model
                    triplets[i] = new OBJTriplet(v[i], vt[i], d+1);
                }

                model.addFace(new OBJFace(triplets));
            }
        }


    }

    /**
     * Ensures that all vertices of a voxel with given visibility are initialized in the model and referenced by the
     * vertex grid.
     *
     * @param model the model
     * @param vertexGrid the grid of vertices
     * @param x the voxel x-coordinate
     * @param y the voxel y-coordinate
     * @param z the voxel z-coordinate
     * @param visibility the voxel visibility map
     */
    private static void initVertices(OBJModel model, IntArray3 vertexGrid, int x, int y, int z, byte visibility) {
        byte vertices = VERTEX_TABLE[visibility];

        for (int i = 0; i<CUBE_VERTICES.length; i++) {
            if (Flags.get(vertices, i)) {
                Vertex3i relVertex = CUBE_VERTICES[i];
                final int
                        gridX = relVertex.getX()+x,
                        gridY = relVertex.getY()+y,
                        gridZ = relVertex.getZ()+z;

                //grid contains no vertex at that point yet
                if (vertexGrid.get(gridX, gridY, gridZ) < 1) {
                    model.addVertex(new Vertex3f(gridX, gridY, gridZ));
                    vertexGrid.set(gridX, gridY, gridZ, model.getVertexCount());
                }

            }
        }
    }

}
