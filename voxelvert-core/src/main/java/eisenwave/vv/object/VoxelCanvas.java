package eisenwave.vv.object;

import eisenwave.spatium.function.*;
import eisenwave.spatium.util.PrimMath;
import eisenwave.spatium.util.Spatium;
import eisenwave.spatium.array.BooleanArray3;
import eisenwave.torrens.object.Vertex3i;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.VoxelArray;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

@Deprecated
public class VoxelCanvas {
    
    private final VoxelArray content;
    private final BooleanArray3 selection;
    
    private final int sizeX, sizeY, sizeZ;
    
    //SETTINGS
    
    private boolean unselectDrawn = false;
    private boolean transparentDraw = true;
    
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
    
    /**
     * Returns the size of this canvas on the x-axis.
     *
     * @return the x-size
     */
    public int getSizeX() {
        return sizeX;
    }
    
    /**
     * Returns the size of this canvas on the y-axis.
     *
     * @return the y-size
     */
    public int getSizeY() {
        return sizeY;
    }
    
    /**
     * Returns the size of this canvas on the z-axis.
     *
     * @return the z-size
     */
    public int getSizeZ() {
        return sizeZ;
    }
    
    /**
     * Returns the volume in voxels of this canvas.
     *
     * @return the volume in voxels
     */
    public int getVolume() {
        return sizeX * sizeY * sizeZ;
    }
    
    /**
     * Returns the amount of drawn voxels in this canvas.
     *
     * @return the amount of drawn voxels
     */
    public int contentSize() {
        return content.size();
    }
    
    /**
     * Returns the amount of selected voxels in this canvas.
     *
     * @return the amount of selected voxels
     */
    public int selectionSize() {
        return sizeOf(selection);
    }
    
    // CONFIGURATION
    
    public void setUnselectDrawn(boolean flag) {
        this.unselectDrawn = flag;
    }
    
    public void setTransparentDraw(boolean flag) {
        this.transparentDraw = flag;
    }
    
    // DRAW PIPELINE
    
    /**
     * Safely sets a voxel at a given position to a given rgb value.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param rgb the rgb value
     */
    public void draw(int x, int y, int z, int rgb) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            noclipDraw(x, y, z, rgb);
    }
    
    /**
     * Safely sets a voxel at a given position to a given rgb value.
     *
     * @param pos the voxel position
     * @param rgb the rgb value to draw
     */
    public void draw(Vertex3i pos, int rgb) {
        draw(pos.getX(), pos.getY(), pos.getZ(), rgb);
    }
    
    /**
     * Draws without checking for clipping.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param rgb the rgb value to draw
     */
    private void noclipDraw(int x, int y, int z, int rgb) {
        if (selection.get(x, y, z)) {
            if (!transparentDraw) {
                content.setRGB(x, y, z, rgb);
            }
            else {
                int old = content.getRGB(x, y, z);
                content.setRGB(x, y, z, ColorMath.stack(old, rgb));
            }
            if (unselectDrawn) selection.disable(x, y, z);
        }
    }
    
    // OBJECT DRAWING
    
    public void drawVoxel(VoxelArray.Voxel voxel) {
        draw(voxel.getX(), voxel.getY(), voxel.getZ(), voxel.getRGB());
    }
    
    public void drawVoxels(VoxelArray array, int x, int y, int z) {
        array.forEach(voxel -> draw(voxel.getX() + x, voxel.getY() + y, voxel.getZ() + z, voxel.getRGB()));
    }
    
    public void drawVoxels(VoxelArray array) {
        array.forEach(this::drawVoxel);
    }
    
    public void drawRaw(Int3IntFunction function) {
        forEachPosition((x, y, z) -> noclipDraw(x, y, z, function.apply(x, y, z)));
    }
    
    public void drawIf(Int3Predicate predicate, int rgb) {
        forEachPosition((x, y, z) -> {
            if (predicate.test(x, y, z)) noclipDraw(x, y, z, rgb);
        });
    }
    
    public void drawIfElse(Int3Predicate predicate, int rgbTrue, int rgbFalse) {
        drawRaw((x, y, z) -> predicate.test(x, y, z)? rgbTrue : rgbFalse);
    }

    /*
    public void drawPath(Path3 path, float interval, int rgb) {
        PathIterator iterator = new PathIterator(path, interval);
        while (iterator.hasNext())
            draw(iterator.next().toBlockVector(), rgb);
    }

    public void drawPath(Path3 path, int steps, int rgb) {
        PathIterator iterator = new PathIterator(path, steps);
        while (iterator.hasNext())
            draw(iterator.next().toBlockVector(), rgb);
    }

    public void drawSpace(Space space, int rgb) {
        drawIf(space::contains, rgb);
    }
    */
    
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
            Math.min(Math.max(x0, x1), sizeX - 1),
            Math.min(Math.max(y0, y1), sizeY - 1),
            Math.min(Math.max(z0, z1), sizeZ - 1),
            rgb);
    }
    
    private void internalDrawBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int rgb) {
        for (int x = minX; x < maxX; x++)
            for (int y = minY; y < maxY; y++)
                for (int z = minZ; z < maxZ; z++)
                    noclipDraw(x, y, z, rgb);
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
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public void drawEllipse(int x0, int y0, int z0, int x1, int y1, int z1, int rgb) {
        naiveDrawEllipse(
            (x0 + x1) / 2,
            (y0 + y1) / 2,
            (z0 + z1) / 2,
            Math.abs(x1 - x0),
            Math.abs(y1 - y0),
            Math.abs(z1 - z0),
            rgb);
    }
    
    /*
     * Draws a triangle into the canvas.
     *
     * @param triangle the triangle
     * @param rgb the color
     */
    /*
    public void drawTriangle(Triangle3 triangle, int rgb) {
        drawBarycentrics(triangle, (x, y, z) -> rgb);
    }
    */
    
    /*
     * <p>
     *     Draws a triangle with vertex colors into the canvas.
     * </p>
     * <p>
     *     The three vertex colors will be scaled by the baryocentric coordinates <b>a, b, c</b> in the manner:
     *     <blockquote>
     *         <code>rgb<sub>abc</sub> = a * rgb<sub>a</sub> + b * rgb<sub>b</sub> + c * rgb<sub>c</sub></code>
     *     </blockquote>
     *     With <code>n * rgb</code> defined as multiplying each color channel (including alpha) with <b>n</b>.
     * </p>
     *
     *
     * @param triangle the triangle
     * @param rgbA the vertex color of A
     * @param rgbB the vertex color of B
     * @param rgbC the vertex color of C
     */
    /*
    public void drawTriangle(Triangle3 triangle, int rgbA, int rgbB, int rgbC) {
        final int
            redA = red(rgbA),   redB = red(rgbB),   redC = red(rgbC),
            grnA = green(rgbA), grnB = green(rgbB), grnC = green(rgbC),
            bluA = blue(rgbA),  bluB = blue(rgbB),  bluC = blue(rgbC),
            alpA = alpha(rgbA), alpB = alpha(rgbB), alpC = alpha(rgbC);
    
        drawBarycentrics(triangle, (a, b, c) -> fromRGB(
            (int) (redA*a + redB*b + redC*c),
            (int) (grnA*a + grnB*b + grnC*c),
            (int) (bluA*a + bluB*b + bluC*c),
            (int) (alpA*a + alpB*b + alpC*c)));
        
    }
    */
    
    /*
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
    /*
    public void drawBarycentrics(Triangle3 triangle, Float3IntFunction function) {
        final Vector3
            a = triangle.getA(),
            b = triangle.getB(),
            c = triangle.getC(),
            ab = Vector3.between(a, b),
            ac = Vector3.between(a, c),
            bc = Vector3.between(b, c);
            
        final float //subtract epsilon to account for imprecision and prevent holes
            incrAB = (float) (1 / Distances.maxAbs(ab) - Spatium.EPSILON),
            incrAC = (float) (1 / Distances.maxAbs(ac) - Spatium.EPSILON),
            incrBC = (float) (1 / Distances.maxAbs(bc) - Spatium.EPSILON);
        
        //iterate over AB & AC
        for (float baryB = 0; baryB <= 1; baryB += incrAB) {
            Vector3 offB = ab.clone().multiply(baryB);
            
            for (float baryC = 0; baryC <= 1; baryC += incrAC) {
                float baryA = 1 - (baryB + baryC);
                if (baryA < 0) break;
                Vector3 offC = ac.clone().multiply(baryC);
                final int
                    x = (int) (a.getX() + offB.getX() + offC.getX()),
                    y = (int) (a.getY() + offB.getY() + offC.getY()),
                    z = (int) (a.getZ() + offB.getZ() + offC.getZ());
                
                draw(x, y, z, function.apply(baryA, baryB, baryC));
            }
        }
        
        //iterate over BC to smooth edge
        for (float baryC = 0; baryC <= 1; baryC +=incrBC) {
            draw(
                (int) (b.getX() + bc.getX() * baryC),
                (int) (b.getY() + bc.getY() * baryC),
                (int) (b.getZ() + bc.getZ() * baryC),
                function.apply(0, 1-baryC, baryC));
        }
    }
    */
    
    /*
    private static BoundingBox6i getBoundsArray(Vertex3i a, Vertex3i b, Vertex3i c) {
        BoundingBox6i bounds = triangle.getBoundaries();
        return new BooleanArray3(
            (int) bounds.getSizeX(),
            (int) bounds.getSizeY(),
            (int) bounds.getSizeZ());
    }
    */
    
    /**
     * Draws a sphere around a voxel.
     *
     * @param x0 the first voxel x
     * @param y0 the first voxel y
     * @param z0 the first voxel z
     * @param radius the size of the sphere
     * @param rgb the line color
     */
    public void drawSphere(int x0, int y0, int z0, int radius, int rgb) {
        if (radius < 0) return;
        fastDrawSphere(x0, y0, z0, radius, rgb);
    }
    
    @SuppressWarnings("Duplicates")
    private void bresenhamDrawSphere(int cenX, int cenY, int cenZ, int r, int rgb) {
        int x = 0, z = r;
        //int decision = 3 - 2 * r;
        int decision = 1 - r;
        while (z >= x) {
            //rgb = ThreadLocalRandom.current().nextInt() | 0xFF000000;
            bresenhamDrawCircleXY(cenX, cenY, cenZ + z, x, rgb);
            bresenhamDrawCircleXY(cenX, cenY, cenZ + x, z, rgb);
            bresenhamDrawCircleXY(cenX, cenY, cenZ - x, z, rgb);
            bresenhamDrawCircleXY(cenX, cenY, cenZ - z, x, rgb);
            /* draw(cenX + x, cenY, cenZ + z, rgb);
            draw(cenX + z, cenY, cenZ + x, rgb);
            draw(cenX + z, cenY, cenZ - x, rgb);
            draw(cenX + x, cenY, cenZ - z, rgb); */
            
            x++;
            
            //if (decision > 0) {
            if (decision >= 0) {
                decision += 2 * (x - z) + 5;
                //decision += 2 * z + 3;
                z--;
            }
            else
                //decision += 4 * x + 6;
                decision += 2 * x + 3;
        }
    }
    
    @SuppressWarnings("Duplicates")
    private void bresenhamDrawCircleXY(int cenX, int cenY, int cenZ, int r, int rgb) {
        int x = 0, y = r;
        int decision = 1 - r;
        while (y >= x) {
            draw(cenX + x, cenY + y, cenZ, rgb);
            draw(cenX + y, cenY + x, cenZ, rgb);
            draw(cenX - y, cenY + x, cenZ, rgb);
            draw(cenX - x, cenY + y, cenZ, rgb);
            draw(cenX - x, cenY - y, cenZ, rgb);
            draw(cenX - y, cenY - x, cenZ, rgb);
            draw(cenX + y, cenY - x, cenZ, rgb);
            draw(cenX + x, cenY - y, cenZ, rgb);
            x++;
            
            if (decision >= 0) {
                y--;
                decision += 2 * (x - y) + 5;
            }
            else
                decision += 2 * x + 3;
        }
    }
    
    @SuppressWarnings("Duplicates")
    private void fastDrawSphere(int cenX, int cenY, int cenZ, int r, int rgb) {
        final int lim = r * r;
        
        for (int x = 0, x0 = cenX, x1 = cenX, xSqr = 0; x < r; xSqr += x + x + 1, x++, x0--, x1++)
            for (int y = 0, y0 = cenY, y1 = cenY, ySqr = 0; y < r; ySqr += y + y + 1, y++, y0--, y1++)
                for (int z = 0, z0 = cenZ, z1 = cenZ, zSqr = 0; xSqr + ySqr + zSqr < lim; zSqr += z + z + 1, z++, z0--, z1++) {
                    draw(x0, y0, z0, rgb);
                    draw(x0, y0, z1, rgb);
                    draw(x0, y1, z0, rgb);
                    draw(x0, y1, z1, rgb);
                    draw(x1, y0, z0, rgb);
                    draw(x1, y0, z1, rgb);
                    draw(x1, y1, z0, rgb);
                    draw(x1, y1, z1, rgb);
                }
    }
    
    private void naiveDrawSphere(float cenX, float cenY, float cenZ, float r, int rgb) {
        final int
            minX = (int) (cenX - r), minY = (int) (cenY - r), minZ = (int) (cenZ - r),
            maxX = (int) (cenX + r), maxY = (int) (cenY + r), maxZ = (int) (cenZ + r);
        
        for (int x = minX; x < maxX; x++)
            for (int y = minY; y < maxY; y++)
                for (int z = minZ; z < maxZ; z++) {
                    float dist = Spatium.hypot(x - cenX, y - cenY, z - cenZ);
                    if (dist <= r)
                        draw(x, y, z, rgb);
                }
    }
    
    private void naiveDrawEllipse(float cenX, float cenY, float cenZ, float rx, float ry, float rz, int rgb) {
        final int
            minX = (int) (cenX - rx), minY = (int) (cenY - ry), minZ = (int) (cenZ - rz),
            maxX = (int) (cenX + rx), maxY = (int) (cenY + ry), maxZ = (int) (cenZ + rz);
        
        for (int x = minX; x < maxX; x++)
            for (int y = minY; y < maxY; y++)
                for (int z = minZ; z < maxZ; z++) {
                    float dist = Spatium.hypot(x - cenX, y - cenY, z - cenZ);
                    if (dist <= rx && dist <= ry && dist <= rz)
                        noclipDraw(x, y, z, rgb);
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
            Math.min(Math.max(x0, x1), sizeX - 1),
            Math.min(Math.max(y0, y1), sizeY - 1),
            Math.min(Math.max(z0, z1), sizeZ - 1),
            rgb);
    }
    
    /**
     * Draws a line from one voxel to another.
     *
     * @param a the first voxel
     * @param b the first voxel
     * @param rgb the line color
     */
    public void drawLine(Vertex3i a, Vertex3i b, int rgb) {
        drawLine(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), rgb);
    }
    
    @SuppressWarnings("Duplicates")
    private void internalDrawLine(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int rgb) {
        final int dx = maxX - minX, dy = maxY - minY, dz = maxZ - minZ, dmax = PrimMath.max(dx, dy, dz);
        int err0 = dmax / 2, err1 = err0;
        
        if (dmax == dx) {
            for (int x = minX, y = minY, z = minZ; x <= maxX; x++) {
                noclipDraw(x, y, z, rgb);
                
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
            for (int x = minX, y = minY, z = minZ; y <= maxY; y++) {
                noclipDraw(x, y, z, rgb);
                
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
            for (int x = minX, y = minY, z = minZ; z <= maxZ; z++) {
                noclipDraw(x, y, z, rgb);
                
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
    
    // SELECTION PIPELINE
    
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
    
    // SELECT OBJECTS
    
    public void selectAll() {
        forEachPosition(this::select);
    }
    
    public void selectContent(boolean append) {
        selectRaw(content::contains, append);
    }
    
    public void selectRaw(Int3Predicate predicate, boolean append) {
        if (append) {
            forEachPosition((x, y, z) -> {
                if (predicate.test(x, y, z)) select(x, y, z);
            });
        }
        else {
            forEachPosition((x, y, z) -> setSelected(x, y, z, predicate.test(x, y, z)));
        }
    }
    
    // CHECKERS
    
    public boolean hasContent(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ && content.contains(x, y, z);
    }
    
    public boolean isSelected(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ && selection.get(x, y, z);
    }
    
    // ITERATION
    
    public void forEachVoxel(Consumer<? super VoxelArray.Voxel> action) {
        forEachPosition((x, y, z) -> {
            if (content.contains(x, y, z) && selection.get(x, y, z))
                action.accept(content.getVoxel(x, y, z));
        });
    }
    
    @SuppressWarnings("Duplicates")
    public void forEachPosition(Int3Consumer action) {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                for (int z = 0; z < sizeZ; z++)
                    action.accept(x, y, z);
    }
    
    // MISC
    
    @Override
    public String toString() {
        return VoxelCanvas.class.getSimpleName() +
            "{content=" + content +
            ",selected=" + sizeOf(selection) + "}";
    }
    
    private static int sizeOf(BooleanArray3 array) {
        int size = 0;
        
        BooleanArray3.BooleanArrayIterator3 iter = array.iterator();
        while (iter.hasNext())
            if (iter.nextBoolean()) size++;
        
        return size;
    }
    
}
