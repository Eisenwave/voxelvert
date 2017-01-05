package net.grian.vv.cache;

import org.bukkit.Material;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorMap {

    private final String name;
    private final Map<Material, Color> colorByMaterial = new HashMap<>();
    private final Map<Color, Material> materialByColor = new HashMap<>();

    public ColorMap(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void put(Material material, Color color) {
        colorByMaterial.put(material, color);
        materialByColor.put(color, material);
    }

    /**
     * Returns the color corresponding to the given material. Returns null if there is none.
     *
     * @param material the material
     * @return the color of the material
     */
    public Color getColor(Material material) {
        return colorByMaterial.get(material);
    }

    /**
     * Returns the material corresponding to the given color. Returns null if there is none.
     *
     * @param color the color
     * @return the material of the color
     */
    public Material getMaterial(Color color) {
        return materialByColor.get(color);
    }

}
