package eisenwave.vv.bukkit.gui;

import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.ChatColor;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.*;

public enum FileType {
    DIRECTORY(null, "media.directory", "chest", BOLD),
    
    VARIABLE(null, "media.inventory_variable", "command_block", BOLD, ITALIC),
    
    FILE(null, "media.file", "light_gray_shulker_box", RESET),
    
    BCT(null, "media.voxelvert-bct", "green_shulker_box", DARK_GREEN),
    
    IMAGE(Format.IMAGE, "media.image", "yellow_shulker_box", YELLOW),
    
    MTL(null, "media.wavefront-mtl", "cyan_shulker_box", DARK_AQUA),
    
    QB(Format.QB, "media.qubicle-binary", "magenta_shulker_box", DARK_PURPLE),
    
    QEF(Format.QEF, "media.qubicle-exchange", "purple_shulker_box", LIGHT_PURPLE),
    
    SCHEMATIC(Format.SCHEMATIC, "media.schematic", "lime_shulker_box", GREEN),
    
    STL(Format.STL, "media.stl", "blue_shulker_box", BLUE),
    
    WAVEFRONT(Format.WAVEFRONT, "media.wavefront", "light_blue_shulker_box", AQUA),
    
    RESOURCE_PACK(Format.RESOURCE_PACK, "media.minecraft-resource_pack", "brown_shulker_box", GOLD);
    
    private final static Map<Format, FileType> formatMap = new HashMap<>();
    
    static {
        for (FileType val : values()) {
            Format f = val.getFormat();
            if (f != null)
                formatMap.put(f, val);
        }
    }
    
    @Nullable
    public static FileType fromFormat(@NotNull Format format) {
        return formatMap.get(format);
    }
    
    public static FileType fromPath(String path) {
        if (path.startsWith("#"))
            return VARIABLE;
        else if (path.endsWith("/"))
            return DIRECTORY;
        else {
            String ext = CommandUtil.extensionOf(path);
            if (ext == null) return FILE;
            else switch (ext.toLowerCase()) {
                case "bct": return BCT;
                case "png":
                case "jpg":
                case "jpeg":
                case "bmp": return IMAGE;
                case "qef": return QEF;
                case "qb": return QB;
                case "zip": return RESOURCE_PACK;
                case "schem":
                case "schematic": return SCHEMATIC;
                case "stl": return STL;
                case "mtl": return MTL;
                case "obj": return WAVEFRONT;
                default: return FILE;
            }
        }
    }
    
    private final Format format;
    private final String icon, prefix, prefixNoColors, langName;
    
    FileType(@Nullable Format format, String langName, String icon, ChatColor... colors) {
        this.format = format;
        this.langName = langName;
        this.icon = icon;
        this.prefix = Arrays.stream(colors)
            .map(ChatColor::toString)
            .collect(Collectors.joining());
        this.prefixNoColors = Arrays.stream(colors)
            .filter(ChatColor::isFormat)
            .map(ChatColor::toString)
            .collect(Collectors.joining());
    }
    
    public boolean isDirectory() {
        return this == DIRECTORY;
    }
    
    public boolean isVariable() {
        return this == VARIABLE;
    }
    
    public boolean isFile() {
        return ordinal() >= 2;
    }
    
    public boolean isFormat() {
        return format != null;
    }
    
    public String getLanguageName() {
        return langName;
    }
    
    @Nullable
    @Contract(pure = true)
    public Format getFormat() {
        return format;
    }
    
    @Contract(pure = true)
    public String getIcon() {
        return icon;
    }
    
    @Contract(pure = true)
    public String getPrefix() {
        return prefix;
    }
    
    @Contract(pure = true)
    public String getPrefixNoColors() {
        return prefixNoColors;
    }
    
}
