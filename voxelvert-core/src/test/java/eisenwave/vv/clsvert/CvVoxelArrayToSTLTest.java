package eisenwave.vv.clsvert;

import eisenwave.vv.VVTest;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.stl.STLModel;
import eisenwave.torrens.stl.SerializerSTL;
import eisenwave.torrens.voxel.VoxelArray;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class CvVoxelArrayToSTLTest {
    
    private final File DEBUG_FILE = new File(VVTest.DIRECTORY, "axes.stl");
    
    @Test
    public void invoke() throws Exception {
        VoxelArray voxels = new VoxelArray(4, 4, 4);
        voxels.setRGB(1, 0, 0, ColorMath.DEBUG1);
        voxels.setRGB(0, 2, 0, ColorMath.DEBUG1);
        voxels.setRGB(0, 0, 3, ColorMath.DEBUG1);
        
        STLModel model = new CvVoxelArrayToSTL().invoke(voxels);
        assertNotNull(model);
        
        System.out.println(model);
        
        if (DEBUG_FILE.canWrite())
            new SerializerSTL().toFile(model, DEBUG_FILE);
    }
    
}
