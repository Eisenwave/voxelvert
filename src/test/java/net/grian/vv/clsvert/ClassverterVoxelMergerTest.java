package net.grian.vv.clsvert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.qbcl.DeserializerQB;
import net.grian.torrens.qbcl.DeserializerQEF;
import net.grian.torrens.qbcl.SerializerQB;
import net.grian.torrens.qbcl.QBModel;
import net.grian.torrens.util.ANSI;
import net.grian.vv.VVTest;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class ClassverterVoxelMergerTest {

    @Test
    public void mergeQB() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class); //turn into flat voxels

        mesh = ConvertUtil.convert(array, VoxelMesh.class); //merge back into mesh (only x-merge)
        model = ConvertUtil.convert(mesh, QBModel.class);

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        File out = new File(VVTest.DIR_FILES, "ClassverterVoxelMergerTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        new SerializerQB().toFile(model, out);
    }

    @Test
    public void mergeQEF() throws Exception {
        VoxelArray array = new DeserializerQEF().fromResource(getClass(), "sword.qef");
        VoxelMesh mesh = ConvertUtil.convert(array, VoxelMesh.class);

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        QBModel model = ConvertUtil.convert(mesh, QBModel.class);

        File out = new File(VVTest.DIR_FILES, "ConverterVoxelMergerTest2.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        new SerializerQB().toFile(model, out);
    }

    /**
     * Tests whether the total amount of voxels is being preserved when voxels in a {@link VoxelArray} are being merged
     * into a {@link VoxelMesh}.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void preserveVolume() throws Exception {
        VoxelArray array = new VoxelArray(8, 8, 8);
        Random r = new Random();
        array.forEachPosition(pos -> {if (r.nextBoolean()) array.setRGB(pos, ColorMath.SOLID_RED);});
        VoxelMesh mesh = ConvertUtil.convert(array, VoxelMesh.class);

        assertEquals(array.size(), mesh.getCombinedVolume());
    }

    /**
     * Tests whether the total amount of voxels is being preserved when voxels in a {@link VoxelArray} are being merged
     * into a {@link VoxelMesh}.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void preserveVoxelCount() throws Exception {
        VoxelArray array = new VoxelArray(8, 8, 8);
        Random r = new Random();
        array.forEachPosition(pos -> {if (r.nextBoolean()) array.setRGB(pos, ColorMath.SOLID_RED);});

        final int count = array.size();
        VoxelMesh mesh = ConvertUtil.convert(array, VoxelMesh.class);

        assertEquals(count, mesh.voxelCount());
    }

    @Test
    public void testWorstCaseMerging() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        Thread thread = new Thread(() -> {
            for (int i = 0; i<8; i++) {
                long now = System.currentTimeMillis();
                VoxelArray array = new VoxelArray(64, 64, 64);
                Random r = new Random();
                array.forEachPosition(pos -> {if (r.nextBoolean()) array.setRGB(pos, ColorMath.SOLID_RED);});
                logger.fine("generated "+array+" array in "+(System.currentTimeMillis()-now)+"ms");

                long now2 = System.currentTimeMillis();
                VoxelMesh cluster = new ClassverterVoxelMerger(logger).invoke(array);
                long time = (System.currentTimeMillis() - now2);
                logger.fine("merged to " + cluster + " in " + ANSI.RED+time+"ms"+ANSI.RESET);
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}