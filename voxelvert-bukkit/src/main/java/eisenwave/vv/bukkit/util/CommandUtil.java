package eisenwave.vv.bukkit.util;

import eisenwave.vv.object.Language;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.*;

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
    
    private final static String[] UNITS = new String[] {"Bytes", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB"};
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
    public static String localizeTime(long age, Language lang) {
        if (age < 1000)
            return age + " " + lang.get(age == 1? "unit.time.milli" : "unit.time.millis");
        if ((age /= 1000) < 60)
            return age + " " + lang.get(age == 1? "unit.time.second" : "unit.time.seconds");
        if ((age /= 60) < 60)
            return age + " " + lang.get(age == 1? "unit.time.minute" : "unit.time.minutes");
        if ((age /= 60) < 24)
            return age + " " + lang.get(age == 1? "unit.time.hour" : "unit.time.hours");
        if ((age /= 24) < 365)
            return age + " " + lang.get(age == 1? "unit.time.day" : "unit.time.days");
        
        return (age /= 365) + " " + lang.get(age == 1? "unit.time.year" : "unit.time.years");
    }
    
    public static long parseFileSize(@NotNull String size) {
        /*
        0: first digit
        1: digits
        2: first unit
        3: units
         */
        int state = 0;
        StringBuilder numberBuilder = new StringBuilder();
        StringBuilder unitBuilder = new StringBuilder();
        
        loop:
        for (final char c : size.toCharArray()) {
            switch (state) {
                case 0: {
                    if (Character.isWhitespace(c))
                        continue loop;
                    else if (Character.isDigit(c) || c == '.') {
                        numberBuilder.append(c);
                        state = 1;
                    }
                    else
                        throw new IllegalArgumentException('"' + size + "\" error at '" + c + "'");
                    break;
                }
                case 1: {
                    if (Character.isWhitespace(c))
                        state = 2;
                    else if (Character.isDigit(c) || c == '.')
                        numberBuilder.append(c);
                    else {
                        unitBuilder.append(c);
                        state = 3;
                    }
                    break;
                }
                case 2: {
                    if (Character.isWhitespace(c)) continue loop;
                    else if (Character.isDigit(c) || c == '.')
                        throw new IllegalArgumentException('"' + size + "\" error at '" + c + "'");
                    else {
                        unitBuilder.append(c);
                        state = 3;
                    }
                    break;
                }
                case 3: {
                    if (Character.isWhitespace(c) || Character.isDigit(c) || c == '.')
                        throw new IllegalArgumentException('"' + size + "\" error at '" + c + "'");
                    else unitBuilder.append(c);
                    break;
                }
            }
        }
        
        double number = Double.parseDouble(numberBuilder.toString());
        long unit = bytesOfFileSizeUnit(unitBuilder.toString());
        
        return (long) (number * unit);
    }
    
    public static long bytesOfFileSizeUnit(String unit) {
        switch (unit.toLowerCase()) {
            case "b":
            case "byte":
            case "bytes": return 1;
            case "kb": return 1_000;
            case "mb": return 1_000_000;
            case "gb": return 1_000_000_000;
            case "tb": return 1_000_000_000_000L;
            case "pb": return 1_000_000_000_000_000L;
            case "eb": return 1_000_000_000_000_000_000L;
            case "kib": return 0x400;
            case "mib": return 0x100_000;
            case "gib": return 0x40_000_000;
            case "tib": return 0x10_000_000_000L;
            case "pib": return 0x4_000_000_000_000L;
            case "eib": return 0x1_000_000_000_000_000L;
            default: throw new IllegalArgumentException("unknown unit \"" + unit + "\"");
        }
        
        /*
        long result = 0;
        
        char[] chars = unit.toCharArray();
        
        int pow;
        switch (Character.toLowerCase(chars[0])) {
            case 'k': pow = 1;
            case 'm': pow = 2;
            case 'g': pow = 3;
            case 't': pow = 4;
            case 'p': pow = 5;
            case 'e': pow = 6;
            case 'z': pow = 7;
            case 'y': pow = 8;
        }
        */
    }
    
    @NotNull
    public static String chatColors(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    /* @NotNull
    public static VVUser userOf(BukkitVoxelVert vv, CommandSender sender) {
        if (sender instanceof Player)
            return new PlayerVVUser(vv, (Player) sender);
        else
            return new ConsoleVVUser(vv, sender);
    } */
    
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
