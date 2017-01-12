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

        private final Vertex normal, a, b, c;
        private final short attribute;

        public STLTriangle(Vertex normal, Vertex a, Vertex b, Vertex c, short attribute) {
            Arguments.requireNonnull(normal, a, b, c);
            this.normal = normal;
            this.a = a;
            this.b = b;
            this.c = c;
            this.attribute = attribute;
        }

        public STLTriangle(Vertex normal, Vertex a, Vertex b, Vertex c) {
            this(normal, a, b, c, (short) 0);
        }

        public Vertex getA() {
            return a;
        }

        public Vertex getB() {
            return b;
        }

        public Vertex getC() {
            return c;
        }

        public Vertex getNormal() {
            return normal;
        }

        public short getAttribute() {
            return attribute;
        }

    }

}
