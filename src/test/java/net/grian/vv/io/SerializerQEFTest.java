package net.grian.vv.io;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.SerializerQEF;
import net.grian.vv.VVTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SerializerQEFTest {
    
    @Test
    public void makeBigQEF() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        VoxelArray array = new VoxelArray(400, 400 ,400);
        array.forEachPosition(((x, y, z) -> {
            if ((x + y + z) % 2 == 0) array.setRGB(x, y, z, ColorMath.DEBUG1);
        }));
        
        File out = new File(VVTest.DIR_FILES, "SerializerQEFTest.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerQEF(logger).toFile(array, out);
        assertTrue(true);
    }
    
    
}
