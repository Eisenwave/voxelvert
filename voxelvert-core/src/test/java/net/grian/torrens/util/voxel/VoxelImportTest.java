package net.grian.torrens.util.voxel;

import eisenwave.torrens.voxel.*;
import eisenwave.vv.VVTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class VoxelImportTest {
    
    private final File DEBUG_FILE = new File("/home/user/Files/debug2.binvox");
    
    @Test
    public void test() throws IOException {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        QBModel qb = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        assertNotNull(qb);
        
        VoxelArray qef = new DeserializerQEF(logger).fromResource(getClass(), "debug.qef");
        assertNotNull(qef);
        
        VoxelMesh pnx = new DeserializerPNX(logger).fromResource(getClass(), "debug.pnx");
        assertNotNull(pnx);
    
        VoxelArray binvox = new DeserializerBINVOX(logger).fromResource(getClass(), "chair.binvox");
        assertNotNull(binvox);
        
        System.out.println(binvox);
        
        if (DEBUG_FILE.canWrite()) {
            new SerializerBINVOX().toFile(qef, DEBUG_FILE);
        }
    }

}
