package org.eisenwave.vv.util;

import net.grian.spatium.enums.Direction;
import net.grian.torrens.object.Vertex3f;
import org.jetbrains.annotations.Contract;

public final class Util3D {
    
    private Util3D() {}
    
    private final static Vertex3f[] NORMALS = new Vertex3f[Direction.values().length];
    
    static {
        for (Direction dir : Direction.values()) {
            NORMALS[dir.ordinal()] = new Vertex3f(dir.x(), dir.y(), dir.z());
        }
    }
    
    @Contract(pure = true)
    public static Vertex3f normalOf(Direction dir) {
        return NORMALS[dir.ordinal()];
    }
    
}
