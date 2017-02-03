package net.grian.vv.cmd;

import net.grian.spatium.voxel.VoxelArray;
import net.grian.vv.VVTest;
import net.grian.vv.plugin.UserManager;
import net.grian.vv.plugin.VVPlugin;
import net.grian.vv.plugin.VVUser;
import org.bukkit.command.CommandSender;
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
    
    @Test
    public void commandTest() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
    
        VVPlugin.initLanguage();
        
        CmdConvert cmd = new CmdConvert();
        CommandSender sender = new DebugCommandSender();
        String[] args = {"qb", "qubicle/debug.qb", "voxels", "result"};
        
        cmd.onCommand(sender, null, "convert", args);
    
        VVUser user = UserManager.getInstance().getDebugUser();
        assertTrue(user.hasData("result"));
    
        VoxelArray data = user.getData("result");
        logger.info(data.toString());
    }
    
}