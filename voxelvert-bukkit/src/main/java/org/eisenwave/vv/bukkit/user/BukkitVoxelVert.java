package org.eisenwave.vv.bukkit.user;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.async.BlockScanner;
import org.eisenwave.vv.bukkit.async.VoxelVertQueue;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.VoxelVert;
import org.eisenwave.vv.ui.cmd.VoxelVertTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BukkitVoxelVert implements VoxelVert {
    
    private final VoxelVertQueue queue;
    private final File dir;
    private final Language lang;
    private final WorldEditPlugin worldEdit;
    private final BlockScanner scanner;
    
    public BukkitVoxelVert(@NotNull VoxelVertPlugin plugin,
                           @NotNull WorldEditPlugin worldEdit,
                           @NotNull BlockScanner scanner) {
        this.queue = new VoxelVertQueue();
        this.dir = plugin.getDataFolder();
        this.lang = plugin.getLanguage();
        this.worldEdit = worldEdit;
        this.scanner = scanner;
    }
    
    public VoxelVertQueue getQueue() {
        return queue;
    }
    
    public Thread startConversionThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                
                if (!queue.isEmpty()) {
                    VoxelVertTask task = queue.poll();
                    try {
                        task.run();
                    } catch (Exception ex) {
                        String cls = ex.getClass().getSimpleName();
                        String msg = ex.getMessage();
                        task.getUser().error("Convert-Error: %s: \"%s\"", cls, msg);
                        ex.printStackTrace();
                    }
                }
                
                else try {
                    synchronized (queue) {queue.wait();}
                } catch (InterruptedException e) {
                    System.out.println("[VoxelVert] converter thread has been interrupted");
                    break;
                }
                
            }
        });
        
        thread.setName("VoxelVert Converter");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
    
    @NotNull
    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }
    
    @NotNull
    @Override
    public Language getLanguage() {
        return lang;
    }
    
    @Override
    public File getDirectory() {
        return dir;
    }
    
    @NotNull
    public BlockScanner getBlockScanner() {
        return scanner;
    }
    
}
