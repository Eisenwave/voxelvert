package net.grian.vv.core;

/**
 * A rectangular texture.
 */
public interface BaseTexture extends BaseRectangle {

    /**
     * Returns the rgb value at given u, v coordinates.
     *
     * @param u the u-coordinate
     * @param v the v-coordinate
     * @return the rgb value at the coordinates
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public abstract int get(int u, int v);

    /**
     * Sets the rgb value at given u, v coordinates.
     *
     * @param u the u-coordinate
     * @param v the v-coordinate
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public abstract void set(int u, int v, int rgb);

}
