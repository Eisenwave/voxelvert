package net.grian.vv.core;

import net.grian.spatium.Spatium;
import net.grian.spatium.cache.CacheMath;
import net.grian.spatium.geo.*;
import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.Flags;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerQEF;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VoxelCanvasTest {

    private static void print(VoxelArray voxels, String name) throws IOException {
        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\"+name+".qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQEF().toFile(voxels, out);
    }

    @Test
    public void drawVisibility() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);

        canvas.selectContent(false);
        canvas.drawRaw((x, y, z) -> {
            float brightness = Flags.bitSum(voxels.getVisibilityMask(x, y, z)) / 6F;
            return ColorMath.fromRGB(brightness, brightness, brightness);
        });

        VoxelArray content = canvas.getContent();
        System.out.println(voxels+" = "+content);
        assertEquals(voxels.size(), content.size());

        print(content, "VoxelCanvasTest_drawVisibility");
    }
    
    @Test
    public void drawTriangle() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(256, 256, 256);
        Triangle triangle = Triangle.fromPoints(
            0, 0, 0,
            250, 127, 8,
            8, 128, 250);
        
        long now = System.nanoTime();
        canvas.drawTriangle(triangle, ColorMath.SOLID_RED, ColorMath.SOLID_GREEN, ColorMath.SOLID_BLUE);
        double time = (System.nanoTime()-now) / CacheMath.BILLION;
        System.out.println("drawing triangle took "+time+" s");
        
        print(canvas.getContent(), "VoxelCanvasTest_Triangle");
    }

    @Test
    public void drawLine() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        canvas.drawLine(0, 7, 15, 63, 63, 63, ColorMath.DEBUG1);

        print(canvas.getContent(), "VoxelCanvasTest_drawLine");
    }

    @Test
    public void drawPath() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);

        Path ray = Path.linear(
                Vector.fromXYZ(0.5F, 0.5F, 0.5F),
                Vector.fromXYZ(63.5F, 63.5F, 63.5F));
        canvas.drawPath(ray, 1F, ColorMath.DEBUG1);

        for (int i = 0; i<10; i++) {
            Path circle = Path.circle(
                    Vector.fromXYZ(32, 32, 32), //center
                    20, //radius
                    Vector.random(1)); //normal
            canvas.drawPath(circle, 128, ColorMath.fromHSB(PrimMath.randomFloat(1F), 0.5F, 0.75F));
        }

        print(canvas.getContent(), "VoxelCanvasTest_drawPath");
    }

    @Test
    public void drawSpace() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        OrientedBB box = OrientedBB.fromAABB(AxisAlignedBB.fromPoints(16, 16, 16, 48, 48, 48));
        box.rotateX(Spatium.radians(45));
        box.rotateZ(Math.atan(CacheMath.INV_SQRT_2));//

        canvas.drawSpace(box, ColorMath.DEBUG2);

        print(canvas.getContent(), "VoxelCanvasTest_drawSpace");
    }

    @Test
    public void contentSelect() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);
        assertEquals(voxels.size(), canvas.contentSize());

        canvas.selectContent(false);
        assertEquals(canvas.contentSize(), canvas.selectionSize());
    }

    @Test
    public void fullSelect() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(3, 3, 3);
        assertEquals(canvas.getVolume(), canvas.selectionSize());

        canvas.forEachPosition(canvas::unselect);
        /*
        canvas.forEachPosition((x,y,z) -> {
            if (canvas.isSelected(x,y,z))
                System.out.println(ANSI.GREEN+x+","+y+","+z+ANSI.RESET);
            else
                System.out.println(ANSI.RED+x+","+y+","+z+ANSI.RESET);
        });
        System.out.println(canvas);
        */
        assertEquals(0, canvas.selectionSize());

        canvas.forEachPosition(canvas::select);
        assertEquals(canvas.getVolume(), canvas.selectionSize());
    }

}