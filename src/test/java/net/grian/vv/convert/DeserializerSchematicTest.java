package net.grian.vv.convert;

import net.grian.vv.core.BlockArray;
import net.grian.vv.io.DeserializerSchematic;
import net.grian.vv.util.Resources;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class DeserializerSchematicTest {

    @Test
    public void deserialize() throws Exception {
        InputStream stream = Resources.getStream(getClass(), "bunny.schematic");
        assertNotNull(stream);

        BlockArray array = new DeserializerSchematic().deserialize(stream);
        stream.close();
        System.out.println(array);
    }

}