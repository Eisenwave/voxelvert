package org.eisenwave.vv.io;

import net.grian.torrens.util.ColorMath;
import net.grian.torrens.voxel.SerializerQEF;
import net.grian.torrens.voxel.VoxelArray;
import org.eisenwave.vv.VVTest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SerializerQEFTest {
    
    public void makeBigQEF() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        VoxelArray array = new VoxelArray(400, 400 ,400);
        array.forEachPosition(((x, y, z) -> {
            if ((x + y + z) % 2 == 0) array.setRGB(x, y, z, ColorMath.DEBUG1);
        }));
        
        File out = new File(VVTest.DIR_FILES, "big.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerQEF(logger).toFile(array, out);
        assertTrue(true);
    }
    
    
}
