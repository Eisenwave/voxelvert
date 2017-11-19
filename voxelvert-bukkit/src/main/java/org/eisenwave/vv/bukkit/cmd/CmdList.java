package org.eisenwave.vv.bukkit.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.gui.FileType;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        
        for (String file : user.getInventory().list()) {
            if (file.startsWith(".")) {
                //builder.append(ChatColor.DARK_GRAY);
                continue;
            }
            if (first) first = false;
            else builder.append('\n');
            
            FileType type = FileType.fromPath(file);
            if (type != null) builder.append(type.getPrefix());
            
            builder.append(file).append(ChatColor.RESET);
        }
        
        user.print(builder.toString());
        return true;
    }
    
    
    
    
}
