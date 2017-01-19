package net.grian.vv.convert;

import net.grian.spatium.voxel.BlockArray;
import net.grian.vv.io.DeserializerSchematic;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeserializerSchematicTest {

    @Test
    public void deserialize() throws Exception {
        BlockArray array = new DeserializerSchematic().deserialize(getClass(), "bunny.schematic");
        System.out.println(array);
        assertNotNull(array);
    }

}