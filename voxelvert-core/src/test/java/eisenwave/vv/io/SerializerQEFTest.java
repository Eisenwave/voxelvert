package eisenwave.vv.io;

import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.SerializerQEF;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.VVTest;

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
        
        File out = new File(VVTest.directory(), "big.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerQEF(logger).toFile(array, out);
        assertTrue(true);
    }
    
    
}
