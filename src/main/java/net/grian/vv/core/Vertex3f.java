package net.grian.vv.core;

/**
 * An immutable triplet of float coordinates.
 */
public class Vertex3f {

    private final float x, y, z;

    public Vertex3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

}
