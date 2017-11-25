package eisenwave.vv.bukkit.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandUtilTest {
    
    @Test
    public void printFileSize() throws Exception {
        assertEquals("100 Bytes", CommandUtil.printFileSize(100));
        assertEquals("1 KB", CommandUtil.printFileSize(1024));
        assertEquals("1 MB", CommandUtil.printFileSize(1024 * 1024));
        assertEquals("1 GB", CommandUtil.printFileSize(1024 * 1024 * 1024));
        assertEquals("5 MB", CommandUtil.printFileSize(1024 * 1024 * 5));
    }
    
}
