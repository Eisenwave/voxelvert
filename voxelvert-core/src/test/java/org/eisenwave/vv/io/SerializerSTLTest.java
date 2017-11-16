package org.eisenwave.vv.io;

import net.grian.torrens.voxel.DeserializerQEF;
import net.grian.torrens.stl.SerializerSTL;
import net.grian.torrens.stl.STLModel;
import net.grian.torrens.voxel.VoxelArray;
import org.eisenwave.vv.VVTest;
import org.eisenwave.vv.clsvert.CvVoxelArrayToSTL;
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
        STLModel model = new CvVoxelArrayToSTL().invoke(voxels);
        logger.fine("converted "+voxels+" to "+model);

        File out = new File(VVTest.DIR_FILES, "SerializerSTLTest.stl");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed out fromPoints "+out);
        
        new SerializerSTL(logger).toFile(model, out);
    }

}
