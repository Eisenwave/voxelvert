package net.grian.vv.clsvert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.voxel.BlockArray;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.mc.DeserializerSchematic;
import net.grian.torrens.img.Texture;
import net.grian.torrens.util.Resources;
import net.grian.vv.VVTest;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.io.ExtractableColor;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

public class ClassverterBlocksToVoxelsTest {

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        long now = System.currentTimeMillis();
        BlockArray blocks = new DeserializerSchematic().fromResource(getClass(), "bunny.schematic");
        logger.fine((System.currentTimeMillis()-now)+": "+blocks);
        
        ExtractableColor[] extractableColors = VVTest.getInstance().getRegistry().getColors("default");
        logger.fine((System.currentTimeMillis()-now)+": got colors");
        
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        logger.fine((System.currentTimeMillis()-now)+": "+pack);
        
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        logger.fine((System.currentTimeMillis()-now)+": "+colors);
    
        now = System.currentTimeMillis();
        final int flags = ClassverterBlocksToVoxels.IGNORE_ALPHA | ClassverterBlocksToVoxels.SHOW_MISSING;
        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, flags);
        logger.fine((System.currentTimeMillis()-now)+": "+voxels);
    
        now = System.currentTimeMillis();
        Texture texture = ConvertUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, true);
        BufferedImage image = ConvertUtil.convert(texture, BufferedImage.class, true, false);
        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\maps\\ClassverterBlocksToVoxelsTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints file");
        ImageIO.write(image, "png", out);
        logger.fine((System.currentTimeMillis()-now)+": done");
    }

}