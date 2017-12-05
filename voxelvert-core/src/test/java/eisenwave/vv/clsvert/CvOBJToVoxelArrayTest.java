package eisenwave.vv.clsvert;

import eisenwave.vv.VVTest;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.torrens.wavefront.DeserializerOBJ;
import eisenwave.torrens.voxel.SerializerQEF;
import eisenwave.torrens.wavefront.OBJModel;
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
