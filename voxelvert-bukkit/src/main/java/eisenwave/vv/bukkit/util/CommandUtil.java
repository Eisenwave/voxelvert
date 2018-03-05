package eisenwave.vv.bukkit.util;

import eisenwave.vv.bukkit.user.BukkitVoxelVert;
import eisenwave.vv.bukkit.user.ConsoleVVUser;
import eisenwave.vv.bukkit.user.PlayerVVUser;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

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
    
    private final static String[] UNITS = new String[] {"Bytes", "KiB", "MiB", "GiB", "TiB"};
    private final static DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("#,##0.#");
    
    @NotNull
    public static String printFileSize(long size) {
        if (size <= 0) return "0";
        long s = size;
        int u = 0;
        
        while (s >= 1024 && u <= UNITS.length) {
            s >>= 10;
            u++;
        }
        
        double printSize = size / Math.pow(1024, u);
        return FILE_SIZE_FORMAT.format(printSize) + " " + UNITS[u];
    }
    
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
        return index < 0? null : file.substring(index + 1);
    }
    
    @NotNull
    public static String[] nameAndExtensionOf(String file) {
        int index = file.lastIndexOf('.');
        return index < 0?
            new String[] {file} :
            new String[] {file.substring(0, index), file.substring(index + 1)};
    }
    
}
