package net.grian.vv.io;

import net.grian.vv.core.STLModel;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.ConvUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class SerializerSTLTest {

    @Test
    public void serialize() throws Exception {
        VoxelArray voxels = new DeserializerQEF(Logger.getGlobal()).deserialize(getClass(), "sword.qef");
        STLModel model = ConvUtil.convert(voxels, STLModel.class);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\SerializerSTLTest.stl");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed out create "+out);

        new SerializerSTL().serialize(model, out);
    }

}