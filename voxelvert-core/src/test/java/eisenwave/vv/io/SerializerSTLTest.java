package eisenwave.vv.io;

import eisenwave.torrens.voxel.DeserializerQEF;
import eisenwave.torrens.stl.SerializerSTL;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.VVTest;
import eisenwave.vv.clsvert.CvVoxelArrayToSTL_Naive;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerializerSTLTest {

    @Test
    public void serialize() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        VoxelArray voxels = new DeserializerQEF(logger).fromResource(getClass(), "sword.qef");
        STLModel model = new CvVoxelArrayToSTL_Naive(logger).invoke(voxels);
        logger.fine("converted "+voxels+" to "+model);

        File out = new File(VVTest.directory(), "SerializerSTLTest.stl");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed out fromPoints "+out);
        
        new SerializerSTL(logger).toFile(model, out);
    }

}
