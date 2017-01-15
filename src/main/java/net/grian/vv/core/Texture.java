package net.grian.vv.core;

import java.io.Serializable;

/**
 * <p>
 *     A basic image format using a two-dimensional array of ARGB integers to represent an image.
 * </p>
 */
public class Texture implements Serializable, BaseTexture {

    private final int[][] content;

    final int width, height;

    /**
     * Constructs a new texture with a given width and height.
     *
     * @param width the texture width
     * @param height the texture height
     */
    public Texture(int width, int height) {
        if (width < 1) throw new IllegalArgumentException("width < 1");
        if (height < 1) throw new IllegalArgumentException("height < 1");

        this.content = new int[width][height];
        this.width = width;
        this.height = height;
    }

    /*
    public void setCapacity(int width, int height) {
        if (width < getWidth() || height < getHeight())
            throw new IllegalArgumentException("can not decrease capacity");
        int[][] oldContent = this.content;
        this.content = new int[width][height];
        internalPaste(oldContent, oldContent.length, oldContent[0].length, 0, 0);
    }

    public void addCapacity(int width, int height) {
        if (width < 0 || height < 0) throw new IllegalArgumentException("can not decrease capacity");
        setCapacity(getWidth() + width, getHeight() + height);
    }
    */

    /**
     * Returns new graphics for this texture.
     *
     * @return the texture graphics
     */
    public TextureGraphics getGraphics() {
        return new TextureGraphics(this);
    }

    public void paste(BaseTexture texture, final int u, final int v) {
        final int w = texture.getWidth(), h = texture.getHeight();
        if (w + u > this.getWidth())
            throw new IllegalArgumentException("texture width out of bounds");
        if (h + v > this.getHeight())
            throw new IllegalArgumentException("texture height out of bounds");

        if (texture instanceof Texture)
            internalPaste(((Texture) texture).content, w, h, u, v);
        else
            internalPaste(texture, w, h, u, v);
    }

    public void paste(Texture texture) {
        paste(texture, 0, 0);
    }

    private void internalPaste(BaseTexture content, int w, int h, int u, int v) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                this.content[i + u][j + v] = content.get(i, j);
    }

    private void internalPaste(int[][] content, int w, int h, int u, int v) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                this.content[i + u][j + v] = content[i][j];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int get(int u, int v) {
        return content[u][v];
    }

    @Override
    public void set(int u, int v, int rgb) {
        content[u][v] = rgb;
    }

}
