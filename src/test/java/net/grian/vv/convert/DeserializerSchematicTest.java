package net.grian.vv.convert;

import net.grian.spatium.voxel.BlockArray;
import net.grian.torrens.io.DeserializerSchematic;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class DeserializerSchematicTest {

    @Test
    public void deserialize() throws Exception {
        BlockArray array = new DeserializerSchematic().fromResource(getClass(), "bunny.schematic");
        System.out.println(array);
        assertNotNull(array);
    }

}