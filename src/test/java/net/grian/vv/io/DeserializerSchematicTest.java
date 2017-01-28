package net.grian.vv.io;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.voxel.BlockArray;
import net.grian.spatium.voxel.BlockKey;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerSchematic;
import net.grian.torrens.io.SerializerPNG;
import net.grian.torrens.object.Texture;
import net.grian.torrens.util.Resources;
import net.grian.vv.VoxelVertTest;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.convert.ConverterBlocksToVoxels;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;

public class DeserializerSchematicTest {

    private final static BlockKey
    COAL_BLOCK = new BlockKey(173, 0);

    @Test
    public void deserialize() throws Exception {
        BlockArray blocks = new DeserializerSchematic().fromResource(getClass(), "bunny.schematic");

        assertEquals(COAL_BLOCK, blocks.getBlock(50, 30, 36));

        ExtractableColor[] extractableColors = VoxelVertTest.getInstance().getRegistry().getColors("default");
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        final int flags = ConverterBlocksToVoxels.SHOW_MISSING;

        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, flags);
        Texture front = ConvertUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, false);
        BufferedImage image = ConvertUtil.convert(front, BufferedImage.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\maps\\DeserializerSchematicTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        new SerializerPNG().toFile(image, out);
    }

}
