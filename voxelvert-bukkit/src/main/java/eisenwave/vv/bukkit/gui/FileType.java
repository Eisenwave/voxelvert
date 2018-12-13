package eisenwave.vv.bukkit.gui;

import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.ChatColor;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static eisenwave.vv.bukkit.gui.MediaCategory.*;
import static org.bukkit.ChatColor.*;

public enum FileType {
    DIRECTORY(MediaCategory.DIRECTORY, null, "application/x-directory", "chest", BOLD),
    
    VARIABLE(TEMPORARY, null, "application/x.voxelvert-inventory_variable", "command_block", BOLD, ITALIC),
    
    FILE(BINARY, null, "application/x-binary", "light_gray_shulker_box", RESET),
    
    PLAINTEXT(TEXT, null, "text/plain", "light_gray_shulker_box", RESET),
    
    BCT(BINARY, null, "application/x.voxelvert-bct", "bookshelf", DARK_GREEN),
    
    // orange for lossless images, yellow for lossy
    BMP(BINARY, Format.IMAGE, "image/bmp", "orange_shulker_box", GOLD),
    GIF(BINARY, Format.IMAGE, "image/gif", "yellow_shulker_box", YELLOW),
    JPEG(BINARY, Format.IMAGE, "image/jpeg", "yellow_shulker_box", YELLOW),
    PNG(BINARY, Format.IMAGE, "image/png", "orange_shulker_box", GOLD),
    
    MTL(TEXT, null, "text/x.wavefront-mtl", "cyan_shulker_box", DARK_AQUA),
    
    QB(BINARY, Format.QB, "application/x.qubicle-binary", "magenta_shulker_box", DARK_PURPLE),
    
    QEF(TEXT, Format.QEF, "text/x.qubicle-exchange", "purple_shulker_box", LIGHT_PURPLE),
    
    SCHEMATIC(NBT, Format.SCHEMATIC, "application/x.minecraft-schematic", "lime_shulker_box", GREEN),
    
    STL(BINARY, Format.STL, "model/stl", "blue_shulker_box", BLUE),
    
    STRUCTURE(NBT, Format.STRUCTURE, "application/x.minecraft-structure", "structure_block", RED),
    
    WAVEFRONT(TEXT, Format.WAVEFRONT, "model/x.wavefront", "light_blue_shulker_box", AQUA),
    
    RESOURCE_PACK(COMPRESSED, Format.RESOURCE_PACK, "application/x.minecraft-resource_pack", "brown_shulker_box", GOLD);
    
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
    
                case "bmp": return BMP;
                case "gif": return GIF;
                case "jpg":
                case "jpeg": return JPEG;
                case "png": return PNG;
                
                case "mtl": return MTL;
                
                case "nbt": return STRUCTURE;
    
                case "obj": return WAVEFRONT;
                
                case "qef": return QEF;
                
                case "qb": return QB;
                
                case "schem":
                case "schematic": return SCHEMATIC;
                
                case "stl": return STL;
                
                case "txt": return PLAINTEXT;
    
                case "zip": return RESOURCE_PACK;
                
                default: return FILE;
            }
        }
    }
    
    private final MediaCategory category;
    private final Format format;
    private final String icon, prefix, prefixNoColors, langName;
    
    FileType(MediaCategory category, @Nullable Format format, String mediaType, String icon, ChatColor... colors) {
        this.category = category;
        this.format = format;
        this.langName = "media." + mediaType;
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
    
    public MediaCategory getCategory() {
        return category;
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
