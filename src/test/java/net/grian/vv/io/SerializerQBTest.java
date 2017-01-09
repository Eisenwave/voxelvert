package net.grian.vv.io;

import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.Resources;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class SerializerQBTest {

    @Test
    public void serialize() throws Exception {
        InputStream inStream = Resources.getStream(getClass(), "sniper.qb");
        VoxelMesh mesh = new DeserializerQB().deserialize(inStream);
        System.out.println("serializing mesh: "+mesh);
        inStream.close();

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\SerializerQBTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().serialize(mesh, out);

        VoxelMesh mesh2 = new DeserializerQB().deserialize(out);
        inStream.close();
    }

}