package org.eisenwave.vv.bukkit.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.gui.FileBrowserEntry;
import org.eisenwave.vv.bukkit.gui.FileType;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryVariable;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CmdList implements CommandExecutor {
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /vv-ls");
    
    private final VoxelVertPlugin plugin;
    
    public CmdList(@NotNull VoxelVertPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
        BukkitVoxelVert vv = plugin.getVoxelVert();
        VVUser user = CommandUtil.userOf(vv, sender);
        
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
            .collect(Collectors.toList());
    
        entries.sort(null);
    
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
