package eisenwave.vv.bukkit.util;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class HttpUtilTest {
    
    @Test
    public void longToBase64() throws Exception {
        for (int i = 0; i < 100; i++) {
            long random = ThreadLocalRandom.current().nextLong();
            System.out.println(HttpUtil.longToBase64(random));
        }
    }
    
}
