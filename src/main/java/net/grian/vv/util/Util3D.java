package net.grian.vv.util;

import net.grian.spatium.enums.Direction;
import net.grian.torrens.object.Vertex3f;

public final class Util3D {

    private Util3D() {}

    private final static Vertex3f[] NORMALS = new Vertex3f[Direction.values().length];

    static {
        for (Direction dir : Direction.values())
            NORMALS[dir.ordinal()] = Util3D.internalNormalOf(dir);
    }

    public static Vertex3f internalNormalOf(Direction dir) {
        switch (dir) {
            case NEGATIVE_X: return new Vertex3f(-1, 0, 0);
            case POSITIVE_X: return new Vertex3f( 1, 0, 0);
            case NEGATIVE_Y: return new Vertex3f(0, -1, 0);
            case POSITIVE_Y: return new Vertex3f(0,  1, 0);
            case NEGATIVE_Z: return new Vertex3f(0, 0, -1);
            case POSITIVE_Z: return new Vertex3f(0, 0,  1);
            default: throw new IllegalArgumentException("unknown direction: "+dir);
        }
    }

    public static Vertex3f normalOf(Direction dir) {
        return NORMALS[dir.ordinal()];
    }

}
