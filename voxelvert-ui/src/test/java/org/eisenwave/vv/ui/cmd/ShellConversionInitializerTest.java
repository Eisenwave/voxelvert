package org.eisenwave.vv.ui.cmd;

import org.junit.Test;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ShellConversionInitializerTest {
    
    @SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
    private final static Runnable INFINITE_RUNNABLE = () -> {while (true);};
    
    @Test
    public void async() throws Exception {
        Logger logger = Logger.getLogger("vv.test");
        logger.setLevel(Level.FINE);
        
        long before = System.currentTimeMillis();
        
        try {
            ShellConversionInitializer.runWithTimeout(INFINITE_RUNNABLE, 1, 3000);
            throw new AssertionError();
        } catch (TimeoutException ex) {
            long time = System.currentTimeMillis() - before;
            assertTrue(time - 3000 < 100);
            System.out.println("timed out after "+time+"ms");
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