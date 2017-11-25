package eisenwave.vv.util;

import net.grian.spatium.enums.Direction;
import net.grian.torrens.object.Vertex3f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Util3DTest {

    @Test
    public void normalOf() throws Exception {
        assertEquals(new Vertex3f(-1,  0,  0), Util3D.normalOf(Direction.NEGATIVE_X));
        assertEquals(new Vertex3f( 1,  0,  0), Util3D.normalOf(Direction.POSITIVE_X));
        assertEquals(new Vertex3f( 0, -1,  0), Util3D.normalOf(Direction.NEGATIVE_Y));
        assertEquals(new Vertex3f( 0,  1,  0), Util3D.normalOf(Direction.POSITIVE_Y));
        assertEquals(new Vertex3f( 0,  0, -1), Util3D.normalOf(Direction.NEGATIVE_Z));
        assertEquals(new Vertex3f( 0,  0,  1), Util3D.normalOf(Direction.POSITIVE_Z));
    }

}
