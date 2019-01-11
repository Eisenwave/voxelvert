package eisenwave.vv.clsvert;

import eisenwave.spatium.array.*;
import eisenwave.spatium.enums.Axis;
import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.object.*;
import eisenwave.torrens.stl.*;
import eisenwave.torrens.voxel.BitArray2;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Util3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

import static eisenwave.spatium.enums.Direction.*;

public class CvVoxelArrayToSTL_Hybrid implements Classverter<VoxelArray, STLModel> {
    
    private final Logger logger;
    
    public CvVoxelArrayToSTL_Hybrid(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    private void debug(String msg) {
        if (logger != null)
            logger.fine(msg);
    }
    
    private void debugTiming(String format, long time) {
        if (logger != null) {
            time = System.currentTimeMillis() - time;
            logger.fine(String.format(format, time, (double) time / 1000));
        }
    }
    
    @Deprecated
    @Override
    public STLModel invoke(@NotNull VoxelArray from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public STLModel invoke(VoxelArray from) {
        final STLModel result = new STLModel("voxelvert_stl");
        
        final int
            limX = from.getSizeX(),
            limY = from.getSizeY(),
            limZ = from.getSizeZ();
        
        long now = System.currentTimeMillis();
        /*
         * Array containing the rectangles of each slice of each direction.
         */
        final Map<Direction, Rectangle4i[][]> rectangles = new EnumMap<>(Direction.class);
        
        // turn the occlusion (pixel) maps for each direction into rectangles
        for (Axis axis : Axis.values()) {
            final int lim = axis == Axis.X? limX : axis == Axis.Y? limY : limZ;
            Direction neg = Direction.valueOf(axis, AxisDirection.NEGATIVE);
            Direction pos = Direction.valueOf(axis, AxisDirection.POSITIVE);
            
            Rectangle4i[][][] recs = getRectangles(from, axis, lim);
            rectangles.put(neg, recs[0]);
            rectangles.put(pos, recs[1]);
        }
        debugTiming("Created rectangles:    %d ms (%.2f s)", now);
        now = System.currentTimeMillis();
        
        final BooleanArray3 vertices = new BooleanArray3(limX + 1, limY + 1, limZ + 1);
        
        // place the 3D-corners of every rectangle into the global vertex array
        for (Axis axis : Axis.values()) {
            final int lim = axis == Axis.X? limX : axis == Axis.Y? limY : limZ;
            
            for (Direction dir : Direction.valuesOf(axis)) {
                Rectangle4i[][] dirRectangles = rectangles.get(dir);
                
                for (int i = 0; i < lim; i++)
                    for (Rectangle4i rec : dirRectangles[i])
                        for (PolygonVertex v : faceOf(rec, dir, i, lim, null))
                            vertices.enable(v.getX(), v.getY(), v.getZ());
                
            }
        }
        debugTiming("Found all vertices:    %d ms (%.2f s)", now);
        now = System.currentTimeMillis();
        
        // tessellate all the rectangular polygons
        for (Axis axis : Axis.values()) {
            final int lim = axis == Axis.X? limX : axis == Axis.Y? limY : limZ;
            
            for (Direction direction : Direction.valuesOf(axis)) {
                Rectangle4i[][] dirRectangles = rectangles.get(direction);
                
                for (int i = 0; i < lim; i++) {
                    //System.err.println(direction + "@" + i + ": get " + Arrays.toString(sliceRectangles));
                    
                    for (Rectangle4i rectangle : dirRectangles[i])
                        for (STLTriangle triangle : tessellate(rectangle, direction, i, lim, vertices))
                            result.add(triangle);
                    
                    // free some memory as soon as one slice is completely done
                    dirRectangles[i] = null;
                }
            }
            
            // free some more memory as soon as one direction is completely done
            for (Direction direction : Direction.valuesOf(axis))
                rectangles.remove(direction);
        }
        debugTiming("Tessellated:           %d ms (%.2f s)", now);
        
        return result;
    }
    
    private static STLTriangle[] tessellate(Rectangle4i rectangle, Direction dir, int index, int lim,
                                            BooleanArray3 meshVertices) {
        Vertex3f normal = Util3D.vectorOf(dir.opposite());
        
        List<PolygonVertex> face = faceOf(rectangle, dir, index, lim, meshVertices);
        PolygonVertex[] vertices = face.toArray(new PolygonVertex[0]);
        assert vertices.length >= 3;
        
        if (vertices.length == 4) return new STLTriangle[] {
            new STLTriangle(normal, vertices[0].to3f(), vertices[1].to3f(), vertices[2].to3f()),
            new STLTriangle(normal, vertices[2].to3f(), vertices[3].to3f(), vertices[0].to3f())
        };
        
        return new CvPolygonTessellator_ConvexOrStraight().invoke(vertices, normal);
    }
    
    private static Rectangle4i[][][] getRectangles(VoxelArray from, Axis axis, int lim) {
        final int max = lim - 1;
        CvBitImageMerger_XY merger = new CvBitImageMerger_XY();
        
        //final int[] buffer;
        final Slicer slicer;
        switch (axis) {
            case X:
                //buffer = new int[from.getSizeY() * from.getSizeZ()];
                slicer = CvVoxelArrayToSTL_Hybrid::getPresenceYZ;
                break;
            case Y:
                //buffer = new int[from.getSizeZ() * from.getSizeX()];
                slicer = CvVoxelArrayToSTL_Hybrid::getPresenceZX;
                break;
            case Z:
                //buffer = new int[from.getSizeX() * from.getSizeY()];
                slicer = CvVoxelArrayToSTL_Hybrid::getPresenceXY;
                break;
            default: throw new AssertionError();
        }
    
        Rectangle4i[][]
            positive = new Rectangle4i[lim][],
            negative = new Rectangle4i[lim][];
    
        if (lim == 1) {
            BitArray2Adapter slice = slicer.getSlice(from, 0);
            positive[0] = merger.invoke(slice);
            negative[0] = positive[0].clone();
            return new Rectangle4i[][][] {negative, positive};
        }
        
        BitArray2Adapter prev, crnt, next;
        
        crnt = slicer.getSlice(from, 0);
        next = slicer.getSlice(from, 1);
        
        next.not();
        next.and(crnt);
        
        negative[max] = merger.invoke(next);
        positive[0] = merger.invoke(crnt);
        
        for (int i = 1; i < max; i++) {
            prev = crnt;
            crnt = slicer.getSlice(from, i);
            next = slicer.getSlice(from, i + 1);
            
            prev.not();
            next.not();
            prev.and(crnt);
            next.and(crnt);
            
            positive[i] = merger.invoke(prev);
            negative[max - i] = merger.invoke(next);
        }
        
        prev = crnt;
        crnt = slicer.getSlice(from, max);
        
        prev.not();
        prev.and(crnt);
        
        positive[max] = merger.invoke(prev);
        negative[0] = merger.invoke(crnt);
        
        return new Rectangle4i[][][] {negative, positive};
    }
    
    @SuppressWarnings("Duplicates")
    private static BitArray2Adapter getPresenceXY(VoxelArray voxels, int z) {
        final int limX = voxels.getSizeX(), limY = voxels.getSizeY();
        BitArray2Adapter result = new BitArray2Adapter(limX, limY);
        
        for (int y = 0, index = 0; y < limY; y++)
            for (int x = 0; x < limX; x++, index++)
                if (voxels.contains(x, y, z))
                    //if (ColorMath.isVisible(buffer[index]))
                    result.enable(x, y);
        return result;
    }
    
    @SuppressWarnings({"SuspiciousNameCombination", "Duplicates"})
    private static BitArray2Adapter getPresenceYZ(VoxelArray voxels, int x) {
        final int limY = voxels.getSizeY(), limZ = voxels.getSizeZ();
        BitArray2Adapter result = new BitArray2Adapter(limY, limZ);
        
        for (int z = 0, index = 0; z < limZ; z++)
            for (int y = 0; y < limY; y++, index++)
                if (voxels.contains(x, y, z))
                    result.enable(y, z);
        return result;
    }
    
    @SuppressWarnings({"SuspiciousNameCombination", "Duplicates"})
    private static BitArray2Adapter getPresenceZX(VoxelArray voxels, int y) {
        final int limZ = voxels.getSizeZ(), limX = voxels.getSizeX();
        BitArray2Adapter result = new BitArray2Adapter(limZ, limX);
        
        for (int z = 0, index = 0; z < limZ; z++)
            for (int x = 0; x < limX; x++, index++)
                if (voxels.contains(x, y, z))
                    result.enable(z, x);
        return result;
    }
    
    /**
     * Iterates in y direction and adds all the vertices on the way.
     *
     * @param vertices all vertices
     * @param target the list of vertices to add these to
     * @param x0 the first index (exclusive)
     * @param x1 the second index (exclusive)
     * @param xd the direction
     * @param y y
     * @param z z
     */
    private static void addVerticesX(BooleanArray3 vertices, List<PolygonVertex> target,
                                     int x0, int x1, int xd, int y, int z) {
        for (int x = x0 + xd; x != x1; x += xd)
            if (vertices.get(x, y, z))
                target.add(new PolygonVertex(x, y, z, false));
    }
    
    /**
     * Iterates in y direction and adds all the vertices on the way.
     *
     * @param vertices all vertices
     * @param target the list of vertices to add these to
     * @param y0 the first index (exclusive)
     * @param y1 the second index (exclusive)
     * @param yd the y-direction
     * @param z z
     * @param x x
     */
    private static void addVerticesY(BooleanArray3 vertices, List<PolygonVertex> target,
                                     int y0, int y1, int yd, int z, int x) {
        for (int y = y0 + yd; y != y1; y += yd)
            if (vertices.get(x, y, z))
                target.add(new PolygonVertex(x, y, z, false));
    }
    
    /**
     * Iterates in y direction and adds all the vertices on the way.
     *
     * @param vertices all vertices
     * @param target the list of vertices to add these to
     * @param z0 the first index (exclusive)
     * @param z1 the second index (exclusive)
     * @param zd the y-direction
     * @param x x
     * @param y y
     */
    private static void addVerticesZ(BooleanArray3 vertices, List<PolygonVertex> target,
                                     int z0, int z1, int zd, int x, int y) {
        for (int z = z0 + zd; z != z1; z += zd)
            if (vertices.get(x, y, z))
                target.add(new PolygonVertex(x, y, z, false));
    }
    
    @SuppressWarnings({"UnnecessaryLocalVariable", "Duplicates"})
    private static List<PolygonVertex> faceOf(Rectangle4i rec, Direction camDir, int index, int lim,
                                              @Nullable BooleanArray3 vertices) {
        // whether t-junctions should be resolved
        final boolean tjunctions = vertices != null && !rec.isSingularity();
        
        switch (camDir) {
            case NEGATIVE_X: {
                final int x = lim - index,
                    ymin = rec.getMinX(), zmin = rec.getMinY(),
                    ymax = rec.getMaxX() + 1, zmax = rec.getMaxY() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(x, ymin, zmin, true),
                    new PolygonVertex(x, ymax, zmin, true),
                    new PolygonVertex(x, ymax, zmax, true),
                    new PolygonVertex(x, ymin, zmax, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(x, ymin, zmin, true));
                    addVerticesY(vertices, result, ymin, ymax, 1, zmin, x);
                    result.add(new PolygonVertex(x, ymax, zmin, true));
                    addVerticesZ(vertices, result, zmin, zmax, 1, x, ymax);
                    result.add(new PolygonVertex(x, ymax, zmax, true));
                    addVerticesY(vertices, result, ymax, ymin, -1, zmax, x);
                    result.add(new PolygonVertex(x, ymin, zmax, true));
                    addVerticesZ(vertices, result, zmax, zmin, -1, x, ymin);
                    return result;
                }
            }
            case POSITIVE_X: {
                final int x = index,
                    ymin = rec.getMinX(), zmin = rec.getMinY(),
                    ymax = rec.getMaxX() + 1, zmax = rec.getMaxY() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(x, ymin, zmax, true),
                    new PolygonVertex(x, ymax, zmax, true),
                    new PolygonVertex(x, ymax, zmin, true),
                    new PolygonVertex(x, ymin, zmin, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(x, ymin, zmax, true));
                    addVerticesY(vertices, result, ymin, ymax, 1, zmax, x);
                    result.add(new PolygonVertex(x, ymax, zmax, true));
                    addVerticesZ(vertices, result, zmax, zmin, -1, x, ymax);
                    result.add(new PolygonVertex(x, ymax, zmin, true));
                    addVerticesY(vertices, result, ymax, ymin, -1, zmin, x);
                    result.add(new PolygonVertex(x, ymin, zmin, true));
                    addVerticesZ(vertices, result, zmin, zmax, 1, x, ymin);
                    return result;
                }
            }
            case NEGATIVE_Y: {
                final int y = lim - index,
                    xmin = rec.getMinY(), zmin = rec.getMinX(),
                    xmax = rec.getMaxY() + 1, zmax = rec.getMaxX() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(xmin, y, zmax, true),
                    new PolygonVertex(xmax, y, zmax, true),
                    new PolygonVertex(xmax, y, zmin, true),
                    new PolygonVertex(xmin, y, zmin, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(xmin, y, zmax, true));
                    addVerticesX(vertices, result, xmin, xmax, 1, y, zmax);
                    result.add(new PolygonVertex(xmax, y, zmax, true));
                    addVerticesZ(vertices, result, zmax, zmin, -1, xmax, y);
                    result.add(new PolygonVertex(xmax, y, zmin, true));
                    addVerticesX(vertices, result, xmax, xmin, -1, y, zmin);
                    result.add(new PolygonVertex(xmin, y, zmin, true));
                    addVerticesZ(vertices, result, zmin, zmax, 1, xmin, y);
                    return result;
                }
            }
            case POSITIVE_Y: {
                final int y = index,
                    xmin = rec.getMinY(), zmin = rec.getMinX(),
                    xmax = rec.getMaxY() + 1, zmax = rec.getMaxX() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(xmin, y, zmin, true),
                    new PolygonVertex(xmax, y, zmin, true),
                    new PolygonVertex(xmax, y, zmax, true),
                    new PolygonVertex(xmin, y, zmax, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(xmin, y, zmin, true));
                    addVerticesX(vertices, result, xmin, xmax, 1, y, zmin);
                    result.add(new PolygonVertex(xmax, y, zmin, true));
                    addVerticesZ(vertices, result, zmin, zmax, 1, xmax, y);
                    result.add(new PolygonVertex(xmax, y, zmax, true));
                    addVerticesX(vertices, result, xmax, xmin, -1, y, zmax);
                    result.add(new PolygonVertex(xmin, y, zmax, true));
                    addVerticesZ(vertices, result, zmax, zmin, -1, xmin, y);
                    return result;
                }
            }
            case NEGATIVE_Z: {
                final int z = lim - index,
                    xmin = rec.getMinX(), ymin = rec.getMinY(),
                    xmax = rec.getMaxX() + 1, ymax = rec.getMaxY() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(xmin, ymin, z, true),
                    new PolygonVertex(xmax, ymin, z, true),
                    new PolygonVertex(xmax, ymax, z, true),
                    new PolygonVertex(xmin, ymax, z, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(xmin, ymin, z, true));
                    addVerticesX(vertices, result, xmin, xmax, 1, ymin, z);
                    result.add(new PolygonVertex(xmax, ymin, z, true));
                    addVerticesY(vertices, result, ymin, ymax, 1, z, xmax);
                    result.add(new PolygonVertex(xmax, ymax, z, true));
                    addVerticesX(vertices, result, xmax, xmin, -1, ymax, z);
                    result.add(new PolygonVertex(xmin, ymax, z, true));
                    addVerticesY(vertices, result, ymax, ymin, -1, z, xmin);
                    return result;
                }
            }
            case POSITIVE_Z: {
                final int z = index,
                    xmin = rec.getMinX(), ymin = rec.getMinY(),
                    xmax = rec.getMaxX() + 1, ymax = rec.getMaxY() + 1;
                if (!tjunctions) return Arrays.asList(
                    new PolygonVertex(xmin, ymin, z, true),
                    new PolygonVertex(xmin, ymax, z, true),
                    new PolygonVertex(xmax, ymax, z, true),
                    new PolygonVertex(xmax, ymin, z, true)
                );
                else {
                    List<PolygonVertex> result = new ArrayList<>(8);
                    result.add(new PolygonVertex(xmin, ymin, z, true));
                    addVerticesY(vertices, result, ymin, ymax, 1, z, xmin);
                    result.add(new PolygonVertex(xmin, ymax, z, true));
                    addVerticesX(vertices, result, xmin, xmax, 1, ymax, z);
                    result.add(new PolygonVertex(xmax, ymax, z, true));
                    addVerticesY(vertices, result, ymax, ymin, -1, z, xmax);
                    result.add(new PolygonVertex(xmax, ymin, z, true));
                    addVerticesX(vertices, result, xmax, xmin, -1, ymin, z);
                    return result;
                }
            }
            default: throw new IllegalArgumentException("unknown direction: " + camDir);
        }
    }
    
    // SUBCLASSES
    
    @FunctionalInterface
    private static interface Slicer {
        
        public BitArray2Adapter getSlice(VoxelArray voxels, int index);
        
    }
    
    static class PolygonVertex extends Vertex3i {
        
        private Vertex3f f = null;
        private boolean convex;
        
        public PolygonVertex(int x, int y, int z, boolean isCorner) {
            super(x, y, z);
            this.convex = isCorner;
        }
        
        public Vertex3f to3f() {
            return f == null? f = new Vertex3f(getX(), getY(), getZ()) : f;
        }
        
        public boolean isConvex() {
            return convex;
        }
        
        public void setConvex(boolean convex) {
            this.convex = convex;
        }
        
        @Override
        public String toString() {
            return (convex? "C" : "S") + super.toString();
        }
    }
    
    private static class BitArray2Adapter extends BooleanArray2 implements BitArray2 {
        
        public BitArray2Adapter(int x, int y) {
            super(x, y);
        }
        
        @Override
        public int getArea() {
            return 0;
        }
        
        @Override
        public boolean contains(int x, int y) {
            return get(x, y);
        }
        
        @Override
        public boolean contains(Vertex2i pos) {
            return get(pos.getX(), pos.getY());
        }
        
    }
    
}
