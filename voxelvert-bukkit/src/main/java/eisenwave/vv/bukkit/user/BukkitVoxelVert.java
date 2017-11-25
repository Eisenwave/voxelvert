package eisenwave.vv.bukkit.user;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.async.BlockScanner;
import eisenwave.vv.object.Language;
import eisenwave.vv.bukkit.async.VVConverterThread;
import eisenwave.vv.bukkit.async.VoxelVertQueue;
import eisenwave.vv.ui.VoxelVert;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BukkitVoxelVert implements VoxelVert {
    
    private final VoxelVertPlugin plugin;
    private final VoxelVertQueue queue;
    private final WorldEditPlugin worldEdit;
    private final BlockScanner scanner;
    
    public BukkitVoxelVert(@NotNull VoxelVertPlugin plugin,
                           @NotNull WorldEditPlugin worldEdit,
                           @NotNull BlockScanner scanner) {
        this.plugin = plugin;
        this.queue = new VoxelVertQueue();
        this.worldEdit = worldEdit;
        this.scanner = scanner;
    }
    
    // ACTIONS
    
    public Thread startConversionThread() {
        Thread thread = new VVConverterThread(plugin, queue);
        thread.start();
        return thread;
    }
    
    // GETTERS
    
    public VoxelVertQueue getQueue() {
        return queue;
    }
    
    @NotNull
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }
    
    @NotNull
    @Override
    public Language getLanguage() {
        return plugin.getLanguage();
    }
    
    @Override
    public File getDirectory() {
        return plugin.getDataFolder();
    }
    
    @NotNull
    public BlockScanner getBlockScanner() {
        return scanner;
    }
    
}
