package net.grian.vv.convert;

import net.grian.spatium.util.PrimMath;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.io.DeserializerQB;
import net.grian.vv.io.DeserializerQEF;
import net.grian.vv.io.SerializerQB;
import net.grian.vv.util.ColorMath;
import net.grian.vv.util.ConvUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

public class ConverterVoxelMergerTest {

    @Test
    public void mergeQB() throws Exception {
        VoxelMesh mesh = new DeserializerQB().deserialize(getClass(), "sniper.qb");
        VoxelArray array = ConvUtil.convert(mesh, VoxelArray.class); //turn into flat voxels
        mesh = ConvUtil.convert(array, VoxelMesh.class, 3); //merge back into mesh (only x-merge)

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelMergerTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().serialize(mesh, out);
    }

    @Test
    public void mergeQEF() throws Exception {
        VoxelArray array = new DeserializerQEF().deserialize(getClass(), "sword.qef");
        VoxelMesh mesh = ConvUtil.convert(array, VoxelMesh.class);

        for (VoxelMesh.Element element : mesh)
            element.getArray().fill(ColorMath.fromHSB(PrimMath.randomFloat(1), 0.5F, 0.75F));

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelMergerTest2.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().serialize(mesh, out);
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
        VoxelMesh mesh = ConvUtil.convert(array, VoxelMesh.class);

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
        VoxelMesh mesh = ConvUtil.convert(array, VoxelMesh.class);

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