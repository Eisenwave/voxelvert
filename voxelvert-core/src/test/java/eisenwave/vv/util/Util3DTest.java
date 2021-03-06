package eisenwave.vv.util;

import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.object.Vertex3f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Util3DTest {

    @Test
    public void normalOf() throws Exception {
        assertEquals(new Vertex3f(-1,  0,  0), Util3D.vectorOf(Direction.NEGATIVE_X));
        assertEquals(new Vertex3f( 1,  0,  0), Util3D.vectorOf(Direction.POSITIVE_X));
        assertEquals(new Vertex3f( 0, -1,  0), Util3D.vectorOf(Direction.NEGATIVE_Y));
        assertEquals(new Vertex3f( 0,  1,  0), Util3D.vectorOf(Direction.POSITIVE_Y));
        assertEquals(new Vertex3f( 0,  0, -1), Util3D.vectorOf(Direction.NEGATIVE_Z));
        assertEquals(new Vertex3f( 0,  0,  1), Util3D.vectorOf(Direction.POSITIVE_Z));
    }

}
