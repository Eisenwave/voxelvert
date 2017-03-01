package net.grian.vv.clsvert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.img.Texture;
import net.grian.torrens.qbcl.DeserializerQEF;
import net.grian.torrens.wavefront.SerializerMTL;
import net.grian.torrens.wavefront.SerializerOBJ;
import net.grian.torrens.img.SerializerPNG;
import net.grian.torrens.object.*;
import net.grian.torrens.wavefront.*;
import net.grian.vv.TestUtil;
import net.grian.vv.VVTest;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ClassverterVoxelsToOBJTest {

    @Test
    public void convertInternal() throws Exception {
        OBJModel model = new OBJModel();

        model.addNormal(new Vertex3f(0,  1, 0));
        model.addNormal(new Vertex3f(0, -1, 0));

        model.addVertex(new Vertex3f(0, 0, 0));
        model.addVertex(new Vertex3f(1, 0, 0));
        model.addVertex(new Vertex3f(0, 0, 1));

        model.getDefaultGroup().addFace(new OBJFace(new OBJTriplet(1, 0, 1), new OBJTriplet(2, 0, 1), new OBJTriplet(3, 0, 1)));
        model.getDefaultGroup().addFace(new OBJFace(new OBJTriplet(3, 0, 2), new OBJTriplet(2, 0, 2), new OBJTriplet(1, 0, 2)));

        File out = new File(VVTest.DIR_FILES, "debug.obj");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create: "+out);

        new SerializerOBJ().toFile(model, out);
    }
    
    @Test
    public void convertDebugVoxels() throws Exception {
        VoxelArray voxels = getMiniDebug();
        assertNotNull(voxels);

        OBJModel model = ConvertUtil.convert(voxels, OBJModel.class);

        File out = new File(VVTest.DIR_FILES, "ClassverterVoxelsToOBJTest.obj");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create: "+out);

        new SerializerOBJ().toFile(model, out);
    
        MTLLibrary materials = model.getMaterials();
        assertNotNull(materials);
        File out2 = new File(VVTest.DIR_FILES, materials.getName()+".mtl");
        System.out.println(out2);
    
        if (!out2.exists() && !out2.createNewFile()) throw new IOException("failed to create: "+out2);
    
        new SerializerMTL().toFile(materials, out2);
        
        for (MTLMaterial material : materials) {
            String diffuse = material.getDiffuseMap();
            Texture map = materials.getMap(diffuse);
    
            File out3 = new File(VVTest.DIR_FILES, diffuse);
            if (!out3.exists() && !out3.createNewFile()) throw new IOException("failed to create: "+out3);
            
            new SerializerPNG().toFile(ConvertUtil.convert(map, BufferedImage.class), out3);
        }
    }
    
    private static VoxelArray getVoxelizedSword() throws IOException {
        File in = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\voxelized_sword.qef");
        return new DeserializerQEF().fromFile(in);
    }
    
    private static VoxelArray getMiniDebug() throws IOException {
        return new DeserializerQEF().fromResource(ClassverterVoxelsToOBJTest.class, "minidebug.qef");
    }

    /*
    @Test
    public void convertPerformance() throws Exception {
        VoxelArray voxels = new VoxelArray(256, 256, 256);
        voxels.fill(ColorMath.DEBUG1);

        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #1");
        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #2");
        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #3");
    }
    */

}