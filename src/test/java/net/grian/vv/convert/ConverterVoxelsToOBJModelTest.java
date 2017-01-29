package net.grian.vv.convert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerOBJMaterials;
import net.grian.torrens.io.SerializerOBJModel;
import net.grian.torrens.io.SerializerPNG;
import net.grian.torrens.object.*;
import net.grian.vv.TestUtil;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ConverterVoxelsToOBJModelTest {

    @Test
    public void convertInternal() throws Exception {
        OBJModel model = new OBJModel();

        model.addNormal(new Vertex3f(0,  1, 0));
        model.addNormal(new Vertex3f(0, -1, 0));

        model.addVertex(new Vertex3f(0, 0, 0));
        model.addVertex(new Vertex3f(1, 0, 0));
        model.addVertex(new Vertex3f(0, 0, 1));

        model.addFace(new OBJFace(new OBJTriplet(1, 0, 1), new OBJTriplet(2, 0, 1), new OBJTriplet(3, 0, 1)));
        model.addFace(new OBJFace(new OBJTriplet(3, 0, 2), new OBJTriplet(2, 0, 2), new OBJTriplet(1, 0, 2)));

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelsToOBJModelTest.obj");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create: "+out);

        new SerializerOBJModel().toFile(model, out);
    }

    @Test
    public void convertDebugVoxels() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        assertNotNull(voxels);

        OBJModel model = ConvertUtil.convert(voxels, OBJModel.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterVoxelsToOBJModelTest.obj");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create: "+out);

        new SerializerOBJModel().toFile(model, out);
    
        OBJMaterialLibrary materials = model.getMaterials();
        assertNotNull(materials);
        File out2 = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\"+materials.getName()+".mtl");
        System.out.println(out2);
    
        if (!out2.exists() && !out2.createNewFile()) throw new IOException("failed to create: "+out2);
    
        new SerializerOBJMaterials().toFile(materials, out2);
        
        for (OBJMaterial material : materials) {
            String diffuse = material.getDiffuseMap();
            Texture map = materials.getMap(diffuse);
    
            File out3 = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\"+diffuse);
            if (!out3.exists() && !out3.createNewFile()) throw new IOException("failed to create: "+out3);
            
            new SerializerPNG().toFile(ConvertUtil.convert(map, BufferedImage.class), out3);
        }
    }

    @Test
    public void convertPerformance() throws Exception {
        VoxelArray voxels = new VoxelArray(256, 256, 256);
        voxels.fill(ColorMath.DEBUG1);

        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #1");
        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #2");
        TestUtil.printMillis(() -> ConvertUtil.convert(voxels, OBJModel.class), voxels+" to obj #3");
    }

}