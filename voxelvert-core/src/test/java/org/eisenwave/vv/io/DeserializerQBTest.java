package org.eisenwave.vv.io;

import net.grian.spatium.enums.Direction;
import net.grian.torrens.util.ColorMath;
import net.grian.torrens.voxel.DeserializerQB;
import net.grian.torrens.voxel.QBModel;
import net.grian.torrens.img.Texture;
import net.grian.torrens.voxel.SerializerQB;
import net.grian.torrens.voxel.VoxelArray;
import org.eisenwave.vv.VVTest;
import net.grian.torrens.voxel.VoxelMesh;
import org.eisenwave.vv.util.ConvertUtil;
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
            File out = new File(VVTest.DIR_FILES, "DeserializerQBTest.qb");
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create " + out);
            new SerializerQB().toFile(model, out);
        }
        
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        assertNotNull(mesh);

        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class);
        Texture texture = ConvertUtil.convert(array, Texture.class, Direction.NEGATIVE_Z, true, true);
        {
            File out = new File(VVTest.DIR_FILES, "DeserializerQBTest.png");
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create " + out);
            ImageIO.write(texture.toImage(true), "png", out);
        }
    }
    
    @Test
    public void loadsProperly() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        QBModel model = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class);
        
        int rgb = array.getRGB(52, 41, 29);
        assertEquals(new Color(43, 43, 43), new Color(rgb));
        assertEquals(255, ColorMath.alpha(rgb));
    }

}
