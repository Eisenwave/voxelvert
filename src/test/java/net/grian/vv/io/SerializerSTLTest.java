package net.grian.vv.io;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerSTL;
import net.grian.torrens.object.STLModel;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SerializerSTLTest {

    @Test
    public void serialize() throws Exception {
        VoxelArray voxels = new DeserializerQEF(Logger.getGlobal()).fromResource(getClass(), "sword.qef");
        STLModel model = ConvertUtil.convert(voxels, STLModel.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\files\\SerializerSTLTest.stl");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed out create "+out);

        new SerializerSTL().toFile(model, out);
    }

}