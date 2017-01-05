package net.grian.vv.core;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.AxisAlignedBB;
import net.grian.spatium.util.Flags;

public class TexturedBox {

    private final AxisAlignedBB shape;
    private final Rectangle[] uv = new Rectangle[Direction.values().length];

    //every tile visible by default
    private byte flags = 0b00_111111;

    public TexturedBox(AxisAlignedBB shape) {
        this.shape = shape.clone();
    }

    /**
     * Returns the uv of the bounding of a certain side.
     *
     * @param d the direction
     * @return the tile or null if the element has no texture on that side
     */
    public Rectangle getUV(Direction d) {
        return uv[d.ordinal()];
    }

    public AxisAlignedBB getShape() {
        return shape;
    }

    //CHECKERS

    /**
     * Returns whether the element has any visible side and is thus visible.
     *
     * @return whether the element is visible
     */
    public boolean isVisible() {
        return flags != 0;
    }

    /**
     * Returns whether a side of the element is enabled.
     *
     * @param side the side of the element
     * @return whether a side of the element is enabled
     */
    public boolean isSideEnabled(Direction side) {
        return Flags.get(flags, side.ordinal());
    }

    //SETTERS

    /**
     * Enables visibility for a side of the element.
     *
     * @param side the the side to be enabled
     */
    public void enableSide(Direction side) {
        flags = Flags.enable(flags, side.ordinal());
    }

    /**
     * Enables visibility for a side of the element.
     *
     * @param side the the side to be enabled
     */
    public void disableSide(Direction side) {
        flags = Flags.disable(flags, side.ordinal());
    }

    public void setUV(Direction side, Rectangle uv) {
        this.uv[side.ordinal()] = uv;
    }

}
