package net.grian.vv.cmd;

import net.grian.vv.VVTest;
import org.junit.Test;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class CmdConvertTest {
    
    @SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
    private final static Runnable INFINITE_RUNNABLE = () -> {while (true);};
    
    @Test
    public void async() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        long before = System.currentTimeMillis();
        
        try {
            CmdConvert.runWithMaxTime(INFINITE_RUNNABLE, 3000);
            throw new AssertionError();
        } catch (TimeoutException ex) {
            long time = System.currentTimeMillis() - before;
            assertTrue(time - 3000 < 100);
            logger.fine("timed out after "+time+"ms");
        }
    }
    
}