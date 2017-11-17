package org.eisenwave.vv.bukkit.inv;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;
import static org.bukkit.ChatColor.*;

public enum FileBrowserType {
    DIRECTORY(CHEST, BOLD),
    
    VARIABLE(ENDER_CHEST, BOLD, ITALIC),
    
    FILE(SILVER_SHULKER_BOX, RESET),
    
    BCT(GREEN_SHULKER_BOX, DARK_GREEN),
    
    IMAGE(YELLOW_SHULKER_BOX, YELLOW),
    
    MTL(BLUE_SHULKER_BOX, DARK_BLUE),
    
    QB(MAGENTA_SHULKER_BOX, LIGHT_PURPLE),
    
    QEF(PURPLE_SHULKER_BOX, DARK_PURPLE),
    
    SCHEMATIC(ORANGE_SHULKER_BOX, GOLD),
    
    STL(LIME_SHULKER_BOX, GREEN),
    
    WAVEFRONT(LIGHT_BLUE_SHULKER_BOX, AQUA),
    
    RESOURCE_PACK(CYAN_SHULKER_BOX, DARK_AQUA);
    
    private final Material material;
    private final String prefix;
    
    FileBrowserType(Material material, ChatColor... colors) {
        this.material = material;
        this.prefix = Arrays.stream(colors).map(ChatColor::toString).collect(Collectors.joining());
    }
    
    @Contract(pure = true)
    public Material getIcon() {
        return material;
    }
    
    @Contract(pure = true)
    public String getPrefix() {
        return prefix;
    }
    
    public static FileBrowserType fromPath(String path) {
        if (path.startsWith("#"))
            return VARIABLE;
        else if (path.endsWith("/"))
            return DIRECTORY;
        else {
            String ext = CommandUtil.extensionOf(path);
            if (ext == null) return FILE;
            else switch (ext.toLowerCase()) {
                case "colors": return BCT;
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
