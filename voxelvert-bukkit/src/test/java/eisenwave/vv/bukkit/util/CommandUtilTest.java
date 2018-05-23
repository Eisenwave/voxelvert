package eisenwave.vv.bukkit.util;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class CommandUtilTest {
    
    @Test
    public void printFileSize() throws Exception {
        assertEquals("100 Bytes", CommandUtil.printFileSize(100));
        assertEquals("1 KiB", CommandUtil.printFileSize(1024));
        assertEquals("1 MiB", CommandUtil.printFileSize(1024 * 1024));
        assertEquals("1 GiB", CommandUtil.printFileSize(1024 * 1024 * 1024));
        assertEquals("5 MiB", CommandUtil.printFileSize(1024 * 1024 * 5));
        assertEquals("9 TiB", CommandUtil.printFileSize(1024L * 1024 * 1024 * 1024 * 9));
        assertEquals("7.3 PiB", CommandUtil.printFileSize((long) (1024L * 1024 * 1024 * 1024 * 1024 * 7.3D)));
    }
    
    @Test
    public void parseFileSize() throws Exception {
        assertEquals(100, CommandUtil.parseFileSize("100 B"));
        assertEquals(100, CommandUtil.parseFileSize("100 Bytes"));
        assertEquals(1024, CommandUtil.parseFileSize("1 KiB"));
        assertEquals(1024 * 1024, CommandUtil.parseFileSize("1 MiB"));
        assertEquals(1024 * 1024 * 1024, CommandUtil.parseFileSize("1 GiB"));
        assertEquals(1024L * 1024 * 1024 * 1024 * 9, CommandUtil.parseFileSize("9 TiB"));
        assertEquals((long) (1024L * 1024 * 1024 * 1024 * 1024 * 7.3D), CommandUtil.parseFileSize("7.3 PiB"));
    }
    
    /*
    @Test
    public void printAndParse() throws Exception {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 1000; i++) {
            long next = random.nextLong(0, Long.MAX_VALUE);
            System.out.println(next + " -> " + CommandUtil.printFileSize(next));
            assertEquals(next, CommandUtil.parseFileSize(CommandUtil.printFileSize(next)));
        }
    }
    */
    
}
