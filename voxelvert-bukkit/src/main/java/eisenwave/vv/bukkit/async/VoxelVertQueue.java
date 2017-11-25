package eisenwave.vv.bukkit.async;

import eisenwave.vv.ui.cmd.VoxelVertTask;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoxelVertQueue extends ConcurrentLinkedQueue<VoxelVertTask> {
    
    @Override
    public synchronized boolean add(VoxelVertTask task) {
        boolean result = super.add(Objects.requireNonNull(task));
        if (result)
            this.notifyAll();
        
        return result;
    }
    
}
