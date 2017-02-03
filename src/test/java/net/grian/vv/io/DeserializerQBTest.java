package net.grian.vv.io;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQB;
import net.grian.torrens.qbcl.QBModel;
import net.grian.torrens.img.Texture;
import net.grian.vv.VVTest;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public class DeserializerQBTest {

    @Test
    public void deserialize() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        QBModel model = new DeserializerQB(logger).fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);

        assertNotNull(mesh);

        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class);
        Texture texture = ConvertUtil.convert(array, Texture.class, Direction.NEGATIVE_Z, true, true);

        File out = new File(VVTest.DIR_FILES, "DeserializerQBTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        ImageIO.write(texture.toImage(), "png", out);
    }

}