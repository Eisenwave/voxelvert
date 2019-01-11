package eisenwave.vv.object;

import eisenwave.spatium.util.Flags;
import eisenwave.vv.VVTest;
import eisenwave.torrens.util.ColorMath;
import eisenwave.torrens.voxel.*;
import eisenwave.vv.clsvert.CvVoxelArrayToQB;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

@Deprecated
public class VoxelCanvasTest {
    
    private static void saveAs(VoxelArray voxels, String name, boolean qef) throws IOException {
        File out = new File(VVTest.directory(), name + (qef? ".qef" : ".qb"));
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create " + out);
        
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        if (qef)
            new SerializerQEF(logger).toFile(voxels, out);
        else
            new SerializerQB(logger).toFile(CvVoxelArrayToQB.invoke(voxels), out);
    }
    
    @Test
    public void drawVisibility() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        VoxelArray voxels = new DeserializerQEF(logger).fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);
        
        canvas.selectContent(false);
        canvas.drawRaw((x, y, z) -> {
            float brightness = Flags.bitSum(voxels.getVisibilityMask(x, y, z)) / 6F;
            return ColorMath.fromRGB(brightness, brightness, brightness);
        });
        
        VoxelArray content = canvas.getContent();
        logger.fine(voxels + " = " + content);
        assertEquals(voxels.size(), content.size());
        
        saveAs(content, "VoxelCanvasTest_drawVisibility", true);
    }
    
    /*
    @Test
    public void drawTriangle() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        VoxelCanvas canvas = new VoxelCanvas(128, 128, 128);
        Triangle3 triangle = Triangle3.fromPoints(
            0, 0, 0,
            120, 60, 8,
            8, 60, 120);
        
        long now = System.nanoTime();
        canvas.drawTriangle(triangle, ColorMath.SOLID_RED, ColorMath.SOLID_GREEN, ColorMath.SOLID_BLUE);
        double time = (System.nanoTime()-now) / CacheMath.BILLION;
        logger.fine("drawing triangle took "+time+" s");
        
        saveAsQEF(canvas.getContent(), "VoxelCanvasTest_Triangle");
    }
    */
    
    @Test
    public void drawLine() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        canvas.drawLine(0, 7, 15, 63, 63, 63, ColorMath.DEBUG1);
        
        saveAs(canvas.getContent(), "VoxelCanvasTest_drawLine", true);
    }
    
    @Test
    public void drawSphere() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(20, 20, 20);
        canvas.drawSphere(8, 8, 8, 8, ColorMath.DEBUG1);
        
        saveAs(canvas.getContent(), "draw_sphere", false);
    }
    
    //@Test
    public void drawSpherePerformance() {
        VoxelCanvas canvas = new VoxelCanvas(512, 512, 512);
        long now = System.currentTimeMillis();
        canvas.drawSphere(255, 255, 255, 255, ColorMath.DEBUG1);
        System.out.println(System.currentTimeMillis() - now);
    }

    /*
    @Test
    public void drawPath() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);

        Path3 ray = Path3.linear(
                Vector3.fromXYZ(0.5F, 0.5F, 0.5F),
                Vector3.fromXYZ(63.5F, 63.5F, 63.5F));
        canvas.drawPath(ray, 1F, ColorMath.DEBUG1);

        for (int i = 0; i<10; i++) {
            Path3 circle = Path3.circle(
                    Vector3.fromXYZ(32, 32, 32), //center
                    20, //radius
                    Vectors.random3(1)); //normal
            canvas.drawPath(circle, 128, ColorMath.fromHSB(PrimMath.randomFloat(1F), 0.5F, 0.75F));
        }

        saveAsQEF(canvas.getContent(), "VoxelCanvasTest_drawPath");
    }

    @Test
    public void drawSpace_OBB() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        OrientedBB box = OrientedBB.fromAABB(AxisAlignedBB.fromPoints(16, 16, 16, 48, 48, 48));
        box.rotateX(Spatium.radians(45));
        box.rotateZ(Math.atan(CacheMath.INV_SQRT_2));//

        canvas.drawSpace(box, ColorMath.DEBUG2);

        saveAsQEF(canvas.getContent(), "VoxelCanvasTest_drawSpace_OBB");
    }
    
    
    @Test
    public void drawSpace_Cone() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(128, 128, 128);
        //Cone aaCone = Cone.fromApexDirRadius(0, 0, 0, 16, 0, 0, 8);
        Cone orCone = Cone.fromApexDirRadius(0, 0, 0, 80, 80, 80, 40);
        
        //canvas.drawIf(aaCone::contains, ColorMath.DEBUG1);
        canvas.drawIf(orCone::contains, ColorMath.DEBUG2);
        saveAsQEF(canvas.getContent(), "VoxelCanvasTest_drawSpace_Cone");
    }
    
    
    @Test
    public void drawSpace_Sphere() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        Sphere sphere = Sphere.fromCenterRadius(31, 31, 31, 50);
        
        canvas.drawSpace(sphere, ColorMath.SOLID_RED);
        saveAsQEF(canvas.getContent(), "VoxelCanvasTest_drawSpace_Sphere");
    }
    */
    
    @Test
    public void contentSelect() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);
        assertEquals(voxels.size(), canvas.contentSize());
        
        canvas.selectContent(false);
        assertEquals(canvas.contentSize(), canvas.selectionSize());
    }
    
    @Test
    public void fullSelect() {
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
