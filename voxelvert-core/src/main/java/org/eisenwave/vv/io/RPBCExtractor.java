package org.eisenwave.vv.io;

import net.grian.torrens.img.Texture;
import net.grian.torrens.object.Rectangle4i;
import net.grian.torrens.schematic.BlockKey;
import net.grian.torrens.util.ColorMath;
import org.eisenwave.vv.object.BlockColor;
import org.eisenwave.vv.object.ColorMap;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.eisenwave.vv.object.BlockColor.*;

/**
 * A resource pack block color extractor.
 * <p>
 * This class stores instructions for extracting colors out of a resource pack, such as "use the average color of the
 * texture named xyz.png".
 * <p>
 * Once filled with information, it can be applied to a ZIP file out of which it gets the colors.
 */
public class RPBCExtractor {
    
    private final Map<BlockKey, ExtractableColor> blockColors = new HashMap<>();
    
    // GETTERS & OPERATIONS
    
    public int size() {
        return blockColors.size();
    }
    
    public ColorMap extract(ZipFile zip) {
        //logger.info("converting rp to color map using "+colors.length+" colors");
        
        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        ColorMap result = new ColorMap();
        
        forEach((block, color) -> {
            int rgb;
            try {
                rgb = color.getExtractor().extract(zip);
            } catch (IOException ex) {
                return;
            }
            
            result.put(block, new BlockColor(rgb, color.getVoxels() / 4096F, color.getTint()));
        });
        
        return result;
    }
    
    // PREDICATES
    
    public boolean isEmpty() {
        return blockColors.isEmpty();
    }
    
    // MUTATORS
    
    /**
     * Adds a block and a constant color extractor.
     *
     * @param block the block
     */
    public void put(@NotNull BlockKey block, int rgb, int voxels, int tint) {
        ExtractStrategy strategy = new ConstantExtractStrategy(rgb);
        blockColors.put(block, new ExtractableColor(strategy, voxels, tint));
    }
    
    /**
     * Adds a block and a texture color extractor.
     *
     * @param block the block
     */
    public void put(@NotNull BlockKey block, String texturePath, int voxels, int tint) {
        ExtractStrategy strategy = new TextureExtractStrategy(texturePath);
        blockColors.put(block, new ExtractableColor(strategy, voxels, tint));
    }
    
    /**
     * Adds a block and a texture area color extractor.
     *
     * @param block the block
     */
    public void put(@NotNull BlockKey block, String texturePath, Rectangle4i area, int voxels, int tint) {
        ExtractStrategy strategy = new TextureAreaExtractStrategy(texturePath, area);
        blockColors.put(block, new ExtractableColor(strategy, voxels, tint));
    }
    
    public void clear() {
        blockColors.clear();
    }
    
    // ITERATION
    
    public void forEach(BiConsumer<BlockKey, ExtractableColor> action) {
        blockColors.forEach(action);
    }
    
    
    private final class ExtractableColor {
        
        private final ExtractStrategy extractor;
        private final int voxels;
        private final int tint;
        
        private ExtractableColor(@NotNull ExtractStrategy extractor, int voxels, int tint) {
            if (voxels < 0 || voxels > 4096)
                throw new IllegalArgumentException("amount of voxels must be in range(0,4096)");
            if (tint < TINT_NONE || tint > TINT_FOLIAGE)
                throw new IllegalArgumentException("tint type must be in range(0,2)");
            
            this.extractor = extractor;
            this.voxels = voxels;
            this.tint = tint;
        }
        
        public ExtractStrategy getExtractor() {
            return extractor;
        }
        
        public int getVoxels() {
            return voxels;
        }
        
        public boolean hasTint() {
            return tint != TINT_NONE;
        }
        
        public int getTint() {
            return tint;
        }
        
    }
    
    @FunctionalInterface
    public static interface ExtractStrategy {
        
        public int extract(ZipFile zip) throws IOException;
        
    }
    
    /**
     * An extractor which returns the same rgb value every time (necessary for some blocks such as air,
     * structure-void, etc.
     */
    private static class ConstantExtractStrategy implements ExtractStrategy {
        
        private final int rgb;
        
        private ConstantExtractStrategy(int rgb) {
            this.rgb = rgb;
        }
        
        @Override
        public int extract(ZipFile zip) throws IOException {
            return rgb;
        }
    }
    
    /**
     * A color extractor which measures the average color of a texture of choice inside the resource pack.
     */
    private static class TextureExtractStrategy implements ExtractStrategy {
        
        private final String name;
        
        private TextureExtractStrategy(String name) {
            this.name = name;
        }
        
        @Override
        public int extract(ZipFile zip) throws IOException {
            ZipEntry entry = zip.getEntry("assets/minecraft/textures/" + name);
            if (entry == null) throw new IOException("entry not found: " + name);
            
            Texture image = readImage(zip, entry);
            return averageRGB(image, 0, 0, image.getWidth() - 1, image.getHeight() - 1);
        }
        
    }
    
    /**
     * A color extractor which measures the average color of a texture of choice inside the resource pack.
     */
    private static class TextureAreaExtractStrategy implements ExtractStrategy {
        
        private final String name;
        private final Rectangle4i area;
        
        private TextureAreaExtractStrategy(String name, Rectangle4i area) {
            this.name = name;
            this.area = area;
        }
        
        @Override
        public int extract(ZipFile zip) throws IOException {
            ZipEntry entry = zip.getEntry("assets/minecraft/textures/" + name);
            if (entry == null) throw new IOException("entry not found: " + name);
            
            Texture image = readImage(zip, entry);
            return averageRGB(image, area.getMinX(), area.getMinY(), area.getMaxX(), area.getMaxY());
        }
        
    }
    
    // UTIL
    
    private static int averageRGB(Texture texture, int minX, int minY, int maxX, int maxY) {
        long[] sum = new long[4];
        long count = 0;
        
        for (int u = minX; u <= maxX; u++)
            for (int v = minY; v <= maxY; v++) {
                int rgb = texture.get(u, v);
                if (ColorMath.isInvisible(rgb)) continue;
                int[] color = ColorMath.argb(rgb);
                
                for (int i = 0; i < color.length; i++)
                    sum[i] += color[i];
                
                count++;
            }
        
        if (count == 0)
            return ColorMath.INVISIBLE_WHITE;
        
        for (int i = 0; i < sum.length; i++)
            sum[i] /= count;
        
        return ColorMath.fromRGB((int) sum[1], (int) sum[2], (int) sum[3], (int) sum[0]);
    }
    
    private static Texture readImage(ZipFile zip, ZipEntry entry) throws IOException {
        try (InputStream stream = zip.getInputStream(entry)) {
            return Texture.wrapOrCopy(ImageIO.read(stream));
        }
    }
    
}
