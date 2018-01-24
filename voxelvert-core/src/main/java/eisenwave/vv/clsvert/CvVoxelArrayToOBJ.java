package eisenwave.vv.clsvert;

import eisenwave.spatium.util.Flags;
import eisenwave.spatium.util.PrimMath;
import eisenwave.torrens.wavefront.*;
import eisenwave.spatium.array.IntArray3;
import eisenwave.spatium.enums.Direction;
import eisenwave.spatium.util.Incrementer2;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.object.*;
import eisenwave.torrens.voxel.VoxelArray;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CvVoxelArrayToOBJ implements Classverter<VoxelArray, OBJModel> {
    
    /** Pseudo-3D array containing all vertices of a cube. */
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
        int index = x + y * 2 + z * 4;
        if (index < 0 || index >= CUBE_VERTICES.length)
            throw new IndexOutOfBoundsException(x + "," + y + "," + z + " is not a cube vertex");
        
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
    
    /**
     * <p>
     * Returns the relative face of a cube of a certain direction.
     * <p>
     * The result is an {@code int[]} of length 4. Each element is an index in {@link #CUBE_VERTICES}.
     *
     * @param dir the direction of the face
     * @return 4 indexes of {@link #CUBE_VERTICES}
     */
    private static int[] faceOf(Direction dir) {
        return FACE_TABLE[dir.ordinal()];
    }
    
    /**
     * <p>
     * Lookup table for what vertices have to be placed for each possible visibility map of a voxel.
     * <p>
     * The index of the array is equivalent to a visibility map, which is obtainable by setting the bits of the
     * index to whether a {@link Direction} is visible with:
     * <blockquote>
     * <code>bit index = {@link Direction#ordinal()}</code>
     * </blockquote>
     * <p>
     * The byte in the array is a bit field as well with each bit representing whether a cube vertex is required
     * or not with:
     * <blockquote>
     * <code>bit index = index of {@link #CUBE_VERTICES}</code>
     * </blockquote>
     */
    private final static byte[] VERTEX_TABLE = new byte[1 << 6];
    
    static {
        Direction[] directions = Direction.values();
        
        for (int i = 0; i < VERTEX_TABLE.length; i++) {
            for (int d = 0; d < directions.length; d++) {
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
    public OBJModel invoke(@NotNull VoxelArray array, @NotNull Object... args) {
        IntArray3 vertexGrid = new IntArray3(array.getSizeX() + 1, array.getSizeY() + 1, array.getSizeZ() + 1);
        //vertexGrid.forEachIndex(((x, y, z) -> vertexGrid.set(x, y, z, -1)));
        
        OBJModel model = new OBJModel();
        addCubeNormals(model);
        
        TempVoxel[] voxels = prepareVoxels(array);
        addMaterials(voxels, model);
        
        OBJGroup group = new OBJGroup(model, "Voxels");
        model.addGroup(group);
        
        for (TempVoxel voxel : voxels)
            addVoxel(voxel, model, group, vertexGrid);
        
        return model;
    }
    
    private static void addCubeNormals(OBJModel model) {
        for (Direction dir : Direction.values()) {
            float x = (float) dir.x(), y = (float) dir.y(), z = (float) dir.z();
            
            model.addNormal(new Vertex3f(x, y, z));
        }
    }
    
    private static TempVoxel[] prepareVoxels(VoxelArray array) {
        List<TempVoxel> voxels = new ArrayList<>();
        
        array.forEachPosition((x, y, z) -> {
            if (!array.contains(x, y, z)) return;
            
            byte mask = array.getVisibilityMask(x, y, z);
            if (mask != 0)
                voxels.add(new TempVoxel(x, y, z, array.getRGB(x, y, z), mask));
        });
        
        return voxels.toArray(new TempVoxel[voxels.size()]);
    }
    
    private static void addMaterials(TempVoxel[] voxels, OBJModel model) {
        MTLLibrary mtllib = new MTLLibrary("voxel_materials.mtl");
        model.setMaterials(mtllib);
        
        MTLMaterial material = new MTLMaterial(mtllib, "Voxels");
        mtllib.addMaterial(material);
        material.setIlluminationModel(2);
        
        Texture texture = assignUV(voxels, model);
        mtllib.addMap("voxel_texture.png", texture);
        material.setDiffuseMap("voxel_texture.png");
    }
    
    private static Texture assignUV(TempVoxel[] voxels, OBJModel model) {
        final int dims = (int) PrimMath.ceil(Math.sqrt(voxels.length));
        
        Texture texture = Texture.alloc(dims, dims);
        Incrementer2 increment = new Incrementer2(dims, dims);
        
        for (TempVoxel voxel : voxels) {
            int[] uv = increment.get();
            texture.set(uv[0], uv[1], voxel.rgb);
            
            final int initIndex = model.getTextureVertexCount();
            voxel.uv[0] = initIndex + 1;
            voxel.uv[1] = initIndex + 2;
            voxel.uv[2] = initIndex + 3;
            voxel.uv[3] = initIndex + 4;
            
            float div = (float) dims;
            
            float
                minU = (uv[0] + 0.1F) / div,
                minV = (dims - uv[1] - 1 + 0.1F) / div,
                maxU = (uv[0] + 1 - 0.1F) / div,
                maxV = (dims - uv[1] - 0.1F) / div;
            
            model.addTexture(new Vertex2f(minU, minV));
            model.addTexture(new Vertex2f(minU, maxV));
            model.addTexture(new Vertex2f(maxU, maxV));
            model.addTexture(new Vertex2f(maxU, minV));
            
            increment.increment();
        }
        
        return texture;
    }
    
    private static void addVoxel(TempVoxel voxel, OBJModel model, OBJGroup group, IntArray3 vertexGrid) {
        //ensures that vertices exist in order to create faces
        initVertices(model, vertexGrid, voxel.x, voxel.y, voxel.z, voxel.mask);
        
        Direction[] directions = Direction.values();
        for (int d = 0; d < directions.length; d++) {
            
            //for this index, directions[d] is visible
            if (Flags.get(voxel.mask, d)) {
                int[] face = faceOf(directions[d]);
                
                //vertex
                int[] v = new int[face.length];
                for (int i = 0; i < face.length; i++) {
                    Vertex3i vertex = CUBE_VERTICES[face[i]];
                    v[i] = vertexGrid.get(voxel.x + vertex.getX(), voxel.y + vertex.getY(), voxel.z + vertex.getZ());
                }
                
                //face
                OBJTriplet[] triplets = new OBJTriplet[face.length];
                for (int i = 0; i < face.length; i++) {
                    //note that the direction ordinal +1 is equal to the normal index in the model
                    triplets[i] = new OBJTriplet(v[i], voxel.uv[i], d + 1);
                }
                
                group.addFace(new OBJFace(triplets));
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
        
        for (int i = 0; i < CUBE_VERTICES.length; i++) {
            if (Flags.get(vertices, i)) {
                Vertex3i relVertex = CUBE_VERTICES[i];
                final int
                    gridX = relVertex.getX() + x,
                    gridY = relVertex.getY() + y,
                    gridZ = relVertex.getZ() + z;
                
                //grid contains no vertex at that point yet
                if (vertexGrid.get(gridX, gridY, gridZ) < 1) {
                    model.addVertex(new Vertex3f(gridX, gridY, gridZ));
                    vertexGrid.set(gridX, gridY, gridZ, model.getVertexCount());
                }
            }
        }
    }
    
    private static class TempVoxel {
        
        private final int x, y, z, rgb;
        private final byte mask;
        private final int[] uv = new int[4];
        
        private TempVoxel(int x, int y, int z, int rgb, byte mask) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rgb = rgb;
            this.mask = mask;
        }
        
    }
    
}
