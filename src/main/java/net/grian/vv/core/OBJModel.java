package net.grian.vv.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Data representation of a Wavefront Object Model.
 */
public class OBJModel {

    private final List<Vertex3f> vertices = new ArrayList<>();
    private final List<Vertex3f> normals = new ArrayList<>();
    private final List<Vertex2f> textures = new ArrayList<>();

    private final List<OBJFace> faces = new ArrayList<>();

    public Vertex3f getVertex(int index) {
        return vertices.get(index);
    }

    public Vertex3f getNormal(int index) {
        return normals.get(index);
    }

    public Vertex2f getTexture(int index) {
        return textures.get(index);
    }

    public OBJFace getFace(int index) {
        return faces.get(index);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getNormalCount() {
        return normals.size();
    }

    public int getTextureVertexCount() {
        return textures.size();
    }

    public int getFaceCount() {
        return faces.size();
    }

    public int addVertex(Vertex3f vertex) {
        vertices.add(vertex);
        return vertices.size()-1;
    }

    public int addNormal(Vertex3f vertex) {
        normals.add(vertex);
        return normals.size()-1;
    }

    public int addTexture(Vertex2f vertex) {
        textures.add(vertex);
        return textures.size()-1;
    }

    public int addFace(OBJFace face) {
        faces.add(face);
        return faces.size()-1;
    }

}
