package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.user.BukkitVoxelVert;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class VoxelVertCommand implements CommandExecutor {
    
    protected final VoxelVertPlugin plugin;
    protected final BukkitVoxelVert voxelVert;
    private final String usage;
    
    public VoxelVertCommand(@NotNull VoxelVertPlugin plugin) {
        this.plugin = plugin;
        this.voxelVert = plugin.getVoxelVert();
        
        String translation = voxelVert.getLanguage().get("user.use", getName() + " " + getUsage(), getUsage());
        this.usage = CommandUtil.chatColors(translation);
    }
    
    public abstract String getName();
    
    public abstract String getUsage();
    
    public abstract boolean onCommand(CommandSender sender, VVUser user, String[] args);
    
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(usage);
        return onCommand(sender, CommandUtil.userOf(voxelVert, sender), args);
    }
    
    /**
     * Returns whether the sender has a given permission. If not, this method also notifies the given user about the
     * missing permission node in a localized message.
     *
     * @param sender the sender
     * @param user the user
     * @param permission the permission
     * @return whether the sender has the permission
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean requirePermission(CommandSender sender, VVUser user, String permission) {
        if (!sender.hasPermission(permission)) {
            user.errorLocalized("error.permission", permission);
            return false;
        }
        return true;
    }
    
}
