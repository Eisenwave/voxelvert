package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.user.BukkitVoxelVert;
import eisenwave.vv.object.Language;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CmdRemove implements CommandExecutor {
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /vv-rm <file>");
    
    private final VoxelVertPlugin plugin;
    
    public CmdRemove(@NotNull VoxelVertPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
        Language lang = plugin.getLanguage();
        BukkitVoxelVert vv = plugin.getVoxelVert();
        VVUser user = CommandUtil.userOf(vv, sender);
        
        if (args.length < 1) return false;
        
        String file = args[0];
    
        if (file.startsWith("/")) {
            user.error(lang.get("err.path_absolute"));
            return true;
        }
        if (file.startsWith(".")) {
            user.error(lang.get("err.path_hidden"));
            return true;
        }
        if (file.startsWith("#")) {
            user.error(lang.get("cmd.remove.err.var"));
            return true;
        }
        
        VVInventory inventory = user.getInventory();
    
        if (!inventory.contains(file))
            user.error(lang.get("cmd.remove.err.missing"), file);
        else if (inventory.delete(file))
            user.print(lang.get("cmd.remove.success"), file);
        else
            user.error(lang.get("cmd.remove.err"), file);
        
        return true;
    }
    
}
