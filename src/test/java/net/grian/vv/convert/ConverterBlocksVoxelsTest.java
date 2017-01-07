package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.BlockArray;
import net.grian.vv.core.Texture;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.ConvUtil;
import net.grian.vv.util.Resources;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ConverterBlocksVoxelsTest {

    @Test
    public void invoke() throws Exception {
        long now = System.currentTimeMillis();
        InputStream stream = Resources.getStream(getClass(), "bunny.schematic");
        BlockArray blocks = new DeserializerSchematic().deserialize(stream);
        stream.close();
        System.out.println((System.currentTimeMillis()-now)+": "+blocks);

        ColorMap colors = ColorMap.loadDefault();
        System.out.println((System.currentTimeMillis()-now)+": "+colors);

        final int flags = ConverterBlocksVoxels.IGNORE_ALPHA | ConverterBlocksVoxels.SHOW_MISSING;
        VoxelArray voxels = ConvUtil.convert(blocks, VoxelArray.class, colors, flags);
        System.out.println((System.currentTimeMillis()-now)+": "+voxels);

        Texture texture = ConvUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, true);
        BufferedImage image = ConvUtil.convert(texture, BufferedImage.class);
        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\maps\\ConverterBlocksVoxelsTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create file");
        ImageIO.write(image, "png", out);
        System.out.println((System.currentTimeMillis()-now)+": done");
    }

}