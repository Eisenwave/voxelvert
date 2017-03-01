package net.grian.vv.clsvert;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQB;
import net.grian.torrens.qbcl.DeserializerQEF;
import net.grian.torrens.mc.SerializerMCModel;
import net.grian.torrens.img.SerializerPNG;
import net.grian.torrens.mc.MCModel;
import net.grian.torrens.qbcl.QBModel;
import net.grian.vv.VVTest;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class ClassverterVoxelsToMCTest {

    @Test
    public void getSurface() throws Exception {
        final BlockSelection
            box = BlockSelection.fromPoints(1, 2, 3, 2, 8, 18),
            surfaceNX = ClassverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_X),
            surfacePX = ClassverterVoxelsToMC.getSurface(box, Direction.POSITIVE_X),
            surfaceNY = ClassverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_Y),
            surfacePY = ClassverterVoxelsToMC.getSurface(box, Direction.POSITIVE_Y),
            surfaceNZ = ClassverterVoxelsToMC.getSurface(box, Direction.NEGATIVE_Z),
            surfacePZ = ClassverterVoxelsToMC.getSurface(box, Direction.POSITIVE_Z);

        assertEquals(surfaceNX, BlockSelection.fromPoints(0, 2, 3, 0, 8, 18));
        assertEquals(surfacePX, BlockSelection.fromPoints(3, 2, 3, 3, 8, 18));
        assertEquals(surfaceNY, BlockSelection.fromPoints(1, 1, 3, 2, 1, 18));
        assertEquals(surfacePY, BlockSelection.fromPoints(1, 9, 3, 2, 9, 18));
        assertEquals(surfaceNZ, BlockSelection.fromPoints(1, 2, 2, 2, 8, 2));
        assertEquals(surfacePZ, BlockSelection.fromPoints(1, 2, 19, 2, 8, 19));
    }

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        VoxelArray voxels = new DeserializerQEF(logger).fromResource(getClass(), "debug.qef");
        VoxelMesh mesh = ConvertUtil.convert(voxels, VoxelMesh.class);
        MCModel model = new ClassverterVoxelsToMC().invoke(mesh);
        BufferedImage image = ConvertUtil.convert(model.getTexture("texture"), BufferedImage.class);

        File jsonOut = new File(VVTest.DIR_FILES, "ClassverterVoxelsToMCTest.json");
        if (!jsonOut.exists() && !jsonOut.createNewFile()) throw new IOException("failed to fromPoints json");
        new SerializerMCModel().toFile(model, jsonOut);

        File imgOut = new File(VVTest.DIR_FILES, "ClassverterVoxelsToMCTest.png");
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