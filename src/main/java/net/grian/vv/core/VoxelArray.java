package net.grian.vv.core;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.vv.util.ColorMath;
import net.grian.vv.util.RGBValue;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class VoxelArray implements Bitmap3D, Cloneable, Serializable, Iterable<VoxelArray.Voxel> {

    private final int[][][] voxels;

    private final int sizeX, sizeY, sizeZ;

    public VoxelArray(int x, int y, int z) {
        if (x == 0 || y == 0 || z == 0) throw new IllegalArgumentException("size 0 voxel array");
        this.voxels = new int[x][y][z];
        this.sizeX = x;
        this.sizeY = y;
        this.sizeZ = z;
    }

    /**
     * Fills this voxel array with another array at a given offset.
     *
     * @param array the array to fill this one with
     */
    public void fill(VoxelArray array, int x, int y, int z) {
        for (Voxel v : array)
            this.setRGB(v.getX()+x, v.getY()+y, v.getZ()+z, v.getRGB());
    }

    /**
     * Fills the entire voxel array with a single rgb value.
     *
     * @param rgb the rgb value
     */
    public void fill(int rgb) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    voxels[x][y][z] = rgb;
    }

    /**
     * <p>
     *     Fills the entire voxel array with {@link ColorMath#INVISIBLE_WHITE}, effectively deleting all voxels.
     * </p>
     * <p>
     *     The boundaries of the array are not affected by this operation.
     * </p>
     */
    public void clear() {
        fill(ColorMath.INVISIBLE_WHITE);
    }

    /**
     * Returns a copy of a part of this array.
     *
     * @param xmin the min x
     * @param ymin the min y
     * @param zmin the min z
     * @param xmax the max x
     * @param ymax the max y
     * @param zmax the max z
     * @return a new sub array, copied out of this array
     */
    public VoxelArray copy(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
        if (xmin < 0 || ymin < 0 || zmin < 0)
            throw new IllegalArgumentException("min ("+xmin+","+ymin+","+zmin+") out of boundaries");
        if (xmax >= getSizeX() || ymax >= getSizeY() || zmax >= getSizeZ())
            throw new IllegalArgumentException("max ("+xmax+","+ymax+","+zmax+") out of boundaries");

        VoxelArray result = new VoxelArray(xmax-xmin+1, ymax-ymin+1, zmax-zmin+1);
        for (int x = xmin; x<=xmax; x++)
            for (int y = ymin; y<=ymax; y++)
                for (int z = zmin; z<=zmax; z++)
                    result.setRGB(x-xmin, y-ymin, z-zmin, getRGB(x, y, z));

        return result;
    }

    /**
     * Returns the size of the array on the x-axis.
     *
     * @return the size on the x-axis
     */
    @Override
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Returns the size of the array on the y-axis.
     *
     * @return the size on the y-axis
     */
    @Override
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Returns the size of the array on the z-axis.
     *
     * @return the size on the z-axis
     */
    @Override
    public int getSizeZ() {
        return sizeZ;
    }

    /**
     * Returns the boundaries of this voxel array.
     *
     * @return the array boundaries
     */
    public BlockSelection getBoundaries() {
        return BlockSelection.fromPoints(0, 0, 0, getSizeX(), getSizeY(), getSizeZ());
    }

    /**
     * Returns the voxel at the specified coordinates or null if there is no voxel at the coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the voxel at the specified coordinates
     */
    @Nullable
    public Voxel getVoxel(int x, int y, int z) {
        return ColorMath.isVisible(getRGB(x, y, z))? new Voxel(x, y, z) : null;
    }

    /**
     * Returns the voxel at the specified coordinates or null if there is no voxel at the coordinates.
     *
     * @param v the position
     * @return the voxel at the specified position
     */
    public Voxel getVoxel(BlockVector v) {
        return getVoxel(v.getX(), v.getY(), v.getZ());
    }

    /**
     * Returns the RGB value of the voxel at the specified coordinates. If the alpha ({@code rgb >> 24}) is 0, there is
     * no voxel at the position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the color of the voxel at the position
     */
    public int getRGB(int x, int y, int z) {
        return voxels[x][y][z];
    }

    /**
     * Returns the RGB value of the voxel at the specified position. If the alpha ({@code rgb >> 24}) is 0, there is
     * no voxel at the position.
     *
     * @param v the voxel position
     * @return the color of the voxel at the position
     */
    public int getRGB(BlockVector v) {
        return getRGB(v.getX(), v.getY(), v.getZ());
    }

    /**
     * Returns the total amount of visible voxels in this array.
     *
     * @return the amount of voxels
     */
    public int size() {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();
        int count = 0;

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    if (ColorMath.isVisible(getRGB(x, y, z))) count++;

        return count;
    }

    //CHECKERS

    /**
     * <p>
     *     Returns whether the array contains a voxel at the given position.
     * </p>
     * <p>
     *     This is the case unless the voxel array contains a completely transparent voxel <code>(alpha = 0) </code>
     *     at the coordinates.
     * </p>
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return whether the array contains a voxel
     */
    @Override
    public boolean contains(int x, int y, int z) {
        return ColorMath.isVisible(getRGB(x, y, z));
    }

    /**
     * <p>
     *     Returns whether the array contains a voxel at the given position.
     * </p>
     * <p>
     *     This is the case unless the voxel array contains a completely transparent voxel <code>(alpha = 0)</code>
     *     at the position.
     * </p>
     *
     * @param pos the position
     * @return whether the array contains a voxel
     */
    @Override
    public boolean contains(BlockVector pos) {
        return contains(pos.getX(), pos.getY(), pos.getZ());
    }

    //SETTERS

    /**
     * Sets the voxel color at a given position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @param rgb the voxel color
     */
    public void setRGB(int x, int y, int z, int rgb) {
        voxels[x][y][z] = rgb;
    }

    /**
     * Sets the voxel color at a given position.
     *
     * @param pos the position
     * @param rgb the voxel color
     */
    public void setRGB(BlockVector pos, int rgb) {
        setRGB(pos.getX(), pos.getY(), pos.getZ(), rgb);
    }

    public void remove(int x, int y, int z) {
        setRGB(x, y, z, ColorMath.INVISIBLE_WHITE);
    }

    //MISC

    @Override
    public String toString() {
        return VoxelArray.class.getSimpleName()+
                "{dims="+getSizeX()+"x"+getSizeY()+"x"+getSizeZ()+
                ", volume="+getVolume()+
                ", size="+size()+"}";
    }

    @Override
    public VoxelArray clone() {
        return copy(0, 0, 0, getSizeX()-1, getSizeY()-1, getSizeZ()-1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoxelArray && equals((VoxelArray) obj);
    }

    /**
     * Returns whether this array is equal to another array. This condition is met of the arrays are equal in size and
     * equal in content.
     *
     * @param array the array
     * @return whether the arrays are equal
     */
    public boolean equals(VoxelArray array) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();
        if (limX != array.getSizeX() || limY != array.getSizeY() || limZ != array.getSizeZ())
            return false;

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    if (getRGB(x, y, z) != array.getRGB(x, y, z)) return false;

        return true;
    }

    @Override
    public void forEach(Consumer<? super Voxel> action) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    if (ColorMath.isVisible(getRGB(x, y, z)))
                        action.accept(new Voxel(x, y, z));
    }

    public void forEachPosition(Consumer<? super BlockVector> action) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    action.accept(BlockVector.fromXYZ(x, y, z));
    }

    /**
     * Equivalent to {@link #validatingIterator()}.
     *
     * @return a new iterator
     */
    @Override
    public Iterator<Voxel> iterator() {
        return new ValidatingVoxelIterator();
    }

    /**
     * Returns an iterator that skips invisible (invalid) voxels.
     *
     * @return a new validating voxel iterator
     */
    public ValidatingVoxelIterator validatingIterator() {
        return new ValidatingVoxelIterator();
    }

    /**
     * Returns an iterator that does not skip any voxels, whether they are visible or not.
     *
     * @return a new voxel iterator
     */
    public VoxelIterator voxelIterator() {
        return new VoxelIterator();
    }

    public class VoxelIterator implements Iterator<Voxel> {

        private final int
                lim = getVolume(),
                divX = getSizeX(),
                divY = getSizeY(),
                divZ = divX * divY;
        protected int index = 0;

        @Override
        public boolean hasNext() {
            return index < lim;
        }

        @Override
        public Voxel next() {
            final int x = index%divX, y = index/divX%divY, z = index/divZ;
            if (++index > lim) throw new NoSuchElementException();
            return new Voxel(x, y, z);
        }

        @Override
        public void remove() {
            VoxelArray.this.remove(index%divX, index/divX%divY, index/divZ);
        }

    }

    public class ValidatingVoxelIterator implements Iterator<Voxel> {

        private final int
                max = getVolume()-1,
                divX = getSizeX(),
                divY = getSizeY(),
                divZ = divX * divY;
        private int index = -1;

        private ValidatingVoxelIterator() {
            skipToValid();
        }

        @Override
        public Voxel next() {
            Voxel result = peek();
            skip();
            skipToValid();
            return result;
        }

        private void skipToValid() {
            while (hasNext()) {
                if (validate(peek())) break;
                else skip();
            }
        }

        @Override
        public boolean hasNext() {
            return index < max;
        }

        private void skip() throws NoSuchElementException {
            if (++index > max) throw new NoSuchElementException();
        }

        private Voxel peek() throws NoSuchElementException {
            int
            next = index + 1,
            x = next%divX, y = next/divX%divY, z = next/divZ;

            return new Voxel(x, y, z);
        }

        private boolean validate(Voxel voxel) {
            return voxel.isVisible();
        }

    }

    /**
     * <p>
     *     A temporary class representing one position inside a {@link VoxelArray}.
     * </p>
     * <p>
     *     Permanently storing a {@link Voxel} should be strictly avoided as this will result in a reference to its
     *     underlying array, keeping it from being garbage collected and introducing potential memory leaks.
     * </p>
     * <p>
     *     Ideally, this object should be disposed of at the end of iteration over the underlying array.
     * </p>
     *
     */
    public class Voxel implements RGBValue, Serializable, Cloneable {

        private final int x, y, z;

        private Voxel(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Voxel(Voxel copyOf) {
            this(copyOf.x, copyOf.y, copyOf.z);
        }

        private Voxel(BlockVector pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        /**
         * Returns the position of the voxel in its voxel array as a block vector.
         *
         * @return the position of the voxel
         */
        public BlockVector getPosition() {
            return BlockVector.fromXYZ(x, y, z);
        }

        @Override
        public int getRGB() {
            return VoxelArray.this.getRGB(x, y, z);
        }

        /**
         * <p>
         *     Changes the RGB value of the voxel. This change is also applied to the underlying array in which the
         *     voxel is placed.
         * </p>
         * <p>
         *     Assigning an RGB value with an alpha value of 0 is equivalent to removing the voxel from its array, as
         *     voxels can not be invisible.
         * </p>
         *
         * @param rgb the rgb value to be assigned to the voxel
         * @see Voxel#remove(int, int, int)
         */
        public void setRGB(int rgb) {
            VoxelArray.this.setRGB(x, y, z, rgb);
        }

        /**
         * <p>
         *     Sets the RGB value of the voxel to {@link ColorMath#INVISIBLE_WHITE}. Although this color is technically
         *     white, its alpha channel is exactly 0. Using the white color it is possible to distinguish between
         *     unassigned voxels ({@link ColorMath#INVISIBLE_BLACK}) and deleted voxels.
         * </p>
         * <p>
         *     Assigning an RGB value with an alpha value of 0 is equivalent to removing the voxel from its array, as
         *     voxels can not be invisible.
         * </p>
         */
        public void remove() {
            setRGB(ColorMath.INVISIBLE_WHITE);
        }

        /**
         * <p>
         *     Returns whether a face of the voxel is visible.
         * </p>
         * <p>
         *     A face is always visible if there is no voxel covering that face. This also applies if the voxel face can
         *     not be covered by another voxel due to the array containing it ending at the face.
         * </p>
         *
         * @param side the side
         * @return whether the face of the voxel is visible
         */
        public boolean isVisible(Direction side) {
            switch (side) {
                case NEGATIVE_X: return x==0 || !VoxelArray.this.contains(x-1, y, z);
                case NEGATIVE_Y: return y==0 || !VoxelArray.this.contains(x, y-1, z);
                case NEGATIVE_Z: return z==0 || !VoxelArray.this.contains(x, y, z-1);
                case POSITIVE_X: return x==sizeX-1 || !VoxelArray.this.contains(x+1, y, z);
                case POSITIVE_Y: return y==sizeY-1 || !VoxelArray.this.contains(x, y+1, z);
                case POSITIVE_Z: return z==sizeZ-1 || !VoxelArray.this.contains(x, y, z+1);
                default: throw new IllegalArgumentException("unknown direction: "+side);
            }
        }

        /**
         * <p>
         *    Returns a map representing the visibility of each side of the voxel.
         * </p>
         * <p>
         *     A face is always visible if there is no voxel covering that face. This also applies if the voxel face can
         *     not be covered by another voxel due to the array containing it ending at the face.
         * </p>
         * <p>
         *     The ordinal of the {@link Direction} represents the index of the bit which can 0 (covered) or 1 (visible).
         *     Checking which side is visible can be done with the formulas:
         *     <ul>
         *         <li><code>value >> ordinal & 1 == 1</code></li>
         *         <li><code>value & 1 << ordinal != 0</code></li>
         *     </ul>
         * </p>
         * <p>
         *     If the returned byte is exactly 0, the voxel is covered from every side, if it is {@code 0b00111111}
         *     the voxel is visible from every side.
         * </p>
         *
         * @return a bitmap representing which faces are visible
         */
        public byte getFaceVisibility() {
            byte result = 0;

            if (x==0 || !VoxelArray.this.contains(x-1, y, z)) result |= Direction.NEGATIVE_X.ordinal();
            if (y==0 || !VoxelArray.this.contains(x, y-1, z)) result |= Direction.NEGATIVE_Y.ordinal();
            if (z==0 || !VoxelArray.this.contains(x, y, z-1)) result |= Direction.NEGATIVE_Z.ordinal();

            if (x==sizeX-1 || !VoxelArray.this.contains(x+1, y, z)) result |= Direction.POSITIVE_X.ordinal();
            if (y==sizeY-1 || !VoxelArray.this.contains(x, y+1, z)) result |= Direction.POSITIVE_Y.ordinal();
            if (z==sizeZ-1 || !VoxelArray.this.contains(x, y, z+1)) result |= Direction.POSITIVE_Z.ordinal();

            return result;
        }

        @Override
        public Voxel clone() {
            return new Voxel(this);
        }

        @Override
        public String toString() {
            return Voxel.class.getSimpleName()+
                    "{x=" + x +
                    ",y=" + y +
                    ",z=" + z +
                    ",rgb=" + Integer.toHexString(getRGB()).toUpperCase() + "}";
        }

    }

}
