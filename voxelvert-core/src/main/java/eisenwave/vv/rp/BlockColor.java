package eisenwave.vv.rp;

import eisenwave.torrens.util.RGBValue;

public class BlockColor implements RGBValue {
    
    public final static int
        TINT_NONE = 0,
        TINT_GRASS = 1,
        TINT_FOLIAGE = 2;
    
    private final int rgb;
    private final short volume;
    private final float perVol;
    
    public BlockColor(int rgb, float volume) {
        if (volume < 0 || volume > 1)
            throw new IllegalArgumentException("volume must be in range(0,1) (" + volume + ")");
        this.rgb = rgb;
        this.volume = (short) (volume * 4096);
        this.perVol = (float) Math.cbrt(volume);
    }
    
    public BlockColor(int rgb, short volume) {
        this.rgb = rgb;
        this.volume = volume;
        this.perVol = (float) Math.cbrt(volume / 4096F);
    }
    
    public BlockColor(int rgb) {
        this.rgb = rgb;
        this.volume = 4096;
        this.perVol = 1F;
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
        return volume / 4096F;
    }
    
    /**
     * Returns the volume in voxels (0 - 4096).
     *
     * @return the volume in voxels
     */
    public short getVoxelVolume() {
        return volume;
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
    
    /*
     * Returns the type of tint this block has.
     *
     * @return the type of tint the block has
     *
    public int getTint() {
        return tint;
    }
    */
    
}
