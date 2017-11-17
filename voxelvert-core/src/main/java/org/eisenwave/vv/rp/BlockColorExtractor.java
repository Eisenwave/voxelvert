package org.eisenwave.vv.rp;

import net.grian.torrens.img.Texture;
import net.grian.torrens.object.Rectangle4i;
import net.grian.torrens.schematic.BlockKey;
import net.grian.torrens.util.ColorMath;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A resource pack block color extractor.
 * <p>
 * This class stores instructions for extracting colors out of a resource pack, such as "use the average color of the
 * texture named xyz.png".
 * <p>
 * Once filled with information, it can be applied to a ZIP file out of which it gets the colors.
 */
public class BlockColorExtractor {
    
    private final Map<BlockKey, ExtractableColor> blockColors = new LinkedHashMap<>();
    
    // GETTERS & OPERATIONS
    
    public int size() {
        return blockColors.size();
    }
    
    public BlockColorTable extract(ZipFile zip) {
        //logger.info("converting rp to color map using "+colors.length+" colors");
        
        String name = zip.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        BlockColorTable result = new BlockColorTable();
        
        forEach((block, color) -> {
            int rgb;
            try {
                rgb = color.getStrategy().extract(zip);
            } catch (IOException ex) {
                return;
            }
            BlockColorMeta meta = color.getMeta();
            
            //TODO bake tint
            result.put(block, new BlockColor(rgb, meta.getVoxels()));
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
    public void put(@NotNull BlockKey block, @NotNull BlockColorMeta meta, int rgb) {
        ExtractStrategy strategy = new ConstantExtractStrategy(rgb);
        blockColors.put(block, new ExtractableColor(strategy, meta));
    }
    
    /**
     * Adds a block and a texture color extractor.
     *
     * @param block the block
     */
    public void put(@NotNull BlockKey block, @NotNull BlockColorMeta meta, String texturePath) {
        ExtractStrategy strategy = new TextureExtractStrategy(texturePath);
        blockColors.put(block, new ExtractableColor(strategy, meta));
    }
    
    /**
     * Adds a block and a texture area color extractor.
     *
     * @param block the block
     */
    public void put(@NotNull BlockKey block, @NotNull BlockColorMeta meta, String texturePath, Rectangle4i area) {
        ExtractStrategy strategy = new TextureAreaExtractStrategy(texturePath, area);
        blockColors.put(block, new ExtractableColor(strategy, meta));
    }
    
    public void clear() {
        blockColors.clear();
    }
    
    // ITERATION
    
    public void forEach(BiConsumer<BlockKey, ExtractableColor> action) {
        blockColors.forEach(action);
    }
    
    // EXTRACTION
    
    private final class ExtractableColor {
        
        private final ExtractStrategy strategy;
        private BlockColorMeta meta;
        
        private ExtractableColor(@NotNull ExtractStrategy strategy, @NotNull BlockColorMeta meta) {
            this.strategy = strategy;
            this.meta = meta;
        }
        
        public ExtractStrategy getStrategy() {
            return strategy;
        }
        
        public BlockColorMeta getMeta() {
            return meta;
        }
    }
    
    /**
     * A strategy for extracting a single color from a given {@link ZipFile}.
     */
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
