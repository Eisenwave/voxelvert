package eisenwave.vv.clsvert;

import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.object.Vertex3f;
import eisenwave.torrens.stl.STLTriangle;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Util3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class CvVoxelArrayToSTL_Naive implements Classverter<VoxelArray, STLModel> {
    
    private final Logger logger;
    
    public CvVoxelArrayToSTL_Naive(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    private void debug(String msg) {
        if (logger != null)
            logger.fine(msg);
    }
    
    @Deprecated
    @Override
    public STLModel invoke(@NotNull VoxelArray from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public STLModel invoke(VoxelArray from) {
        final STLModel result = new STLModel("voxelvert_stl");
        
        from.forEach(voxel -> {
            for (Direction dir : Direction.values()) {
                if (voxel.isVisible(dir)) {
                    STLTriangle[] triangles = toTriangles(voxel, dir);
                    result.add(triangles[0]);
                    result.add(triangles[1]);
                }
            }
        });
        
        return result;
    }
    
    private static STLTriangle[] toTriangles(VoxelArray.Voxel voxel, Direction side) {
        Vertex3f normal = Util3D.vectorOf(side);
        Vertex3f[] face = faceOf(voxel, side);
        //PrimArrays.flip(face);
        
        return new STLTriangle[] {
            new STLTriangle(normal, face[0], face[1], face[2]),
            new STLTriangle(normal, face[2], face[3], face[0]),
        };
    }
    
    private static Vertex3f[] faceOf(VoxelArray.Voxel voxel, Direction dir) {
        final int x = voxel.getX(), y = voxel.getY(), z = voxel.getZ();
        
        switch (dir) {
            case NEGATIVE_X: return new Vertex3f[] {
                new Vertex3f(x, y, z + 1),
                new Vertex3f(x, y + 1, z + 1),
                new Vertex3f(x, y + 1, z),
                new Vertex3f(x, y, z)
            };
            case NEGATIVE_Y: return new Vertex3f[] {
                new Vertex3f(x, y, z),
                new Vertex3f(x + 1, y, z),
                new Vertex3f(x + 1, y, z + 1),
                new Vertex3f(x, y, z + 1)
            };
            case NEGATIVE_Z: return new Vertex3f[] {
                new Vertex3f(x, y, z),
                new Vertex3f(x, y + 1, z),
                new Vertex3f(x + 1, y + 1, z),
                new Vertex3f(x + 1, y, z)
            };
            case POSITIVE_X: return new Vertex3f[] {
                new Vertex3f(x + 1, y, z),
                new Vertex3f(x + 1, y + 1, z),
                new Vertex3f(x + 1, y + 1, z + 1),
                new Vertex3f(x + 1, y, z + 1)
            };
            case POSITIVE_Y: return new Vertex3f[] {
                new Vertex3f(x, y + 1, z + 1),
                new Vertex3f(x + 1, y + 1, z + 1),
                new Vertex3f(x + 1, y + 1, z),
                new Vertex3f(x, y + 1, z)
            };
            case POSITIVE_Z: return new Vertex3f[] {
                new Vertex3f(x, y, z + 1),
                new Vertex3f(x + 1, y, z + 1),
                new Vertex3f(x + 1, y + 1, z + 1),
                new Vertex3f(x, y + 1, z + 1)
            };
            default: throw new IllegalArgumentException("unknown direction: " + dir);
        }
    }
    
}
