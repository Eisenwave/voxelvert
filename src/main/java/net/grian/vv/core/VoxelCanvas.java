package net.grian.vv.core;

import net.grian.spatium.Spatium;
import net.grian.spatium.array.BooleanArray3;
import net.grian.spatium.coll.Distances;
import net.grian.spatium.function.*;
import net.grian.spatium.geo.*;
import net.grian.spatium.iter.PathIterator;
import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;

import java.util.function.Consumer;

public class VoxelCanvas {

    private final VoxelArray content;
    private final BooleanArray3 selection;

    private final int sizeX, sizeY, sizeZ;

    public VoxelCanvas(int x, int y, int z) {
        this.content = new VoxelArray(x, y, z);
        this.selection = new BooleanArray3(x, y, z);
        this.sizeX = x;
        this.sizeY = y;
        this.sizeZ = z;

        forEachPosition(selection::enable);
    }

    public VoxelCanvas(VoxelArray content) {
        this(content.getSizeX(), content.getSizeY(), content.getSizeZ());
        drawVoxels(content);
    }

    /**
     * Returns a copy of the current canvas content.
     *
     * @return a copy of the current canvas content
     */
    public VoxelArray getContent() {
        return content.clone();
    }

    /**
     * Returns a copy of the current canvas selection.
     *
     * @return a copy of the current canvas selection
     */
    public BooleanArray3 getSelection() {
        return selection;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public int getVolume() {
        return sizeX * sizeY * sizeZ;
    }

    public int contentSize() {
        return content.size();
    }

    public int selectionSize() {
        return selection.size();
    }

    // DRAW

    public void draw(int x, int y, int z, int rgb) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            internalDraw(x, y, z, rgb);
    }

    public void draw(BlockVector pos, int rgb) {
        draw(pos.getX(), pos.getY(), pos.getZ(), rgb);
    }

    private void internalDraw(int x, int y, int z, int rgb) {
        if (selection.contains(x, y, z))
            content.setRGB(x, y, z, rgb);
    }

    public void drawVoxel(VoxelArray.Voxel voxel) {
        draw(voxel.getX(), voxel.getY(), voxel.getZ(), voxel.getRGB());
    }

    public void drawVoxels(VoxelArray array, int x, int y, int z) {
        array.forEach( voxel -> draw(voxel.getX()+x, voxel.getY()+y, voxel.getZ()+z, voxel.getRGB()) );
    }

    public void drawVoxels(VoxelArray array) {
        array.forEach(this::drawVoxel);
    }

    public void drawRaw(Int3IntFunction function) {
        forEachPosition( (x, y, z) -> internalDraw(x, y, z, function.apply(x, y, z)) );
    }

    public void drawIf(Int3Predicate predicate, int rgb) {
        forEachPosition( (x, y, z) -> {
            if (predicate.test(x, y, z)) internalDraw(x, y, z, rgb);
        } );
    }

    public void drawPath(Path path, float interval, int rgb) {
        PathIterator iterator = new PathIterator(path, interval);
        while (iterator.hasNext())
            draw(iterator.next().toBlockVector(), rgb);
    }

    public void drawPath(Path path, int steps, int rgb) {
        PathIterator iterator = new PathIterator(path, steps);
        while (iterator.hasNext())
            draw(iterator.next().toBlockVector(), rgb);
    }

    public void drawSpace(Space space, int rgb) {
        drawIf(space::contains, rgb);
    }

    /**
     * Draws a box from one voxel to another.
     *
     * @param x0 the first voxel x
     * @param y0 the first voxel y
     * @param z0 the first voxel z
     * @param x1 the second voxel x
     * @param y1 the second voxel y
     * @param z1 the second voxel z
     * @param rgb the line color
     */
    public void drawBox(int x0, int y0, int z0, int x1, int y1, int z1, int rgb) {
        internalDrawBox(
                Math.max(Math.min(x0, x1), 0),
                Math.max(Math.min(y0, y1), 0),
                Math.max(Math.min(z0, z1), 0),
                Math.min(Math.max(x0, x1), sizeX-1),
                Math.min(Math.max(y0, y1), sizeY-1),
                Math.min(Math.max(z0, z1), sizeZ-1),
                rgb);
    }

    private void internalDrawBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int rgb) {
        for (int x = minX; x<maxX; x++)
            for (int y = minY; y<maxY; y++)
                for (int z = minZ; z<maxZ; z++)
                    internalDraw(x, y, z, rgb);
    }

    /**
     * Draws an ellipse from one voxel to another.
     *
     * @param x0 the first voxel x
     * @param y0 the first voxel y
     * @param z0 the first voxel z
     * @param x1 the second voxel x
     * @param y1 the second voxel y
     * @param z1 the second voxel z
     * @param rgb the line color
     */
    public void drawEllipse(int x0, int y0, int z0, int x1, int y1, int z1, int rgb) {
        internalDrawEllipse(
                (x0 + x1) / 2,
                (y0 + y1) / 2,
                (z0 + z1) / 2,
                Math.abs(x1 - x0),
                Math.abs(y1 - y0),
                Math.abs(z1 - z0),
                rgb);
    }
    
    /**
     * Draws a triangle into the canvas.
     *
     * @param triangle the triangle
     * @param rgb the color
     */
    public void drawTriangle(Triangle triangle, int rgb) {
        Vector
            a = triangle.getA(),
            ab = Vector.between(a, triangle.getB()),
            ac = Vector.between(a, triangle.getC());
        
        float //subtract epsilon to account for imprecision and prevent holes
            incrB = (float) (1 / Distances.hypotCubical(ab) - Spatium.EPSILON),
            incrC = (float) (1 / Distances.hypotCubical(ac) - Spatium.EPSILON);
        
        for (float baryB = 0; baryB <= 1; baryB += incrB) {
            Vector offB = ab.clone().multiply(baryB);
            
            for (float baryC = 0; baryC <= 1; baryC += incrC) {
                if (baryB + baryC > 1) break;
                Vector offC = ac.clone().multiply(baryC);
    
                draw(
                    (int) (a.getX() + offB.getX() + offC.getX()),
                    (int) (a.getY() + offB.getY() + offC.getY()),
                    (int) (a.getZ() + offB.getZ() + offC.getZ()),
                    rgb);
            }
        }
    }
    
    /**
     * Draws a triangle into the canvas.
     *
     * @param triangle the triangle
     * @param rgbA the vertex color of A
     * @param rgbB the vertex color of B
     * @param rgbC the vertex color of C
     */
    public void drawTriangle(Triangle triangle, int rgbA, int rgbB, int rgbC) {
        final int
            redA = ColorMath.red(rgbA),   redB = ColorMath.red(rgbB),   redC = ColorMath.red(rgbC),
            grnA = ColorMath.green(rgbA), grnB = ColorMath.green(rgbB), grnC = ColorMath.green(rgbC),
            bluA = ColorMath.blue(rgbA),  bluB = ColorMath.blue(rgbB),  bluC = ColorMath.blue(rgbC),
            alpA = ColorMath.alpha(rgbA), alpB = ColorMath.alpha(rgbB), alpC = ColorMath.alpha(rgbC);
    
        drawBarycentrics(triangle, (a, b, c) -> ColorMath.fromRGB(
            (int) (redA*a + redB*b + redC*c),
            (int) (grnA*a + grnB*b + grnC*c),
            (int) (bluA*a + bluB*b + bluC*c),
            (int) (alpA*a + alpB*b + alpC*c)));
        
    }
    
    /**
     * <p>
     *     Loops through all barycentric coordinates of the triangle and uses a given {@link Float3IntFunction} to draw
     *     an rgb value into the triangle.
     * </p>
     * <p>
     *     Barycentric coordinates always add up to one, thus this function may be safely used to draw vertex colors
     *     and such.
     * </p>
     * <p>
     *     Reference: <a href="https://en.wikipedia.org/wiki/Barycentric_coordinate_system">Barycentric coordinates</a>
     * </p>
     *
     * @param triangle the triangle
     * @param function the function to apply
     */
    public void drawBarycentrics(Triangle triangle, Float3IntFunction function) {
        Vector
            a = triangle.getA(),
            ab = Vector.between(a, triangle.getB()),
            ac = Vector.between(a, triangle.getC());
        
        float //subtract epsilon to account for imprecision and prevent holes
            incrB = (float) (1 / Distances.hypotCubical(ab) - Spatium.EPSILON),
            incrC = (float) (1 / Distances.hypotCubical(ac) - Spatium.EPSILON);
        
        for (float baryB = 0; baryB <= 1; baryB += incrB) {
            Vector offB = ab.clone().multiply(baryB);
            
            for (float baryC = 0; baryC <= 1; baryC += incrC) {
                float baryA = 1 - (baryB + baryC);
                if (baryA < 0) break;
                Vector offC = ac.clone().multiply(baryC);
                
                draw(
                    (int) (a.getX() + offB.getX() + offC.getX()),
                    (int) (a.getY() + offB.getY() + offC.getY()),
                    (int) (a.getZ() + offB.getZ() + offC.getZ()),
                    function.apply(baryA, baryB, baryC));
            }
        }
    }
    
    /**
     * Draws a sphere around a voxel.
     *
     * @param x0 the first voxel x
     * @param y0 the first voxel y
     * @param z0 the first voxel z
     * @param size the size of the size
     * @param rgb the line color
     */
    public void drawSphere(int x0, int y0, int z0, int size, int rgb) {
        if (size < 0) return;
        internalDrawSphere(x0, y0, z0, size, rgb);
    }

    private void internalDrawSphere(float cenX, float cenY, float cenZ, float r, int rgb) {
        final int
                minX = (int) (cenX - r), minY = (int) (cenY - r), minZ = (int) (cenZ - r),
                maxX = (int) (cenX + r), maxY = (int) (cenY + r), maxZ = (int) (cenZ + r);

        for (int x = minX; x<maxX; x++)
            for (int y = minY; y<maxY; y++)
                for (int z = minZ; z<maxZ; z++) {
                    float dist = Spatium.hypot(x-cenX, y-cenY, z-cenZ);
                    if (dist <= r)
                        draw(x, y, z, rgb);
                }
    }

    private void internalDrawEllipse(float cenX, float cenY, float cenZ, float rx, float ry, float rz, int rgb) {
        final int
                minX = (int) (cenX - rx), minY = (int) (cenY - ry), minZ = (int) (cenZ - rz),
                maxX = (int) (cenX + rx), maxY = (int) (cenY + ry), maxZ = (int) (cenZ + rz);

        for (int x = minX; x<maxX; x++)
            for (int y = minY; y<maxY; y++)
                for (int z = minZ; z<maxZ; z++) {
                    float dist = Spatium.hypot(x-cenX, y-cenY, z-cenZ);
                    if (dist <= rx && dist <= ry && dist <= rz)
                        internalDraw(x, y, z, rgb);
                }
    }

    /**
     * Draws a line from one voxel to another.
     *
     * @param x0 the first voxel x
     * @param y0 the first voxel y
     * @param z0 the first voxel z
     * @param x1 the second voxel x
     * @param y1 the second voxel y
     * @param z1 the second voxel z
     * @param rgb the line color
     */
    public void drawLine(int x0, int y0, int z0, int x1, int y1, int z1, int rgb) {
        if (x0 == x1 && y0 == y1 && z0 == z1) {
            draw(x0, y0, z0, rgb);
            return;
        }

        internalDrawLine(
                Math.max(Math.min(x0, x1), 0),
                Math.max(Math.min(y0, y1), 0),
                Math.max(Math.min(z0, z1), 0),
                Math.min(Math.max(x0, x1), sizeX-1),
                Math.min(Math.max(y0, y1), sizeY-1),
                Math.min(Math.max(z0, z1), sizeZ-1),
                rgb);
    }

    /**
     * Draws a line from one voxel to another.
     *
     * @param a the first voxel
     * @param b the first voxel
     * @param rgb the line color
     */
    public void drawLine(BlockVector a, BlockVector b, int rgb) {
        drawLine(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), rgb);
    }

    @SuppressWarnings("Duplicates")
    private void internalDrawLine(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int rgb) {
        final int dx = maxX-minX, dy = maxY-minY, dz = maxZ-minZ, dmax = PrimMath.max(dx, dy, dz);
        int err0 = dmax / 2, err1 = err0;

        if (dmax == dx) {
            for (int x=minX, y=minY, z=minZ; x<=maxX; x++) {
                internalDraw(x, y, z, rgb);

                err0 -= dy;
                if (err0 < 0) {
                    err0 += dx;
                    y++;
                }
                err1 -= dz;
                if (err1 < 0) {
                    err1 += dx;
                    z++;
                }
            }
        }
        else if (dmax == dy) {
            for (int x=minX, y=minY, z=minZ; x<=maxX; y++) {
                internalDraw(x, y, z, rgb);

                err0 -= dx;
                if (err0 < 0) {
                    err0 += dy;
                    x++;
                }
                err1 -= dz;
                if (err1 < 0) {
                    err1 += dy;
                    z++;
                }
            }
        }
        else if (dmax == dz) {
            for (int x=minX, y=minY, z=minZ; x<=maxX; z++) {
                internalDraw(x, y, z, rgb);

                err0 -= dx;
                if (err0 < 0) {
                    err0 += dz;
                    x++;
                }
                err1 -= dy;
                if (err1 < 0) {
                    err1 += dz;
                    y++;
                }
            }
        }
    }

    // SELECT

    public void setSelected(int x, int y, int z, boolean selected) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.set(x, y, z, selected);
    }

    public void select(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.enable(x, y, z);
    }

    public void unselect(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.disable(x, y, z);
    }

    public void selectContent(boolean append) {
        selectRaw(content::contains, append);
    }

    public void selectRaw(Int3Predicate predicate, boolean append) {
        forEachPosition(((x, y, z) -> {
            if (predicate.test(x, y, z))
                select(x, y, z);
            else if (!append)
                unselect(x, y, z);
        }));
    }

    // IS SELECTED

    public boolean hasContent(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ && content.contains(x, y, z);
    }

    public boolean isSelected(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ && selection.contains(x, y, z);
    }

    // ITERATION

    public void forEachVoxel(Consumer<? super VoxelArray.Voxel> action) {
        forEachPosition((x, y, z) -> {
            if (content.contains(x, y, z) && selection.contains(x, y, z))
                action.accept(content.getVoxel(x, y, z));
        });
    }

    public void forEachPosition(Int3Consumer action) {
        for (int x = 0; x<sizeX; x++)
            for (int y = 0; y<sizeY; y++)
                for (int z = 0; z<sizeZ; z++)
                    action.accept(x, y, z);
    }

    // MISC


    @Override
    public String toString() {
        return VoxelCanvas.class.getSimpleName()+
                "{content="+content+
                ",selected="+getSelection().size()+"}";
    }

}
