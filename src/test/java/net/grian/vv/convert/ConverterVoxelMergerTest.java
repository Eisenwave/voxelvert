package net.grian.vv.convert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQB;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerQB;
import net.grian.torrens.object.QBModel;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ConverterVoxelMergerTest {

    @Test
    public void mergeQB() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        VoxelArray array = ConvertUtil.convert(mesh, VoxelArray.class); //turn into flat voxels

        mesh = ConvertUtil.convert(array, VoxelMesh.class); //merge back into mesh (only x-merge)
        model = ConvertUtil.convert(mesh, QBModel.class);

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelMergerTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().toFile(model, out);
    }

    @Test
    public void mergeQEF() throws Exception {
        VoxelArray array = new DeserializerQEF().fromResource(getClass(), "sword.qef");
        VoxelMesh mesh = ConvertUtil.convert(array, VoxelMesh.class);

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        QBModel model = ConvertUtil.convert(mesh, QBModel.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelMergerTest2.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

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

        Thread thread = new Thread(() -> {
            for (int i = 0; i<8; i++) {
                long now = System.currentTimeMillis();
                VoxelArray array = new VoxelArray(64, 64, 64);
                Random r = new Random();
                array.forEachPosition(pos -> {if (r.nextBoolean()) array.setRGB(pos, ColorMath.SOLID_RED);});
                System.out.println("generated "+array+" array in "+(System.currentTimeMillis()-now)+"ms");

                long now2 = System.currentTimeMillis();
                VoxelMesh cluster = new ConverterVoxelMerger().invoke(array);
                System.out.println("merged to " + cluster + " in " + (System.currentTimeMillis() - now2) + "ms");
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