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
        QBModel model = new DeserializerQB().deserialize(getClass(), "sniper.qb");
        System.out.println("serializing model: "+model);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\files\\SerializerQBTest.qb");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQB().serialize(model, out);

        QBModel model2 = new DeserializerQB().deserialize(out);
        Assert.assertNotNull(model2);
    }

}