package org.eisenwave.vv.clsvert;

import net.grian.torrens.voxel.VoxelArray;
import net.grian.torrens.wavefront.DeserializerOBJ;
import net.grian.torrens.voxel.SerializerQEF;
import net.grian.torrens.wavefront.OBJModel;
import org.eisenwave.vv.VVTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CvOBJToVoxelArrayTest {
    
    //private final static File DIR =
    
    private static void saveAsQEF(VoxelArray voxels, String name) throws IOException {
        File out = new File(VVTest.DIRECTORY, name+".qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);
        
        new SerializerQEF().toFile(voxels, out);
    }
    
    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        OBJModel model = new OBJModel();
        
        new DeserializerOBJ(model, VVTest.DIRECTORY, logger).fromResource(getClass(), "debug.obj");
        
        long now = System.currentTimeMillis();
        VoxelArray voxels = new CvOBJToVoxelArray(logger).invoke(model, 128, 128, 128);
        long time = System.currentTimeMillis() - now;
        logger.fine("voxelized "+model.getFaceCount()+" faces -> "+voxels+" in "+time+" ms");
    
        //saveAsQEF(voxels, "ClassverterOBJVoxelizerTest");
    }
    
}
