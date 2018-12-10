package eisenwave.vv.bukkit.user;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.async.BlockScanner;
import eisenwave.vv.object.Language;
import eisenwave.vv.bukkit.async.VVConverterThread;
import eisenwave.vv.bukkit.async.VoxelVertQueue;
import eisenwave.vv.ui.VoxelVert;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitVoxelVert implements VoxelVert {
    
    private final VoxelVertPlugin plugin;
    private final VoxelVertQueue queue;
    private final WorldEditPlugin worldEdit;
    private final BlockScanner scanner;
    
    private final Map<UUID, VVUser> users = new HashMap<>();
    
    public BukkitVoxelVert(@NotNull VoxelVertPlugin plugin,
                           @Nullable WorldEditPlugin worldEdit,
                           @NotNull BlockScanner scanner) {
        this.plugin = plugin;
        this.queue = new VoxelVertQueue();
        this.worldEdit = worldEdit;
        this.scanner = scanner;
    }
    
    // ACTIONS
    
    public VVConverterThread startConversionThread() {
        VVConverterThread thread = new VVConverterThread(plugin, queue);
        thread.start();
        return thread;
    }
    
    // GETTERS
    
    /**
     * Returns the {@link VVUser user} of the given {@link CommandSender}.
     *
     * @param sender the command sender
     * @return the corresponding new or existing user
     */
    @NotNull
    public VVUser getUser(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            VVUser result = users.get(player.getUniqueId());
            if (result == null) {
                result = new PlayerVVUser(this, player);
                users.put(player.getUniqueId(), result);
            }
            
            return result;
        }
        else return new ConsoleVVUser(this, sender);
    }
    
    public boolean isWorldEditAvailable() {
        return worldEdit != null;
    }
    
    @NotNull
    public VoxelVertQueue getQueue() {
        return queue;
    }
    
    @Nullable
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
