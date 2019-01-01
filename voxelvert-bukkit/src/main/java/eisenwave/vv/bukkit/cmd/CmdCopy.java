package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import org.bukkit.command.CommandSender;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CmdCopy extends VoxelVertCommand {
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /vv-cp <source> <target>");
    
    public CmdCopy(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert-copy";
    }
    
    @Override
    public String getUsage() {
        return "<source> <target>";
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        if (args.length < 2) return false;
        
        String source = args[0], target = args[1];
        
        for (String arg : args) {
            if (arg.startsWith("/")) {
                user.errorLocalized("error.path_absolute");
                return true;
            }
            if (arg.startsWith(".")) {
                user.errorLocalized("error.path_hidden");
                return true;
            }
            if (arg.startsWith("#")) {
                user.errorLocalized("cmd.copy.err.var");
                return true;
            }
        }
        
        VVInventory inventory = user.getInventory();
        
        if (!inventory.contains(source)) {
            user.errorLocalized("cmd.copy.err.missing", source);
            return true;
        }
        try {
            if (inventory.copy(source, target))
                user.printLocalized("cmd.copy.success", source, target);
            else
                user.errorLocalized("cmd.copy.failure", source, target);
            return true;
        } catch (IOException ex) {
            user.errorLocalized("cmd.copy.exception", ex.getMessage());
        }
        return true;
    }
    
}
