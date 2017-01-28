package net.grian.vv.convert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQB;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerModelJSON;
import net.grian.torrens.io.SerializerPNG;
import net.grian.torrens.object.MCModel;
import net.grian.torrens.object.QBModel;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class ConverterVoxelsToMCTest {

    @Test
    public void getSurface() throws Exception {
        final BlockSelection
                box = BlockSelection.fromPoints(1, 2, 3, 2, 8, 18),
                surfaceNX = ConverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_X),
                surfacePX = ConverterVoxelsToMC.getSurface(box, Direction.POSITIVE_X),
                surfaceNY = ConverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_Y),
                surfacePY = ConverterVoxelsToMC.getSurface(box, Direction.POSITIVE_Y),
                surfaceNZ = ConverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_Z),
                surfacePZ = ConverterVoxelsToMC.getSurface(box, Direction.POSITIVE_Z);

        assertEquals(surfaceNX, BlockSelection.fromPoints(0, 2, 3, 0, 8, 18));
        assertEquals(surfacePX, BlockSelection.fromPoints(3, 2, 3, 3, 8, 18));
        assertEquals(surfaceNY, BlockSelection.fromPoints(1, 1, 3, 2, 1, 18));
        assertEquals(surfacePY, BlockSelection.fromPoints(1, 9, 3, 2, 9, 18));
        assertEquals(surfaceNZ, BlockSelection.fromPoints(1, 2, 2, 2, 8, 2));
        assertEquals(surfacePZ, BlockSelection.fromPoints(1, 2, 19, 2, 8, 19));
    }

    @Test
    public void invoke() throws Exception {
        Logger.getGlobal().setLevel(Level.FINE);
        VoxelArray voxels = new DeserializerQEF(Logger.getGlobal()).fromResource(getClass(), "debug.qef");
        VoxelMesh mesh = ConvertUtil.convert(voxels, VoxelMesh.class);
        MCModel model = new ConverterVoxelsToMC().invoke(mesh);
        BufferedImage image = ConvertUtil.convert(model.getTexture("texture"), BufferedImage.class);

        File jsonOut = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelsToMCTest.json");
        if (!jsonOut.exists() && !jsonOut.createNewFile()) throw new IOException("failed to fromPoints json");
        new SerializerModelJSON().toFile(model, jsonOut);

        File imgOut = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelsToMCTest.png");
        if (!imgOut.exists() && !imgOut.createNewFile()) throw new IOException("failed to fromPoints texture");
        new SerializerPNG().toFile(image, imgOut);
    }

    @Test
    public void testVolumePreserve() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        MCModel mc = ConvertUtil.convert(mesh, MCModel.class);

        assertEquals(mesh.getCombinedVolume(), (int) mc.getCombinedVolume());
    }

    @Test
    public void testElementsPreserve() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        MCModel mc = ConvertUtil.convert(mesh, MCModel.class);

        assertEquals(mesh.size(), mc.size());
    }

}