package org.eisenwave.vv.bukkit.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        
        String[] ls = user.getInventory().list();
        for (int i = 0; i < ls.length-1; i++) {
            String file = ls[i];
            
            if (file.startsWith(".")) {
                //builder.append(ChatColor.DARK_GRAY);
                continue;
            }
            
            else if (file.endsWith("/"))
                builder.append(ChatColor.BOLD);
    
            else {
                String ext = CommandUtil.extensionOf(file.toLowerCase());
    
                if (ext != null) switch (ext) {
                    case "png":
                    case "jpg":
                    case "jpeg":
                    case "bmp": builder.append(ChatColor.YELLOW); break;
                    case "qef": builder.append(ChatColor.LIGHT_PURPLE); break;
                    case "qb": builder.append(ChatColor.DARK_PURPLE); break;
                    case "zip": builder.append(ChatColor.GOLD); break;
                    case "schem":
                    case "schematic": builder.append(ChatColor.GREEN); break;
                    case "stl": builder.append(ChatColor.BLUE); break;
                    case "obj": builder.append(ChatColor.AQUA); break;
                    case "mtl": builder.append(ChatColor.DARK_AQUA); break;
                }
            }
            
            builder
                .append(ls[i])
                .append(ChatColor.RESET)
                .append("\n");
        }
        builder.append(ls[ls.length-1]);
        
        user.print(builder.toString());
        return true;
    }
    
    
    
    
}
