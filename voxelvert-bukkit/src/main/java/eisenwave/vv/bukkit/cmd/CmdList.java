package eisenwave.vv.bukkit.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.gui.FileBrowserEntry;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVInventoryVariable;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class CmdList extends VoxelVertCommand {
    
    public CmdList(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert-list";
    }
    
    @Override
    public String getUsage() {
        return "";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        StringBuilder builder = new StringBuilder("List of files and directories:\n");
        boolean first = true;
        
        VVInventory inventory = user.getInventory();
        
        List<FileBrowserEntry> entries = inventory.list().stream()
            .map(FileBrowserEntry::new)
            .filter(entry -> !entry.isHidden())
            .filter(entry -> {
                if (!entry.isVariable()) return true;
                VVInventoryVariable var = inventory.getVariable(entry.getName());
                return var != null && var.isSet();
            })
            .sorted()
            .collect(Collectors.toList());
    
        for (FileBrowserEntry entry : entries) {
            if (first) first = false;
            else builder.append("\n");
            builder
                .append(ChatColor.RESET)
                .append(entry.getDisplayName(true));
        }
        
        user.print(builder.toString());
        return true;
    }
    
    
}
