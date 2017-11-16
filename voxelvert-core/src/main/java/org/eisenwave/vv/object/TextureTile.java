package org.eisenwave.vv.object;

import net.grian.torrens.img.BaseTexture;

public class TextureTile implements BaseTexture {
    
    private final BaseTexture texture;
    
    private final int width, height;
    private int u, v;
    
    public TextureTile(BaseTexture texture, int width, int height, int u, int v) {
        if (texture == null)
            throw new IllegalArgumentException("tile needs a nonnull texture");
        if (width < 1)
            throw new IllegalArgumentException("width must be at least 1");
        if (height < 1)
            throw new IllegalArgumentException("height must be at least 1");
        if (u < 0)
            throw new IllegalArgumentException("negative u-offset (" + u + ")");
        if (v < 0)
            throw new IllegalArgumentException("negative v-offset (" + v + ")");
        if (u + width > texture.getWidth())
            throw new IllegalArgumentException("tile exceeds texture bounds (u-max=" + (u + width - 1));
        if (v + height > texture.getHeight())
            throw new IllegalArgumentException("tile exceeds texture bounds (v-max=" + (v + width - 1));
        
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
    }
    
    public TextureTile(BaseTexture texture, int width, int height) {
        this(texture, width, height, 0, 0);
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
        return texture.get(this.u + u, this.v + v);
    }
    
    @Override
    public void set(int u, int v, int rgb) {
        texture.set(this.u + u, this.v + v, rgb);
    }
    
    /**
     * Returns the u value tile's offset from the origin of the texture it is placed in.
     *
     * @return the tile's u-coordinate
     */
    public int getU() {
        return u;
    }
    
    /**
     * Returns the v value tile's offset from the origin of the texture it is placed in.
     *
     * @return the tile's v-coordinate
     */
    public int getV() {
        return v;
    }
    
    /**
     * Returns the texture that this tile is placed in.
     *
     * @return this tile's texture
     */
    public BaseTexture getTexture() {
        return texture;
    }
    
}
