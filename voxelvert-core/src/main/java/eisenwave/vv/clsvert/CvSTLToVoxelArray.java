package eisenwave.vv.clsvert;

import eisenwave.spatium.util.PrimMath;
import eisenwave.torrens.object.BoundingBox6f;
import eisenwave.torrens.object.Vertex3f;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.stl.STLTriangle;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class CvSTLToVoxelArray implements Classverter<STLModel, VoxelArray> {
    
    @Override
    public Class<STLModel> getFrom() {
        return STLModel.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Nullable
    private final Logger logger;
    
    public CvSTLToVoxelArray(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    public CvSTLToVoxelArray() {
        this(null);
    }
    
    private void warning(String msg) {
        if (logger != null)
            logger.warning(msg);
    }
    
    private void debug(String msg) {
        if (logger != null)
            logger.fine(msg);
    }
    
    @Override
    public VoxelArray invoke(@NotNull STLModel from, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args, Number.class);
        
        return invoke(from, ((Number) args[0]).intValue());
    }
    
    public VoxelArray invoke(STLModel model, int res) {
        BoundingBox6f bounds = model.getBoundaries();
        Transformation transform = transformOf(bounds, res);
        
        Vertex3f dims = transform.transform(bounds.getMax());
        VoxelArray canvas = new VoxelArray(
            (int) dims.getX() + 1,
            (int) dims.getY() + 1,
            (int) dims.getZ() + 1);
        
        if (logger != null)
            logger.fine(String.format("voxelizing %s sized model using %s canvas", dims, canvas));
        
        for (STLTriangle triangle : model.getTriangles()) {
            Vertex3f
                a = transform.transform(triangle.getA()),
                b = transform.transform(triangle.getB()),
                c = transform.transform(triangle.getC());
            
            drawTriangle(a, b, c, canvas);
        }
        
        return canvas;
    }
    
    @NotNull
    private static Transformation transformOf(BoundingBox6f box, int res) {
        Vertex3f translation = box.getMin().multiplied(-1);
        
        float maxDim = PrimMath.max(box.getSizeX(), box.getSizeY(), box.getSizeZ());
        float scale = (res - 1) / maxDim;
        
        return v -> v.plus(translation).multiplied(scale);
    }
    
    @SuppressWarnings("Duplicates")
    private static void drawTriangle(Vertex3f a, Vertex3f b, Vertex3f c, VoxelArray canvas) {
        
        if (isOneSideTooLong(a, b, c)) {
            Vertex3f ab = a.midPoint(b), bc = b.midPoint(c), ca = c.midPoint(a);
            
            drawTriangle(a, ab, ca, canvas);
            drawTriangle(b, bc, ab, canvas);
            drawTriangle(c, ca, bc, canvas);
            drawTriangle(ab, bc, ca, canvas);
        }
        
        else {
            drawVertex(a, canvas);
            drawVertex(b, canvas);
            drawVertex(c, canvas);
        }
    }
    
    private static void drawVertex(Vertex3f v, VoxelArray canvas) {
        canvas.setRGB((int) v.getX(), (int) v.getY(), (int) v.getZ(), ColorMath.SOLID_WHITE);
    }
    
    // UTIL
    
    @FunctionalInterface
    private static interface Transformation {
        
        abstract Vertex3f transform(Vertex3f v);
        
    }
    
    private final static float MAX_EDGE = 1 - 1E-6F;
    
    private static boolean isOneSideTooLong(Vertex3f a, Vertex3f b, Vertex3f c) {
        return b.minus(a).getLengthSquared() > MAX_EDGE
            || c.minus(b).getLengthSquared() > MAX_EDGE
            || a.minus(c).getLengthSquared() > MAX_EDGE;
    }
    
}
