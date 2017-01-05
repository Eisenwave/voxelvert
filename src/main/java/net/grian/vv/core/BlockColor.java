package net.grian.vv.core;

import net.grian.vv.util.RGBValue;

public class BlockColor implements RGBValue {

    private final int rgb;
    private final float occupation;
    private final boolean tint;

    public BlockColor(int rgb, float occupation, boolean tint) {
        if (occupation < 0 || occupation > 1)
            throw new IllegalArgumentException("occupation must be 0-1 ("+occupation+")");
        this.rgb = rgb;
        this.occupation = occupation;
        this.tint = tint;
    }

    public BlockColor(int rgb, float occupation) {
        this(rgb, occupation, false);
    }

    public BlockColor(int rgb, boolean tint) {
        this(rgb, 1, tint);
    }

    public BlockColor(int rgb) {
        this(rgb, 1, false);
    }

    @Override
    public int getRGB() {
        return rgb;
    }

    /**
     * <p>
     *     Returns the space occupation of the block color.
     * </p>
     * <p>
     *     This is the amount of space the block occupies (0.0 - 1.0). A full block has an occupation of 1.0, a half
     *     slab has an occupation of 0.5, air has an occupation of 0.0 for example.
     * </p>
     *
     * @return the space occupation of the block color
     */
    public float getOccupation() {
        return occupation;
    }

    /**
     * Returns whether the block color is to be tinted by the biome a block is placed in.
     *
     * @return whether the color is to be tinted
     */
     public boolean hasTint() {
         return tint;
     }

}
