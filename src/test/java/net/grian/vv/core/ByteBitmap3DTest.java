package net.grian.vv.core;

import net.grian.spatium.geo.BlockVector;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteBitmap3DTest {

    @Test
    public void enable() throws Exception {
        ByteBitmap3D map = new ByteBitmap3D(10, 20, 30);
        BlockVector pos = BlockVector.fromXYZ(7, 13, 23);

        map.enable(pos);
        assertTrue(map.contains(pos));
        assertFalse(map.contains(pos.getX(), pos.getY()+1, pos.getZ()));
    }

    @Test
    public void disable() throws Exception {
        ByteBitmap3D map = new ByteBitmap3D(10, 20, 30);
        BlockVector pos = BlockVector.fromXYZ(7, 13, 23);

        map.enable(pos);
        assertTrue(map.contains(pos));

        map.disable(pos);
        assertFalse(map.contains(pos));
    }

}