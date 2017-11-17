package org.eisenwave.vv.rp;

import org.jetbrains.annotations.NotNull;

public class BlockColorMeta {
    
    private final short voxels;
    private final Tint tint;
    private final int tintRGB;
    
    public BlockColorMeta(short voxels, @NotNull Tint tint, int tintRGB) {
        if (voxels < 0 || voxels > 4096)
            throw new IllegalArgumentException("amount of voxels must be in range(0,4096)");
        this.voxels = voxels;
        this.tint = tint;
        this.tintRGB = tintRGB;
    }
    
    public BlockColorMeta(short voxels, @NotNull Tint tint) {
        if (voxels < 0 || voxels > 4096)
            throw new IllegalArgumentException("amount of voxels must be in range(0,4096)");
        if (tint == Tint.CONSTANT)
            throw new IllegalArgumentException("must provide tint rgb for constant tint");
        this.voxels = voxels;
        this.tint = tint;
        this.tintRGB = 0;
    }
    
    /**
     * Returns the volume in voxels that make up a block.
     * <p>
     * The returned value is in range 0 - 4096.
     *
     * @return the volume in voxels
     */
    public short getVoxels() {
        return voxels;
    }
    
    /**
     * Returns the tint of this meta.
     *
     * @return the tint
     */
    public Tint getTint() {
        return tint;
    }
    
    /**
     * Returns an ARGB int representing the tint color.
     * <p>
     * This value only has meaning if {@link #getTint()} returns {@link Tint#CONSTANT}. Otherwise this value is
     * typically 0.
     *
     * @return the tint RGB
     */
    public int getTintRGB() {
        return tintRGB;
    }
    
}
