package eisenwave.vv.clsvert;

import eisenwave.vv.VVTest;
import net.grian.torrens.util.ColorMath;
import net.grian.torrens.stl.STLModel;
import net.grian.torrens.stl.SerializerSTL;
import net.grian.torrens.voxel.VoxelArray;
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
