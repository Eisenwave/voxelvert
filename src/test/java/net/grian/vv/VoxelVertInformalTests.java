package net.grian.vv;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.util.ColorMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.DeserializerSchematic;
import net.grian.torrens.io.SerializerQEF;
import net.grian.torrens.object.Texture;
import net.grian.torrens.util.ANSI;
import net.grian.torrens.util.Resources;
import net.grian.vv.convert.*;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

public class VoxelVertInformalTests {

    private final static File
            DIR_VV = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin"),
            DIR_VV_FILES = new File(DIR_VV+File.separator+"files"),
            DIR_VV_MAPS = new File(DIR_VV+File.separator+"maps");

    private final static Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) {
        runStandardTests();
    }

    private static void runStandardTests() {
        try {
            testVoxelSidewaysRenders();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            testQubicleIO(DIR_VV_FILES, "bug.qef");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            testImageVoxelization(DIR_VV_MAPS, "iloveyouall.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            testVoxelMerging();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            testColorExtraction();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            testSchematicIO();
        } catch (IOException e) {
            e.printStackTrace();
        }
        testMergingPerformance();
        //testWorstCaseMergingPerformance(128, 1);
        //testVoxelArrayIterator();
    }

    @Test
    private static void testVoxelArrayIterator() {
        title("VOXEL ARRAY ITERATION");

        VoxelArray array = new VoxelArray(2, 2, 2);
        array.setRGB(0, 0, 0, 0xFF_00_00_00);
        array.setRGB(1, 0, 0, 0xFF_FF_FF_FF);
        array.setRGB(1, 1, 1, 0xFF_FF_FF_00);
        array.setRGB(0, 1, 0, 0xFF_00_FF_FF);
        array.setRGB(0, 0, 1, 0xFF_FF_00_FF);

        System.out.println("ARRAY 1: ("+array.size()+")");
        for (VoxelArray.Voxel v : array)
            System.out.println(v);

        VoxelArray array2 = new VoxelArray(2, 3, 4);
        array2.fill(0xFF_FF_FF_FF);

        System.out.println("ARRAY 2: ("+array2.size()+")");
        for (VoxelArray.Voxel v : array2)
            System.out.println(v);

        VoxelArray array3 = new VoxelArray(3, 5, 7);
        System.out.println("ARRAY 3: ("+array3.size()+"/"+array3.getVolume()+")");
        for (VoxelArray.Voxel v : array3)
            System.out.println(v);
    }

    private static void testVoxelSidewaysRenders() throws IOException {
        title("VOXEL SIDEWAYS RENDERS");

        File file = new File(DIR_VV_FILES+File.separator+"Barrett.qef");
        VoxelArray array = new DeserializerQEF(LOGGER).fromFile(file);

        for (Direction dir : Direction.values()) {
            Texture texture = new ConverterVoxelsToTexture().invoke(array, dir, true, true);
            BufferedImage image = new ConverterTextureToImage().invoke(texture);
            String fname = "vvg_test_"+dir.name()+".png";
            File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\maps\\"+fname);
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);
            ImageIO.write(image, "png", out);
            //if (dir == Direction.POSITIVE_X)
            //    System.out.println("UPLOADED TO: "+ ImageUploads.uploadImage(out));
        }
    }

    private static void testQubicleIO(File directory, String name) throws IOException {
        title("QUBICLE IO");

        File in = new File(directory+File.separator+name);
        File out = new File(directory+File.separator+"vvg_test_output.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints file "+out);

        VoxelArray array = new DeserializerQEF(LOGGER).fromFile(in);
        new SerializerQEF().toFile(array, out);
    }

    private static void testMergingPerformance() {
        title("MERGING PERFORMANCE");

        long now = System.currentTimeMillis();
        VoxelArray array = new VoxelArray(128, 128, 128);
        array.fill(ColorMath.SOLID_RED);
        System.out.println("generated "+array+" array in "+(System.currentTimeMillis()-now)+"ms");

        long now2 = System.currentTimeMillis();
        VoxelMesh cluster = new ConverterVoxelMerger().invoke(array);
        System.out.println("merged to "+cluster+" in "+(System.currentTimeMillis()-now2)+"ms");
    }

    private static void testWorstCaseMergingPerformance(int resolution, int times) {
        title("WORST CASE MERGING PERFORMANCE ("+resolution+","+times+")");

        Thread thread = new Thread(() -> {
            for (int i = 0; i<times; i++) {
                long now = System.currentTimeMillis();
                VoxelArray array = new VoxelArray(resolution, resolution, resolution);
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

    private static void testImageVoxelization(File directory, String name) throws IOException {
        title("IMAGE VOXELIZATION");

        File in = new File(directory+File.separator+name);
        File out = new File(directory+File.separator+"vvg_test_imgvoxelization.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints file "+out);

        BufferedImage image = ImageIO.read(in);
        Texture texture = ConvertUtil.convert(image, BufferedImage.class, Texture.class);
        VoxelArray array = new ConverterTextureVoxelizer().invoke(texture, Direction.POSITIVE_X);
        new SerializerQEF().toFile(array, out);
    }

    private static void testVoxelMerging() throws IOException {
        title("VOXEL MERGING");

        InputStream qef = Resources.getStream(VoxelVertInformalTests.class, "/cube32.qef");
        VoxelArray array = new DeserializerQEF(LOGGER).fromStream(qef);
        VoxelMesh cluster = new ConverterVoxelMerger().invoke(array);
        System.out.println(array+" -> "+cluster);
        qef.close();
    }

    private static void testColorExtraction() throws IOException {
        title("COLOR EXTRACTION");

        ZipFile zip = Resources.getZipFile(VoxelVertInformalTests.class, "resourcepacks/default.zip");
        new ConverterColorExtractor().invoke(zip);
    }

    private static void testSchematicIO() throws IOException {
        title("SCHEMATIC IO");

        new DeserializerSchematic().fromStream(Resources.getStream(VoxelVertInformalTests.class, "bunny.schematic"));
    }

    private static void title(String title) {
        System.out.println(ANSI.UNDERLINE_ON+ANSI.GREEN+title+ANSI.RESET);
    }

}
