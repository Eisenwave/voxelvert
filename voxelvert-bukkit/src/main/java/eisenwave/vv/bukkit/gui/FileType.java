package eisenwave.vv.bukkit.gui;

import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;
import static org.bukkit.ChatColor.*;

public enum FileType {
    DIRECTORY(null, "media.directory", CHEST, BOLD),
    
    VARIABLE(null, "media.inventory_variable", COMMAND, BOLD, ITALIC),
    
    FILE(null, "media.file", SILVER_SHULKER_BOX, RESET),
    
    BCT(Format.BLOCK_COLOR_TABLE, "media.voxelvert-bct", GREEN_SHULKER_BOX, DARK_GREEN),
    
    IMAGE(Format.IMAGE, "media.image", YELLOW_SHULKER_BOX, YELLOW),
    
    MTL(null, "media.wavefront-mtl", CYAN_SHULKER_BOX, DARK_AQUA),
    
    QB(Format.QB, "media.qubicle-binary", MAGENTA_SHULKER_BOX, DARK_PURPLE),
    
    QEF(Format.QEF, "media.qubicle-exchange", PURPLE_SHULKER_BOX, LIGHT_PURPLE),
    
    SCHEMATIC(Format.SCHEMATIC, "media.schematic", LIME_SHULKER_BOX, GREEN),
    
    STL(Format.STL, "media.stl", BLUE_SHULKER_BOX, BLUE),
    
    WAVEFRONT(Format.WAVEFRONT, "media.wavefront", LIGHT_BLUE_SHULKER_BOX, AQUA),
    
    RESOURCE_PACK(Format.RESOURCE_PACK, "media.minecraft-resource_pack", BROWN_SHULKER_BOX, GOLD);
    
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
    private final Material material;
    private final String prefix, prefixNoColors, langName;
    
    FileType(@Nullable Format format, String langName, Material material, ChatColor... colors) {
        this.format = format;
        this.langName = langName;
        this.material = material;
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
    public Material getIcon() {
        return material;
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
