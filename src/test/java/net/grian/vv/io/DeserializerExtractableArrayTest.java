package net.grian.vv.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeserializerExtractableArrayTest {

    @Test
    public void parseHex() throws Exception {
        final int value = DeserializerExtractableArray.parseHex("0xFFFFFFFF");
        assertEquals(value >> 24 & 0xFF, 0xFF);
    }

}