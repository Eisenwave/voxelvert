package net.grian.vv.core;

/**
 * An immutable pair of float coordinates.
 */
public class Vertex2f {

    private final float x, y;

    public Vertex2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
