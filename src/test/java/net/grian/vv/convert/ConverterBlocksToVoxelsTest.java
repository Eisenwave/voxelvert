package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.voxel.BlockArray;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.vv.VoxelVertTest;
import net.grian.vv.cache.ColorMap;
import net.grian.torrens.object.Texture;
import net.grian.torrens.io.DeserializerSchematic;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.util.ConvUtil;
import net.grian.torrens.util.Resources;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ConverterBlocksToVoxelsTest {

    @Test
    public void invoke() throws Exception {
        long now = System.currentTimeMillis();
        BlockArray blocks = new DeserializerSchematic().deserialize(getClass(), "bunny.schematic");
        System.out.println((System.currentTimeMillis()-now)+": "+blocks);

        ExtractableColor[] extractableColors = VoxelVertTest.getInstance().getRegistry().getColors("default");
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        ColorMap colors = ConvUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        System.out.println((System.currentTimeMillis()-now)+": "+colors);

        final int flags = ConverterBlocksToVoxels.IGNORE_ALPHA | ConverterBlocksToVoxels.SHOW_MISSING;
        VoxelArray voxels = ConvUtil.convert(blocks, VoxelArray.class, colors, flags);
        System.out.println((System.currentTimeMillis()-now)+": "+voxels);

        Texture texture = ConvUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, true);
        BufferedImage image = ConvUtil.convert(texture, BufferedImage.class, true, false);
        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\maps\\ConverterBlocksToVoxelsTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create file");
        ImageIO.write(image, "png", out);
        System.out.println((System.currentTimeMillis()-now)+": done");
    }

}