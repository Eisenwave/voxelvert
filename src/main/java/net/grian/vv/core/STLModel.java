package net.grian.vv.core;

import com.google.common.collect.ImmutableList;
import net.grian.vv.util.Arguments;

import java.util.ArrayList;
import java.util.List;

public class STLModel {

    private final List<STLTriangle> triangles = new ArrayList<>();

    private String header;

    public STLModel(String header) {
        setHeader(header);
    }

    public STLModel() {
        this("");
    }

    //GETTERS

    public List<STLTriangle> getTriangles() {
        return ImmutableList.copyOf(triangles);
    }

    public int size() {
        return triangles.size();
    }

    public String getHeader() {
        return header;
    }

    //SETTERS

    public boolean add(STLTriangle triangle) {
        return triangles.add(triangle);
    }

    public void setHeader(String header) {
        if (header.startsWith("solid")) throw new IllegalArgumentException("header must not start with 'solid'");
        this.header = header;
    }

    public static class STLTriangle {

        private final Vertex3f normal, a, b, c;
        private final short attribute;

        public STLTriangle(Vertex3f normal, Vertex3f a, Vertex3f b, Vertex3f c, short attribute) {
            Arguments.requireNonnull(normal, a, b, c);
            this.normal = normal;
            this.a = a;
            this.b = b;
            this.c = c;
            this.attribute = attribute;
        }

        public STLTriangle(Vertex3f normal, Vertex3f a, Vertex3f b, Vertex3f c) {
            this(normal, a, b, c, (short) 0);
        }

        public Vertex3f getA() {
            return a;
        }

        public Vertex3f getB() {
            return b;
        }

        public Vertex3f getC() {
            return c;
        }

        public Vertex3f getNormal() {
            return normal;
        }

        public short getAttribute() {
            return attribute;
        }

    }

}
