package org.eisenwave.vv.bukkit.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.user.ConsoleVVUser;
import org.eisenwave.vv.bukkit.user.PlayerVVUser;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandUtil {
    
    /*
    private final static String[][] COLOR_ANSI_TABLE = {
        {BLACK.toString(), ANSI.BLACK},
        {DARK_BLUE.toString(), ANSI.BLUE},
        {DARK_GREEN.toString(), ANSI.GREEN},
        {DARK_AQUA.toString(), ANSI.CYAN},
        {DARK_RED.toString(), ANSI.RED},
        {DARK_PURPLE.toString(), ANSI.PURPLE},
        {GOLD.toString(), ANSI.YELLOW},
        {GRAY.toString(), ANSI.BLACK},
        {DARK_GRAY.toString(), ANSI.BLACK},
        {BLUE.toString(), ANSI.BLUE},
        {GREEN.toString(), ANSI.GREEN},
        {AQUA.toString(), ANSI.CYAN},
        {RED.toString(), ANSI.RED},
        {LIGHT_PURPLE.toString(), ANSI.PURPLE},
        {YELLOW.toString(), ANSI.YELLOW},
        {WHITE.toString(), ANSI.WHITE},
        {MAGIC.toString(), ""},
        {BOLD.toString(), ANSI.BOLD_ON},
        {STRIKETHROUGH.toString(), ANSI.STRIKETHROUGH_ON},
        {ITALIC.toString(), ""},
        {RESET.toString(), ANSI.RESET}
    };
    
    public static String chatColorToAnsi(String str) {
        for (String[] replace : COLOR_ANSI_TABLE) {
            str = str.replace(replace[0], replace[1]);
        }
        return str;
    }
    */
    
    private CommandUtil() {}
    
    
    @NotNull
    public static String chatColors(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    @NotNull
    public static VVUser userOf(BukkitVoxelVert vv, CommandSender sender) {
        if (sender instanceof Player)
            return new PlayerVVUser(vv, (Player) sender);
        else
            return new ConsoleVVUser(vv, sender);
    }
    
    @Nullable
    public static String extensionOf(String file) {
        int index = file.lastIndexOf('.');
        return index < 0? null : file.substring(index+1);
    }
    
}
