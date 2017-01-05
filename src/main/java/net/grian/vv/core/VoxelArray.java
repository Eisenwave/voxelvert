package net.grian.vv.core;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.vv.util.Colors;
import net.grian.vv.util.RGBValue;

import java.awt.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class VoxelArray implements Cloneable, Serializable, Iterable<VoxelArray.Voxel> {

    private final int[][][] voxels;

    public VoxelArray(int x, int y, int z) {
        if (x == 0 || y == 0 || z == 0) throw new IllegalArgumentException("size 0 voxel array");
        this.voxels = new int[x][y][z];
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

    public void fill(int rgb) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    voxels[x][y][z] = rgb;
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
    public int getSizeX() {
        return voxels.length;
    }

    /**
     * Returns the size of the array on the y-axis.
     *
     * @return the size on the y-axis
     */
    public int getSizeY() {
        return voxels[0].length;
    }

    /**
     * Returns the size of the array on the z-axis.
     *
     * @return the size on the z-axis
     */
    public int getSizeZ() {
        return voxels[0][0].length;
    }

    /**
     * Returns the volume of the voxel array.
     *
     * @return the volume
     */
    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public BlockVector getDimensions() {
        return BlockVector.fromXYZ(getSizeX(), getSizeY(), getSizeZ());
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
     * Returns the voxel at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the voxel at the specified coordinates
     */
    public Voxel getVoxel(int x, int y, int z) {
        int rgb = getRGB(x, y, z);
        if (rgb >> 24 == 0) return null;
        return new Voxel(x, y, z);
    }

    /**
     * Returns the voxel at the specified position.
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
                    if (Colors.isVisible(getRGB(x, y, z))) count++;

        return count;
    }

    //CHECKERS

    /**
     * Returns whether the array contains a voxel at the given position. This is the case unless the voxel array
     * contains a completely transparent voxel <code>(alpha = 0) </code> at the coordinates.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return whether the array contains a voxel
     */
    public boolean contains(int x, int y, int z) {
        return Colors.isVisible(getRGB(x, y, z));
    }

    /**
     * Returns whether the array contains a voxel at the given position.
     * @param v the voxel position
     * @return whether the array contains a voxel
     */
    public boolean contains(BlockVector v) {
        return contains(v.getX(), v.getY(), v.getZ());
    }

    //SETTERS

    public void setRGB(int x, int y, int z, int rgb) {
        voxels[x][y][z] = rgb;
    }

    public void setRGB(BlockVector v, int rgb) {
        setRGB(v.getX(), v.getY(), v.getZ(), rgb);
    }

    public void remove(int x, int y, int z) {
        setRGB(x, y, z, Colors.INVISIBLE_WHITE);
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
                    if (Colors.isVisible(getRGB(x, y, z)))
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

        private Voxel(BlockVector pos, Color color) {
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

        public BlockVector getPosition() {
            return BlockVector.fromXYZ(x, y, z);
        }

        @Override
        public int getRGB() {
            return VoxelArray.this.getRGB(x, y, z);
        }

        public void setRGB(int rgb) {
            VoxelArray.this.setRGB(x, y, z, rgb);
        }

        public void remove() {
            setRGB(Colors.INVISIBLE_WHITE);
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
