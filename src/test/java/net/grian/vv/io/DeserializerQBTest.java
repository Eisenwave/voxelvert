package net.grian.vv.io;

import net.grian.spatium.enums.Direction;
import net.grian.vv.core.Texture;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvUtil;
import net.grian.vv.util.Resources;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class DeserializerQBTest {

    @Test
    public void deserialize() throws Exception {
        VoxelMesh mesh = new DeserializerQB().deserialize(getClass(), "sniper.qb");
        System.out.println("mesh = "+mesh);

        assertNotNull(mesh);

        VoxelArray array = ConvUtil.convert(mesh, VoxelArray.class);
        Texture texture = ConvUtil.convert(array, Texture.class, Direction.NEGATIVE_Z, true, true);
        BufferedImage image = ConvUtil.convert(texture, BufferedImage.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\maps\\DeserializerQBTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        ImageIO.write(image, "png", out);
    }

}