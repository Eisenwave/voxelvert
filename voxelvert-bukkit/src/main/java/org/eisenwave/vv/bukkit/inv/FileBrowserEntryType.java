package org.eisenwave.vv.bukkit.inv;

import org.bukkit.Material;
import org.jetbrains.annotations.Contract;

public enum FileBrowserEntryType {
    DIRECTORY(Material.CHEST),
    
    VARIABLE(Material.ENDER_CHEST),
    
    FILE(Material.SILVER_SHULKER_BOX),
    
    COLORS(Material.GREEN_SHULKER_BOX),
    
    IMAGE(Material.YELLOW_SHULKER_BOX),
    
    MTL(Material.BLUE_SHULKER_BOX),
    
    QB(Material.MAGENTA_SHULKER_BOX),
    
    QEF(Material.PURPLE_SHULKER_BOX),
    
    SCHEMATIC(Material.ORANGE_SHULKER_BOX),
    
    STL(Material.LIME_SHULKER_BOX),
    
    WAVEFRONT(Material.LIGHT_BLUE_SHULKER_BOX),
    
    RESOURCE_PACK(Material.CYAN_SHULKER_BOX);
    
    private final Material material;
    
    FileBrowserEntryType(Material material) {
        this.material = material;
    }
    
    @Contract(pure = true)
    public Material material() {
        return material;
    }
    
    public static FileBrowserEntryType fromPath(String path) {
        if (path.startsWith("#"))
            return VARIABLE;
        else if (path.endsWith("/"))
            return DIRECTORY;
        else {
            String ext = CommandUtil.extensionOf(path);
            if (ext == null) return FILE;
            else switch (ext.toLowerCase()) {
                case "colors": return COLORS;
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
