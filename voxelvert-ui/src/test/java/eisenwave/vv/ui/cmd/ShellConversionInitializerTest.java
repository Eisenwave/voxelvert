package eisenwave.vv.ui.cmd;

import org.junit.Test;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ShellConversionInitializerTest {
    
    @SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
    private final static Runnable INFINITE_RUNNABLE = () -> {while (true) ;};
    
    @Test
    public void async() throws Exception {
        final int timeout = 50;
        
        Logger logger = Logger.getLogger("vv.test");
        logger.setLevel(Level.FINE);
        
        long before = System.currentTimeMillis();
        
        try {
            ShellConversionInitializer.runWithTimeout(INFINITE_RUNNABLE, 1, timeout);
            throw new AssertionError();
        } catch (TimeoutException ex) {
            long millis = System.currentTimeMillis() - before;
            // make sure that the timeout was in 10 ms range of timeout
            assertTrue(Math.abs(millis - timeout) <= 10);
            System.err.println("timed out after " + millis + "ms");
        }
    }
    
    /*
    @Test
    public void commandTest() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        CmdConvert cmd = new CmdConvert();
        CommandSender sender = new DebugCommandSender();
        String[] args = {"qb", "qubicle/debug.qb", "voxels", "result"};
        
        cmd.execute(sender, null, "convert", args);
    
        VVUser user = UserManager.getInstance().getDebugUser();
        assertTrue(user.hasData("result"));
    
        VoxelArray data = (VoxelArray) user.getData("result");
        logger.info(data.toString());
    }
    */
    
}
