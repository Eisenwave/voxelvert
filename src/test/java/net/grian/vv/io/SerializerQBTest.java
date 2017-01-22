package net.grian.vv.io;

import net.grian.torrens.io.DeserializerQB;
import net.grian.torrens.io.SerializerQB;
import net.grian.torrens.object.QBModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class SerializerQBTest {

    @Test
    public void serialize() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        System.out.println("serializing model: "+model);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\files\\SerializerQBTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().toFile(model, out);

        QBModel model2 = new DeserializerQB().fromFile(out);
        Assert.assertNotNull(model2);
    }

}