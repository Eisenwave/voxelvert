package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CmdRemove extends VoxelVertCommand {
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /vv-rm <file>");
    
    public CmdRemove(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert-remove";
    }
    
    @Override
    public String getUsage() {
        return "<file>";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        if (args.length < 1) return false;
        
        String file = args[0];
        
        if (file.startsWith("/")) {
            user.errorLocalized("error.path_absolute");
            return true;
        }
        if (file.startsWith(".")) {
            user.errorLocalized("error.path_hidden");
            return true;
        }
        if (file.startsWith("#")) {
            user.errorLocalized("cmd.remove.err.var");
            return true;
        }
        
        VVInventory inventory = user.getInventory();
        
        if (!inventory.contains(file))
            user.errorLocalized("cmd.remove.err.missing", file);
        else if (inventory.delete(file))
            user.printLocalized("cmd.remove.success", file);
        else
            user.errorLocalized("cmd.remove.err", file);
        
        return true;
    }
    
}
