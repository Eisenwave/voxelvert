package net.grian.vv.core;

import net.grian.spatium.geo.BlockVector;

public interface Bitmap3D {

    /**
     * Returns the dimension on the x-axis.
     *
     * @return the dimension on the x-axis
     */
    int getSizeX();

    /**
     * Returns the dimension on the y-axis.
     *
     * @return the dimension on the y-axis
     */
    int getSizeY();

    /**
     * Returns the dimension on the z-axis.
     *
     * @return the dimension on the z-axis
     */
    int getSizeZ();

    /**
     * Returns the volume. This is equivalent to the product of all dimensions.
     *
     * @return the volume
     */
    default int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    /**
     * Returns the dimensions on x, y and z axis.
     *
     * @return the dimensions
     */
    default BlockVector getDimensions() {
        return BlockVector.fromXYZ(getSizeX(), getSizeY(), getSizeZ());
    }

    /**
     * Checks whether the bitmap contains an element at the given position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return whether there is an element
     */
    boolean contains(int x, int y, int z);

    /**
     * Checks whether the bitmap contains an element at the given position.
     *
     * @param pos the position
     * @return whether there is an element
     */
    default boolean contains(BlockVector pos) {
        return contains(pos.getX(), pos.getY(), pos.getZ());
    }

}
