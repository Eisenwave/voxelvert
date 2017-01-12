package net.grian.vv.core;

import net.grian.spatium.geo.Vector;

import java.util.ArrayList;
import java.util.List;

public class OBJModel {

    private final List<Vector> vertices = new ArrayList<>();
    private final List<Vector> normals = new ArrayList<>();
    private final List<float[]> textures = new ArrayList<>();

    private final List<OBJFace> faces = new ArrayList<>();

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


}
