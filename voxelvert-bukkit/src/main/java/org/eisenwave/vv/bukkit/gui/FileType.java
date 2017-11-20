package org.eisenwave.vv.bukkit.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;
import static org.bukkit.ChatColor.*;

public enum FileType {
    DIRECTORY("media.directory", CHEST, BOLD),
    
    VARIABLE("media.inventory_variable", COMMAND, BOLD, ITALIC),
    
    FILE("media.file", SILVER_SHULKER_BOX, RESET),
    
    BCT("media.voxelvert-bct", GREEN_SHULKER_BOX, DARK_GREEN),
    
    IMAGE("media.image", YELLOW_SHULKER_BOX, YELLOW),
    
    MTL("media.wavefront-mtl", BLUE_SHULKER_BOX, DARK_BLUE),
    
    QB("media.qubicle-binary", MAGENTA_SHULKER_BOX, LIGHT_PURPLE),
    
    QEF("media.qubicle-exchange", PURPLE_SHULKER_BOX, DARK_PURPLE),
    
    SCHEMATIC("media.schematic", ORANGE_SHULKER_BOX, GOLD),
    
    STL("media.stl", LIME_SHULKER_BOX, GREEN),
    
    WAVEFRONT("media.wavefront", LIGHT_BLUE_SHULKER_BOX, AQUA),
    
    RESOURCE_PACK("media.minecraft-resource_pack", CYAN_SHULKER_BOX, DARK_AQUA);
    
    private final Material material;
    private final String prefix, prefixNoColors, langName;
    
    FileType(String langName, Material material, ChatColor... colors) {
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
    
    public String getLanguageName() {
        return langName;
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
    
}
