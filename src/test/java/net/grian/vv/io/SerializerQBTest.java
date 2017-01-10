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
        VoxelMesh mesh = new DeserializerQB().deserialize(getClass(), "sniper.qb");
        System.out.println("serializing mesh: "+mesh);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\SerializerQBTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().serialize(mesh, out);

        VoxelMesh mesh2 = new DeserializerQB().deserialize(out);
        assertNotNull(mesh2);
    }

}