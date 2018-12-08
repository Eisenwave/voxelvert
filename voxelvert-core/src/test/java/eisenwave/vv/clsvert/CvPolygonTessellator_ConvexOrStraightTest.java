package eisenwave.vv.clsvert;

import eisenwave.torrens.object.Vertex3f;
import eisenwave.torrens.stl.STLTriangle;
import eisenwave.vv.clsvert.CvVoxelArrayToSTL_Optimized.PolygonVertex;

import org.junit.Test;

import static org.junit.Assert.*;

public class CvPolygonTessellator_ConvexOrStraightTest {
    
    @Test
    public void simple0() {
        Vertex3f normal = new Vertex3f(0, 0, 1);
        PolygonVertex[] vertices = {
            new PolygonVertex(0, 0, 0, true),
            new PolygonVertex(0, 1, 0, false),
            new PolygonVertex(0, 2, 0, true),
            new PolygonVertex(2, 2, 0, true),
            new PolygonVertex(2, 0, 0, true)
        };
        
        STLTriangle[] triangles = new CvPolygonTessellator_ConvexOrStraight().invoke(vertices, normal);
        for (STLTriangle t : triangles)
            System.out.println(t);
        
        assertTrue(triangles.length == 3);
    }
    
    @Test
    public void simple1() {
        Vertex3f normal = new Vertex3f(1, 0, 0);
        PolygonVertex[] vertices = {
            new PolygonVertex(0, 0, 8, true),
            new PolygonVertex(0, 1, 8, false),
            new PolygonVertex(0, 2, 8, false),
            new PolygonVertex(0, 8, 8, true),
            new PolygonVertex(0, 8, 0, true),
            new PolygonVertex(0, 0, 0, true)
        };
        
        STLTriangle[] triangles = new CvPolygonTessellator_ConvexOrStraight().invoke(vertices, normal);
        for (STLTriangle t : triangles)
            System.out.println(t);
    }
}
