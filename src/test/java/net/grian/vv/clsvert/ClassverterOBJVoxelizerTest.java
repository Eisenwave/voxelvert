package net.grian.vv.clsvert;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.wavefront.DeserializerOBJ;
import net.grian.torrens.qbcl.SerializerQEF;
import net.grian.torrens.wavefront.OBJModel;
import net.grian.vv.VVTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassverterOBJVoxelizerTest {
    
    private static void saveAsQEF(VoxelArray voxels, String name) throws IOException {
        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\"+name+".qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);
        
        new SerializerQEF().toFile(voxels, out);
    }
    
    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        OBJModel model = new OBJModel();
        File directory = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\wavefront");
        
        new DeserializerOBJ(model, directory, logger).fromFile(new File(directory, "sword.obj"));
        
        long now = System.currentTimeMillis();
        VoxelArray voxels = new ClassverterOBJVoxelizer(logger).invoke(model, 128, 128, 128);
        long time = System.currentTimeMillis() - now;
        logger.fine("voxelized "+model.getFaceCount()+" faces -> "+voxels+" in "+time+" ms");
    
        saveAsQEF(voxels, "ClassverterOBJVoxelizerTest");
    }
    
}