package net.grian.vv.core;

/**
 * Object dedicated to drawing in {@link BaseTexture} objects.
 */
public class TextureGraphics {

    private final BaseTexture texture;
    private final int width, height;

    public TextureGraphics(BaseTexture texture) {
        this.texture = texture;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    /**
     * Fills the texture with a single color.
     *
     * @param rgb the color
     */
    public void fill(int rgb) {
        for (int u = 0; u<width; u++)
            for (int v = 0; v<height; v++)
                texture.set(u, v, rgb);
    }

    public void drawRaster(int a, int b) {
        for (int u = 0; u<width; u++)
            for (int v = 0; v<height; v++)
                texture.set(u, v, u%2 == v%2? a : b);
    }

    public void drawRaster(int a, int b, int tileSize) {
        if (tileSize < 1) return;
        for (int u = 0; u<width; u++)
            for (int v = 0; v<height; v++)
                texture.set(u, v, u/tileSize%2 == v/tileSize%2? a : b);
    }

    /**
     * Draws a rectangle inside the texture.
     *
     * @param rgb the color
     * @param minU the min u-coordinate
     * @param minV the min v-coordinate
     * @param maxU the max u-coordinate
     * @param maxV the max v-coordinate
     */
    public void drawRectangle(int rgb, int minU, int minV, int maxU, int maxV) {
        final int
                limU = Math.min(maxU+1, width),
                limV = Math.min(maxV+1, height);
        for (int u = Math.max(0,minU); u<limU; u++)
            for (int v = Math.max(0,minV); v<limV; v++)
                texture.set(u, v, rgb);
    }

}
