package eisenwave.vv.rp;

import eisenwave.torrens.util.RGBValue;

public class BlockColor implements RGBValue {
    
    public final static int
        FLAG_VOLATILE = 0b1,
        FLAG_TRANSPARENT = 0b10,
        FLAG_PHYSICS = 0b100;
    
    public final static short MAX_VOLUME = 4096;
    
    /*
    public final static int
        TINT_NONE = 0,
        TINT_GRASS = 1,
        TINT_FOLIAGE = 2;
    */
    
    private final int rgb;
    private final int flags;
    private final short volume;
    private final float perVol;
    private final boolean legacy;
    
    private BlockColor(int rgb, int flags, float volume, boolean legacy) {
        if (volume < 0 || volume > 1)
            throw new IllegalArgumentException("volume must be in range(0,1) (" + volume + ")");
        this.rgb = rgb;
        this.flags = flags;
        this.volume = (short) (volume * 4096);
        this.perVol = (float) Math.cbrt(volume);
        this.legacy = legacy;
    }
    
    public BlockColor(int rgb, int flags, short volume, boolean legacy) {
        this.rgb = rgb;
        this.flags = flags;
        this.volume = volume;
        this.perVol = (float) Math.cbrt(volume / 4096F);
        this.legacy = legacy;
    }
    
    public BlockColor(int rgb, int flags, boolean legacy) {
        this.rgb = rgb;
        this.flags = flags;
        this.volume = 4096;
        this.perVol = 1F;
        this.legacy = legacy;
    }
    
    public int getFlags() {
        return flags;
    }
    
    public boolean isLegacy() {
        return legacy;
    }
    
    /**
     * Returns whether this black is volatile. If so, the block may be destroyed if not placed correctly, such
     * as a sapling or a pressure plate disappearing if they're not placed on a full block.
     *
     * @return whether this block is volatile
     */
    public boolean isVolatile() {
        return (flags & FLAG_VOLATILE) != 0;
    }
    
    /**
     * Returns whether this block is transparent.
     *
     * @return whether this block is transparent.
     */
    public boolean isTransparent() {
        return (flags & FLAG_TRANSPARENT) != 0;
    }
    
    /**
     * Returns whether this black is physics-affected.
     *
     * @return whether this block is physics-affected
     */
    public boolean isPhysicsAffected() {
        return (flags & FLAG_PHYSICS) != 0;
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
     * Returns whether this block is a whole block, meaning that it's not a layer of snow, a slab or another block
     * which does not fill the complete volume of a block.
     *
     * @return whether the block is whole
     */
    public boolean isWhole() {
        return this.volume == MAX_VOLUME;
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
