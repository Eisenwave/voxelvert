package net.grian.vv.io;

import net.grian.spatium.geo.AxisAlignedBB;
import net.grian.vv.core.MCElement;
import net.grian.vv.core.Texture;
import net.grian.vv.core.MCModel;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class SerializerModelJSONTest {

    @Test
    public void serialize() throws Exception {
        MCModel model = new MCModel();

        model.addTexture("texture", new Texture(16, 16));
        model.addElement(new MCElement(AxisAlignedBB.fromPoints(0, 0, 0, 16, 16, 16)));

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVertPlugin\\files\\SerializerModelJSONTest.json");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerModelJSON().serialize(model, out);
    }

    /*
    @Test
    public void downUVNumber() throws Exception {
        float a = 0, b = 1.23456789F, c = -1.23456789F;
        Assert.assertEquals(    0F, SerializerModelJSON.downUVNumber(a));
        Assert.assertEquals( 1.23F, SerializerModelJSON.downUVNumber(b));
        Assert.assertEquals(-1.23F, SerializerModelJSON.downUVNumber(c));
    }
    */

}