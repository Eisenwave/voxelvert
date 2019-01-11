package eisenwave.vv.clsvert;

import eisenwave.torrens.object.Vertex3f;
import eisenwave.torrens.stl.STLTriangle;
import eisenwave.vv.clsvert.CvVoxelArrayToSTL_Hybrid.PolygonVertex;

import java.util.*;

public class CvPolygonTessellator_ConvexOrStraight
    implements Classverter<PolygonVertex[], STLTriangle[]> {
    
    @Deprecated
    @Override
    public STLTriangle[] invoke(PolygonVertex[] from, Object... args) {
        return invoke(from, (Vertex3f) args[0]);
    }
    
    public static STLTriangle[] invoke(PolygonVertex[] vertices, Vertex3f normal) {
        assert vertices.length >= 3;
        
        List<STLTriangle> result = new ArrayList<>(8);
        
        final int maxIndex = vertices.length - 1;
        
        outer:
        for (int a = 2, b = 1, c = 0, v = vertices.length; ; a = a == maxIndex? 0 : a + 1) {
            
            if (vertices[a] == null)
                continue;
            
            if (v == 3) {
                assert vertices[c] != null;
                result.add(new STLTriangle(normal, vertices[c].to3f(), vertices[b].to3f(), vertices[a].to3f()));
                break;
            }
            
            //assert triple[1] != null;
            if (vertices[b].isConvex()) {
                /*
                 * We must verify that there is at least one convex vertex in the rest of the polygon.
                 * If this was not the case, we would create a triangle with an unaccounted vertex between two of its
                 * other vertices.
                 * The result would be at least one edge which has a vertex inside of it which is not part of the edge.
                 * The further result would be a mesh with triangles within other triangles.
                 */
                for (int d = a == maxIndex? 0 : a + 1; ; d = d == maxIndex? 0 : d + 1) {
                    if (d == c)
                        continue outer;
                    //System.out.println("checking: @" + d + " = " + vertices[d]);
                    if (vertices[d] != null && vertices[d].isConvex())
                        break;
                }
                
                STLTriangle triangle = new STLTriangle(normal,
                    vertices[c].to3f(),
                    vertices[b].to3f(),
                    vertices[a].to3f());
                result.add(triangle);
                //System.out.println(Arrays.toString(vertices) + "     |     " + triangle);
                vertices[c].setConvex(true);
                vertices[b] = null;
                vertices[a].setConvex(true);
                
                c = a;
                b = a == maxIndex? 0 : a + 1;
                while (vertices[b] == null)
                    b = b == maxIndex? 0 : b + 1;
                a = b;
                v--;
            }
            else {
                c = b;
                b = a;
            }
        }
        
        return result.toArray(new STLTriangle[0]);
    }
    
}
