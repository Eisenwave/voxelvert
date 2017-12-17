package eisenwave.vv.bukkit.cmd;

import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.ui.user.VVInventory;
import org.bukkit.command.CommandSender;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CmdMove extends VoxelVertCommand {
    
    public CmdMove(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert-move";
    }
    
    @Override
    public String getUsage() {
        return "<source> <target>";
    }
    
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
                user.errorLocalized("cmd.move.err.var");
                return true;
            }
        }
        
        VVInventory inventory = user.getInventory();
        
        if (!inventory.contains(source)) {
            user.errorLocalized("cmd.move.err.missing", source);
            return true;
        }
        try {
            if (inventory.move(source, target))
                user.printLocalized("cmd.move.success", source, target);
            else
                user.errorLocalized("cmd.move.failure", source, target);
            return true;
        } catch (IOException ex) {
            user.errorLocalized("cmd.move.exception", ex.getMessage());
        }
        return true;
    }
    
}
