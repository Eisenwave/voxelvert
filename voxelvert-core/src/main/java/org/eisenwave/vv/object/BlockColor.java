package org.eisenwave.vv.object;

import net.grian.torrens.util.RGBValue;

public class BlockColor implements RGBValue {
    
    public final static int
        TINT_NONE = 0,
        TINT_GRASS = 1,
        TINT_FOLIAGE = 2;
    
    private final int rgb;
    private final float relVol, perVol;
    private final int tint;
    
    public BlockColor(int rgb, float vol, int tint) {
        if (vol < 0 || vol > 1)
            throw new IllegalArgumentException("volume must be 0-1 (" + vol + ")");
        this.rgb = rgb;
        this.relVol = vol;
        this.perVol = (float) Math.cbrt(vol);
        this.tint = tint;
    }
    
    public BlockColor(int rgb, float occupation) {
        this(rgb, occupation, TINT_NONE);
    }
    
    public BlockColor(int rgb, int tint) {
        this(rgb, 1, tint);
    }
    
    public BlockColor(int rgb) {
        this(rgb, 1, TINT_NONE);
    }
    
    @Override
    public int getRGB() {
        return rgb;
    }
    
    /**
     * Returns the relative volume of the block color.
     * <p>
     * This is the amount of space the block occupies (0.0 - 1.0). A full block has a volume of 1.0, a half
     * slab has a volume of 0.5, air has a volume of 0.0 etc.
     *
     * @return the space occupation of the block
     */
    public float getRelativeVolume() {
        return relVol;
    }
    
    /**
     * Returns the perceived volume of the block color.
     * <p>
     * While the relative volume describes more accurately how much space a block occupies, the perceived volume
     * occupation describes how much space the block visually occupies.
     * <p>
     * This, this value is ideal for direct multiplication with an alpha channel to make blocks such as carpets,
     * slabs, stairs, fences, etc. more transparent.
     *
     * @return the cube root of the occupation of the block
     * @see #getRelativeVolume()
     */
    public double getPerceivedVolume() {
        return perVol;
    }
    
    /**
     * Returns the type of tint this block has.
     *
     * @return the type of tint the block has
     */
    public int getTint() {
        return tint;
    }
    
}
