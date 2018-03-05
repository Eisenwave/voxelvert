package eisenwave.vv.bukkit.util;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class DynamicByteBufferTest {
    
    @Test
    public void get() {
    
    }
    
    @Test
    public void put() {
        DynamicByteBuffer buffer = new DynamicByteBuffer(4);
        byte[] bytes = new byte[1024];
        ThreadLocalRandom.current().nextBytes(bytes);
        for (byte b : bytes) {
            buffer.put(b);
        }
        buffer.setPosition(0);
        for (byte b : bytes) {
            assertEquals(b, buffer.get());
        }
        assertEquals(bytes, buffer.getContent());
    }
    
}
