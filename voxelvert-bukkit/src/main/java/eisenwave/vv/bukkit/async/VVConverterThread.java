package eisenwave.vv.bukkit.async;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.ui.cmd.VoxelVertTask;
import eisenwave.vv.ui.error.FormatverterException;
import org.bukkit.ChatColor;

import java.util.logging.Logger;

public class VVConverterThread extends Thread {
    
    private final VoxelVertPlugin plugin;
    private final VoxelVertQueue queue;
    private final boolean verbose;
    
    public VVConverterThread(VoxelVertPlugin plugin, VoxelVertQueue queue) {
        this.setName("VoxelVert Converter");
        this.setDaemon(true);
        
        this.plugin = plugin;
        this.queue = queue;
        this.verbose = plugin.getVVConfig().hasVerbosityOnRuntime();
    }
    
    @Override
    public void run() {
        Logger logger = plugin.getLogger();
        
        while (true) {
            
            if (!queue.isEmpty()) {
                VoxelVertTask task = queue.poll();
                try {
                    if (verbose) logger.info("Now running queued task: " + task);
                    task.run();
                    if (verbose) logger.info("Finished running task: " + task);
                } catch (FormatverterException ex) {
                    task.getUser().error(ex.getMessage());
                } catch (Exception ex) {
                    String cls = ex.getClass().getSimpleName();
                    String msg = ex.getMessage();
                    task.getUser().error(ChatColor.RED + "%s: \"%s\"", cls, msg);
                    ex.printStackTrace();
                }
            }
            
            else try {
                if (verbose) logger.info("Queue is empty, converter thread is now sleeping.");
                synchronized (queue) {queue.wait();}
            } catch (InterruptedException e) {
                logger.warning("converter thread has been interrupted");
                break;
            }
            
        }
        
    }
    
}
