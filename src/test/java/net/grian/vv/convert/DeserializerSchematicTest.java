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
        BlockArray array = new DeserializerSchematic().deserialize(getClass(), "bunny.schematic");
        System.out.println(array);
        assertNotNull(array);
    }

}