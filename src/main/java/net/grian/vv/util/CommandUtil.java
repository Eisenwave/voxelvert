package net.grian.vv.util;

import net.grian.torrens.util.ANSI;

import static org.bukkit.ChatColor.*;

public final class CommandUtil {
    
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
    
    private CommandUtil() {}
    
    public static String chatColorToAnsi(String str) {
        for (String[] replace : COLOR_ANSI_TABLE) {
            str = str.replace(replace[0], replace[1]);
        }
        return str;
    }
    
}
