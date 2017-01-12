package net.grian.vv.core;

import java.io.Serializable;
import java.util.Objects;

public class OBJFace {

    private final OBJTriplet[] shape;

    public OBJFace(OBJTriplet... shape) {
        Objects.requireNonNull(shape);
        this.shape = shape;
    }

    public OBJTriplet[] getShape() {
        return shape;
    }

    public int size() {
        return shape.length;
    }

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
