package net.grian.spatium;

import net.grian.spatium.coll.Collisions;
import net.grian.spatium.coll.Rays;
import net.grian.spatium.geo3.*;
import net.grian.spatium.util.ColorMath;
import net.grian.torrens.img.SerializerPNG;
import net.grian.torrens.img.Texture;
import net.grian.torrens.img.TextureCanvas;
import net.grian.vv.VVTest;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class RaysTest {

    @Test
    public void draw_Ray_Sphere() throws Exception {
        Sphere sphere = Sphere.fromCenterAndRadius(0, 0, 2, 1);

        Texture texture = rayTrace(256, 256, 1.25F, 1.25F, ray -> {
            double[] entryExit = Rays.pierce(ray, sphere);
            if (entryExit == null) return ColorMath.SOLID_BLACK;

            double thickness = (entryExit[1] - entryExit[0]) / 2;
            assertTrue(thickness <= 1);

            //thick = red, thin = blue
            return ColorMath.fromHSB((float) (thickness / 3 + 0.66F), 0.5F, 0.75F);
        });

        print(texture, "Ray_Sphere");
    }

    @Test
    public void draw_Ray_Triangle() throws Exception {
        Triangle3 triangle = Triangle3.fromPoints(
                0, -1, -1,
                0,  0,  1,
                0,  1,  1);
        Vector3 origin = Vector3.fromXYZ(-2, 0, 0);

        Texture texture = new Texture(256, 256);
        TextureCanvas graphics = texture.getGraphics();
        graphics.drawRaw((x,y) -> {
            Vector3 target = Vector3.fromXYZ(0, (y-128) / 100D, (x-128) / 100D );
            Ray3 ray = Ray3.between(origin, target);
            ray.normalize();

            double t = Rays.cast(ray, triangle);
            return Double.isFinite(t) ? ColorMath.SOLID_WHITE : ColorMath.SOLID_BLACK;
        });

        print(texture, "Ray_Triangle");
    }

    @Test
    public void draw_Ray_AABB() throws Exception {
        AxisAlignedBB3 aabb = AxisAlignedBB3.fromPoints(-1, -1, 1, 1, 1, 3);

        Texture texture = rayTrace(256, 256, 1.25F, 1.25F, ray -> {
            double t = Rays.cast(ray, aabb);
            return Double.isFinite(t)? ColorMath.SOLID_WHITE : ColorMath.SOLID_BLACK;
        });

        print(texture, "Ray_AABB");
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Test
    public void draw_Ray_OBB() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        OrientedBB3 box = OrientedBB3.fromAABB(AxisAlignedBB3.fromPoints(-1, -1, 1, 1, 1, 3));
        box.rotateY(Spatium.radians(45));
        Slab3 slabX = box.getSlabX(), slabY = box.getSlabY(), slabZ = box.getSlabZ();
        logger.fine(slabX.toString());
        logger.fine(slabY.toString());
        logger.fine(slabZ.toString());
        assertEquals(slabX.getThickness(), 2, Spatium.EPSILON);
        assertEquals(slabY.getThickness(), 2, Spatium.EPSILON);
        assertEquals(slabZ.getThickness(), 2, Spatium.EPSILON);

        Ray3 testRay = Ray3.fromOD(0, 0, 0, 0, 0, 1);
        assertTrue( Collisions.test(testRay, slabX) );
        assertTrue( Collisions.test(testRay, slabY) );
        assertTrue( Collisions.test(testRay, slabZ) );

        Texture texture = rayTrace(256, 256, 2F, 2F, ray -> {
            double tx = Rays.cast(ray, slabX);
            double ty = Rays.cast(ray, slabY);
            double tz = Rays.cast(ray, slabZ);

            switch (minIndex(tx, ty, tz)) {
                case 0: return ColorMath.SOLID_RED;
                case 1: return ColorMath.SOLID_GREEN;
                case 2: return ColorMath.SOLID_BLUE;
                default: return ColorMath.SOLID_BLACK;
            }
        });

        print(texture, "Ray_OBB");
    }

    private static int minIndex(double... nums) {
        double min = Double.POSITIVE_INFINITY;
        int index = -1;

        for (int i = 0; i<nums.length; i++) {
            if (Double.isFinite(nums[i]) && nums[i] < min) {
                min = nums[i];
                index = i;
            }
        }

        return index;
    }

    /**
     * Applies a function to a texture canvas so that an image with given width and height is being filled.
     * The ray-tracer has following properties:
     * <ul>
     *     <li>The z axis is the depth axis, with the camera always located at (0, 0, 0).</li>
     *     <li>The direction of the camera is (1, 0, 0).</li>
     *     <li>The space being rendered is slightly bigger than a 1x1 square in front of the camera.</li>
     *     <li>Renders are orthogonal, there is no perspective. The ray direction is constant, only the origin changes.</li>
     * </ul>
     *
     * @param width the image width
     * @param height the image height
     * @param function the function that converts rays to pixel colors
     * @return the rendered image
     */
    private static Texture rayTrace(int width, int height, float worldW, float worldH, RayIntFunction function) {
        Vector3 dir = Vector3.fromXYZ(0, 0, 1);
        final int transU = -width / 2, transV = -height / 2;
        final float scaleU = worldW / transU, scaleV = worldH / transV;

        Texture texture = new Texture(256, 256);
        TextureCanvas graphics = texture.getGraphics();

        graphics.drawRaw((x,y) -> {
            double worldX = (x+transU) * scaleU, worldY = (y+transV) * scaleV;

            Vector3 origin = Vector3.fromXYZ(worldX, worldY, -2);
            Ray3 ray = Ray3.fromOD(origin, dir);

            return function.apply(ray);
        });

        return texture;
    }

    @FunctionalInterface
    private static interface RayIntFunction {
        int apply(Ray3 ray);
    }

    private static void print(Texture texture, String name) throws IOException {
        BufferedImage image = ConvertUtil.convert(texture, BufferedImage.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\"+name+".png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        new SerializerPNG().toFile(image, out);
    }

}
