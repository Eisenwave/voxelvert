package net.grian.vv.core;

import net.grian.vv.util.Arguments;

import java.io.Serializable;
import java.util.Objects;

public class OBJFace {

    private final OBJTriplet[] shape;

    public OBJFace(OBJTriplet... shape) {
        Arguments.requireMin(shape, 1);
        this.shape = shape;
    }

    public OBJTriplet[] getShape() {
        return shape;
    }

    /**
     * Returns amount of vertices this face is made of.
     *
     * @return the amount of vertices
     */
    public int size() {
        return shape.length;
    }

    /**
     * A index triplet consisting of a vertex index, a normal index and a texture index.
     */
    public static class OBJTriplet implements Serializable {

        private final int v, vn, vt;

        public OBJTriplet(int v, int vn, int vt) {
            this.v = v;
            this.vn = vn;
            this.vt = vt;
        }

        public int getVertexIndex() {
            return v;
        }

        public int getNormalIndex() {
            return vn;
        }

        public int getTextureIndex() {
            return vt;
        }

    }

}
