package org.eisenwave.vv.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class CmdCopy implements CommandExecutor {
    
    private final static String
        USAGE = CommandUtil.chatColors("&cUsage: /vv-cp <source> <target>");
    
    private final VoxelVertPlugin plugin;
    
    public CmdCopy(@NotNull VoxelVertPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
        Language lang = plugin.getLanguage();
        BukkitVoxelVert vv = plugin.getVoxelVert();
        VVUser user = CommandUtil.userOf(vv, sender);
        
        if (args.length < 2) return false;
        
        String source = args[0], target = args[1];
        
        for (String arg : args) {
            if (arg.startsWith("/")) {
                user.error(lang.get("err.path_absolute"));
                return true;
            }
            if (arg.startsWith(".")) {
                user.error(lang.get("err.path_hidden"));
                return true;
            }
            if (arg.startsWith("#")) {
                user.error(lang.get("cmd.copy.err.var"));
                return true;
            }
        }
        
        VVInventory inventory = user.getInventory();
        
        if (!inventory.contains(null, source)) {
            user.error(lang.get("cmd.copy.err.missing"), source);
            return true;
        }
        try {
            if (inventory.copy(source, target))
                user.print(lang.get("cmd.copy.success"), source, target);
            else
                user.error(lang.get("cmd.copy.failure"), source, target);
            return true;
        } catch (IOException ex) {
            user.error(lang.get("cmd.copy.exception"), ex.getMessage());
        }
        return true;
    }
    
}