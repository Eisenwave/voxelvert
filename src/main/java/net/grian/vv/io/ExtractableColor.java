package net.grian.vv.io;

import net.grian.vv.core.BlockKey;
import net.grian.vv.util.ColorMath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractableColor {

    private final BlockKey block;
    private final RGBExtractor extractor;
    private final int voxels;
    private final boolean tint;

    private ExtractableColor(BlockKey block, RGBExtractor extractor, int voxels, boolean tint) {
        this.block = block;
        this.extractor = extractor;
        this.voxels = voxels;
        this.tint = tint;
    }

    public ExtractableColor(BlockKey block, int rgb, int voxels, boolean tint) {
        this(block, new ConstantRGBExtractor(rgb), voxels, tint);
    }

    public ExtractableColor(BlockKey block, String texturePath, int voxels, boolean tint) {
        this(block, new TextureColorRGBExtractor(texturePath), voxels, tint);
    }

    public BlockKey getBlock() {
        return block;
    }

    public RGBExtractor getExtractor() {
        return extractor;
    }

    public int getVoxels() {
        return voxels;
    }

    public boolean hasTint() {
        return tint;
    }

    @Override
    public String toString() {
        return ExtractableColor.class.getSimpleName()+"{block="+getBlock()+"}";
    }

    private static BufferedImage readImage(ZipFile zip, ZipEntry entry) throws IOException {
        InputStream stream = zip.getInputStream(entry);
        BufferedImage image = ImageIO.read(stream);
        stream.close();
        return image;
    }

    @FunctionalInterface
    public static interface RGBExtractor {

        public int extract(ZipFile zip) throws IOException;

    }

    /**
     * An extractor which returns the same rgb value every time.
     */
    private static class ConstantRGBExtractor implements RGBExtractor {

        private final int rgb;

        private ConstantRGBExtractor(int rgb) {
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
    private static class TextureColorRGBExtractor implements RGBExtractor {

        private final String name;

        private TextureColorRGBExtractor(String name) {
            this.name = name;
        }

        @Override
        public int extract(ZipFile zip) throws IOException {
            ZipEntry entry = zip.getEntry("assets/minecraft/textures/" + name);
            if (entry == null) throw new IOException("entry not found: " + name);

            BufferedImage image = readImage(zip, entry);
            int[] sum = new int[4];
            int count = 0;

            final int width = image.getWidth(), height = image.getHeight();
            for (int u = 0; u < width; u++)
                for (int v = 0; v < height; v++) {
                    int rgb = image.getRGB(u, v);
                    if (ColorMath.isInvisible(rgb)) continue;
                    int[] color = ColorMath.split(rgb);

                    for (int i = 0; i < color.length; i++)
                        sum[i] += color[i];

                    count++;
                }

            return count == 0 ?
                    ColorMath.INVISIBLE_WHITE :
                    ColorMath.fromRGB(sum[1] / count, sum[2] / count, sum[3] / count, sum[0] / count);
        }

    }

}
