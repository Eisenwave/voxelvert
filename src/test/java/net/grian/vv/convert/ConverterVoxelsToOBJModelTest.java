package net.grian.vv.convert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerOBJModel;
import net.grian.torrens.object.OBJFace;
import net.grian.torrens.object.OBJModel;
import net.grian.torrens.object.OBJTriplet;
import net.grian.torrens.object.Vertex3f;
import net.grian.vv.TestUtil;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

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