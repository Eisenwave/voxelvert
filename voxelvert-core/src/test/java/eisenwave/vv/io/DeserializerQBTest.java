package eisenwave.vv.io;

import eisenwave.vv.VVTest;
import eisenwave.vv.clsvert.CvQBToVoxelMesh;
import eisenwave.vv.clsvert.CvVoxelArrayToTexture;
import eisenwave.vv.clsvert.CvVoxelMeshToVoxelArray;
import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.*;
import eisenwave.torrens.img.Texture;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeserializerQBTest {

    @Test
    public void deserialize() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        QBModel model = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        {
            File out = new File(VVTest.directory(), "DeserializerQBTest.qb");
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create " + out);
            new SerializerQB().toFile(model, out);
        }
    
        VoxelMesh mesh = new CvQBToVoxelMesh().invoke(model);
        assertNotNull(mesh);
    
        VoxelArray array = new CvVoxelMeshToVoxelArray().invoke(mesh);
        Texture texture = new CvVoxelArrayToTexture().invoke(array, Direction.NEGATIVE_Z, true, true);
        {
            File out = new File(VVTest.directory(), "DeserializerQBTest.png");
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create " + out);
            ImageIO.write(texture.toImage(true), "png", out);
        }
    }
    
    @Test
    public void loadsProperly() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        QBModel model = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = new CvQBToVoxelMesh().invoke(model);
        VoxelArray array = new CvVoxelMeshToVoxelArray().invoke(mesh);
        
        int rgb = array.getRGB(52, 41, 29);
        assertEquals(new Color(43, 43, 43), new Color(rgb));
        assertEquals(255, ColorMath.alpha(rgb));
    }

}
