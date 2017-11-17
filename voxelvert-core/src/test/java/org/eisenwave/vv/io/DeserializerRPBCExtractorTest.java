package org.eisenwave.vv.io;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeserializerRPBCExtractorTest {

    @Test
    public void parseHex() throws Exception {
        final int value = DeserializerBCE.parseHex("0xFFFFFFFF");
        assertEquals(value >> 24 & 0xFF, 0xFF);
    }

}
