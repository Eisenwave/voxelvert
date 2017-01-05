package net.grian.vv.core;

import java.io.Serializable;

/**
 * <p>
 *     A basic image format using a two-dimensional array of ARGB integers to represent an image.
 * </p>
 * <p>
 *     The size or capacity of the image is variable and can be adjusted using {@link #setCapacity(int, int)} and
 *     {@link #addCapacity(int, int)}, although the image can not decrease in size through this process.
 * </p>
 */
public class Texture implements Serializable, BaseTexture {

    private int[][] content = new int[1][1];

    public Texture(int width, int height) {
        setCapacity(width, height);
    }

    public Texture() {}

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

    public void paste(Texture texture, final int u, final int v) {
        final int w = texture.getWidth(), h = texture.getHeight();
        if (w + u > this.getWidth())
            throw new IllegalArgumentException("texture width out of bounds");
        if (h + v > this.getHeight())
            throw new IllegalArgumentException("texture height out of bounds");

        internalPaste(texture.content, w, h, u, v);
    }

    public void paste(Texture texture) {
        paste(texture, 0, 0);
    }

    private void internalPaste(int[][] content, int w, int h, int u, int v) {
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                this.content[i + u][j + v] = content[i][j];
    }

    public int getWidth() {
        return content.length;
    }

    public int getHeight() {
        return content[0].length;
    }

    public int get(int u, int v) {
        return content[u][v];
    }

    public void set(int u, int v, int rgb) {
        content[u][v] = rgb;
    }

    @Deprecated
    public static class Tile {

        private int au, av, bu, bv;

        public Tile(int au, int av, int bu, int bv) {
            this.au = au;
            this.av = av;
            this.bu = bu;
            this.bv = bv;
        }

        public int getWidth() {
            return Math.abs(au - bu);
        }

        public int getHeight() {
            return Math.abs(av - bv);
        }

        public int getAU() {
            return au;
        }

        public int getAV() {
            return av;
        }

        public int getBU() {
            return bu;
        }

        public int getBV() {
            return bv;
        }

        public void setAU(int au) {
            this.au = au;
        }

        public void setAV(int av) {
            this.av = av;
        }

        public void setBU(int bu) {
            this.bu = bu;
        }

        public void setBV(int bv) {
            this.bv = bv;
        }

    }

}
